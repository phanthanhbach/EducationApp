package com.example.educationapp.presentation.screenmodel.invoice

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.util.asUiText
import com.example.educationapp.domain.entity.Invoice
import com.example.educationapp.domain.entity.PaymentQr
import com.example.educationapp.domain.usecase.GetInvoiceByIdUseCase
import com.example.educationapp.domain.usecase.GetMyInvoicesUseCase
import com.example.educationapp.domain.usecase.GetPaymentQrUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class ClassInvoicesScreenModel(
    private val classId: Int,
    private val studentId: Int,
    private val getMyInvoicesUseCase: GetMyInvoicesUseCase,
    private val getPaymentQrUseCase: GetPaymentQrUseCase,
    private val getInvoiceByIdUseCase: GetInvoiceByIdUseCase
) : ScreenModel {

    private val _state = MutableStateFlow<ClassInvoicesState>(ClassInvoicesState.Loading)
    val state: StateFlow<ClassInvoicesState> = _state.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _selectedStatus = MutableStateFlow<String?>(null) // PENDING, PARTIAL, PAID, OVERDUE, CANCELLED
    val selectedStatus: StateFlow<String?> = _selectedStatus.asStateFlow()

    private val _paidFilter = MutableStateFlow<Boolean?>(null) // true, false, null
    val paidFilter: StateFlow<Boolean?> = _paidFilter.asStateFlow()

    private val _paymentQrState = MutableStateFlow<PaymentQrState>(PaymentQrState.Idle)
    val paymentQrState: StateFlow<PaymentQrState> = _paymentQrState.asStateFlow()

    private var pollingJob: Job? = null

    private var loadJob: Job? = null

    init {
        launchLoadInvoices(page = 0, append = false)
    }

    fun filterByStatus(status: String?) {
        _selectedStatus.value = if (status == "ALL" || status.isNullOrEmpty()) null else status
        launchLoadInvoices(page = 0, append = false)
    }

    fun filterByPaid(paid: Boolean?) {
        _paidFilter.value = paid
        launchLoadInvoices(page = 0, append = false)
    }

    fun loadNextPage() {
        val currentState = _state.value
        if (currentState is ClassInvoicesState.Success && currentState.hasNextPage) {
            launchLoadInvoices(page = currentState.currentPage + 1, append = true)
        }
    }

    fun reload() {
        launchLoadInvoices(page = 0, append = false)
    }

    fun refreshData() {
        if (loadJob?.isActive == true) return
        loadJob?.cancel()
        loadJob = screenModelScope.launch {
            _isRefreshing.value = true
            loadInvoicesInternal(page = 0, append = false, silent = true)
            _isRefreshing.value = false
        }
    }

    fun requestPaymentQr(invoiceId: Int, installmentId: Int?) {
        _paymentQrState.value = PaymentQrState.Loading
        screenModelScope.launch {
            when (val result = getPaymentQrUseCase(invoiceId, installmentId)) {
                is ApiResult.Error -> {
                    _paymentQrState.value = PaymentQrState.Error(result.asUiText())
                }
                is ApiResult.Success -> {
                    _paymentQrState.value = PaymentQrState.Success(result.data)
                    startPollingInvoice(invoiceId)
                }
            }
        }
    }

    fun dismissPaymentQr() {
        stopPolling()
        _paymentQrState.value = PaymentQrState.Idle
    }

    fun dismissPaymentCompleted() {
        _paymentQrState.value = PaymentQrState.Idle
        // Reload invoices list to reflect updated payment status
        launchLoadInvoices(page = 0, append = false)
    }

    private fun startPollingInvoice(invoiceId: Int) {
        pollingJob?.cancel()
        pollingJob = screenModelScope.launch {
            while (true) {
                delay(5000L) // Poll every 5 seconds
                when (val result = getInvoiceByIdUseCase(invoiceId)) {
                    is ApiResult.Success -> {
                        val invoice = result.data
                        if (invoice.fullyPaid || invoice.paymentStatus.equals("PAID", ignoreCase = true)) {
                            _paymentQrState.value = PaymentQrState.PaymentCompleted(invoice)
                            stopPolling()
                            return@launch
                        }
                        // Check if partial payment happened — update QR state with latest data
                        val currentQrState = _paymentQrState.value
                        if (currentQrState is PaymentQrState.Success) {
                            val prevPaid = currentQrState.paymentQr.paidAmount
                            if (invoice.paidAmount > prevPaid) {
                                // Partial payment detected, update invoice list and show completed
                                _paymentQrState.value = PaymentQrState.PaymentCompleted(invoice)
                                stopPolling()
                                return@launch
                            }
                        }
                    }
                    is ApiResult.Error -> {
                        // Silently continue polling on error
                    }
                }
            }
        }
    }

    private fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    private fun launchLoadInvoices(page: Int, append: Boolean, silent: Boolean = false) {
        if (append && loadJob?.isActive == true) return

        if (!append) {
            loadJob?.cancel()
        }

        loadJob = screenModelScope.launch {
            loadInvoicesInternal(page, append, silent)
        }
    }

    private suspend fun loadInvoicesInternal(page: Int, append: Boolean, silent: Boolean = false) {
        if (!append && !silent) {
            _state.value = ClassInvoicesState.Loading
        }

        val result = getMyInvoicesUseCase(
            classId = classId,
            studentId = studentId,
            status = _selectedStatus.value,
            paid = _paidFilter.value,
            page = page,
            size = 20
        )

        when (result) {
            is ApiResult.Error -> {
                if (!append) {
                    _state.value = ClassInvoicesState.Error(result.asUiText())
                }
            }
            is ApiResult.Success -> {
                val pagination = result.data
                val newInvoices = pagination.content

                val currentInvoices = if (append && _state.value is ClassInvoicesState.Success) {
                    val existing = (_state.value as ClassInvoicesState.Success).invoices
                    (existing + newInvoices).distinctBy { it.id }
                } else {
                    newInvoices
                }

                _state.value = ClassInvoicesState.Success(
                    invoices = currentInvoices,
                    currentPage = pagination.number,
                    totalPages = pagination.totalPages,
                    totalElements = pagination.totalElements,
                    hasNextPage = !pagination.last && pagination.content.isNotEmpty()
                )
            }
        }
    }

    override fun onDispose() {
        stopPolling()
        super.onDispose()
    }
}
