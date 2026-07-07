package com.example.educationapp.presentation.screenmodel.invoice

import com.example.educationapp.core.util.UiText
import com.example.educationapp.domain.entity.Invoice
import com.example.educationapp.domain.entity.PaymentQr

sealed interface ClassInvoicesState {
    object Loading : ClassInvoicesState
    data class Success(
        val invoices: List<Invoice>,
        val currentPage: Int,
        val totalPages: Int,
        val totalElements: Int,
        val hasNextPage: Boolean
    ) : ClassInvoicesState
    data class Error(val message: UiText) : ClassInvoicesState
}

sealed interface PaymentQrState {
    object Idle : PaymentQrState
    object Loading : PaymentQrState
    data class Success(val paymentQr: PaymentQr) : PaymentQrState
    data class Error(val message: UiText) : PaymentQrState
    data class PaymentCompleted(val invoice: Invoice) : PaymentQrState
}
