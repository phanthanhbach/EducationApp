package com.example.educationapp.presentation.screen.invoice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import com.example.educationapp.core.ui.shimmer.skeleton.InvoiceCardSkeleton
import com.example.educationapp.core.ui.layout.AppScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.chip.AppChip
import com.example.educationapp.core.ui.image.AppImage
import com.example.educationapp.core.ui.image.CoreMediaSource
import com.example.educationapp.core.ui.layout.AppTopBar
import com.example.educationapp.core.ui.sheet.AppBottomSheet
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.entity.Invoice
import com.example.educationapp.domain.entity.PaymentQr
import com.example.educationapp.presentation.screenmodel.invoice.ClassInvoicesScreenModel
import com.example.educationapp.presentation.screenmodel.invoice.ClassInvoicesState
import com.example.educationapp.presentation.screenmodel.invoice.PaymentQrState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.profile_retry
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf
import kotlin.time.Duration.Companion.milliseconds

class ClassInvoicesScreen(
    private val classId: Int,
    private val studentId: Int,
    private val className: String
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel =
            koinScreenModel<ClassInvoicesScreenModel> { parametersOf(classId, studentId) }
        val state by screenModel.state.collectAsState()
        val selectedStatus by screenModel.selectedStatus.collectAsState()
        val paymentQrState by screenModel.paymentQrState.collectAsState()
        val isRefreshing by screenModel.isRefreshing.collectAsState()

        AppScaffold(
            topBar = {
                AppTopBar(
                    onBackClick = { navigator.pop() },
                    titleContent = {
                        Column {
                            AppText(
                                text = "Hóa đơn học phí",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            AppText(
                                text = className,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
            },
            isRefreshing = isRefreshing,
            onRefresh = { screenModel.refreshData() }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Filter row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = AppDimen.p16, vertical = AppDimen.p8),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppChip(
                            text = "Tất cả",
                            selected = selectedStatus == null,
                            onClick = {
                                screenModel.filterByStatus(null)
                            }
                        )
                        AppChip(
                            text = "Chờ thanh toán",
                            selected = selectedStatus == "PENDING",
                            onClick = {
                                screenModel.filterByStatus("PENDING")
                            }
                        )
                        AppChip(
                            text = "Đã thanh toán",
                            selected = selectedStatus == "PAID",
                            onClick = {
                                screenModel.filterByStatus("PAID")
                            }
                        )
                        AppChip(
                            text = "Trễ hạn",
                            selected = selectedStatus == "OVERDUE",
                            onClick = {
                                screenModel.filterByStatus("OVERDUE")
                            }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        when (val invoiceState = state) {
                            is ClassInvoicesState.Loading -> {
                                InvoiceCardSkeleton(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    itemCount = 3
                                )
                            }

                            is ClassInvoicesState.Error -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(AppDimen.p16),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(
                                                alpha = 0.1f
                                            )
                                        ),
                                        border = BorderStroke(
                                            1.dp,
                                            AppColor.Error.copy(alpha = 0.3f)
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(AppDimen.p16),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            AppText(
                                                text = invoiceState.message,
                                                fontSize = 14.sp,
                                                color = AppColor.Error,
                                                textAlign = TextAlign.Center
                                            )
                                            Button(
                                                onClick = { screenModel.reload() },
                                                colors = ButtonDefaults.buttonColors(containerColor = AppColor.Primary)
                                            ) {
                                                AppText(
                                                    text = stringResource(Res.string.profile_retry),
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            is ClassInvoicesState.Success -> {
                                if (invoiceState.invoices.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(AppDimen.p16),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AppText(
                                            text = "Không có hóa đơn học phí nào.",
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                } else {
                                    val lazyListState = rememberLazyListState()

                                    LaunchedEffect(lazyListState) {
                                        snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo }
                                            .collect { visibleItems ->
                                                val lastVisibleItem = visibleItems.lastOrNull()
                                                if (lastVisibleItem != null && lastVisibleItem.index >= lazyListState.layoutInfo.totalItemsCount - 3) {
                                                    screenModel.loadNextPage()
                                                }
                                            }
                                    }

                                    LazyColumn(
                                        state = lazyListState,
                                        contentPadding = PaddingValues(
                                            start = AppDimen.p16,
                                            end = AppDimen.p16,
                                            top = AppDimen.p8,
                                            bottom = AppDimen.p24
                                        ),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        items(invoiceState.invoices, key = { it.id }) { invoice ->
                                            InvoiceCard(
                                                invoice = invoice,
                                                onPayClick = {
                                                    // Find the first unpaid installment
                                                    val unpaidInstallment =
                                                        invoice.installments.firstOrNull {
                                                            !it.status.equals(
                                                                "PAID",
                                                                ignoreCase = true
                                                            )
                                                        }
                                                    if (unpaidInstallment != null) {
                                                        screenModel.requestPaymentQr(
                                                            invoiceId = invoice.id.toInt(),
                                                            installmentId = unpaidInstallment.installmentId.toInt()
                                                        )
                                                    } else {
                                                        // No installments, use invoice id with installmentId = null
                                                        screenModel.requestPaymentQr(
                                                            invoiceId = invoice.id.toInt(),
                                                            installmentId = null
                                                        )
                                                    }
                                                }
                                            )
                                        }

                                        if (invoiceState.hasNextPage) {
                                            item {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = AppDimen.p16),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    CircularProgressIndicator(
                                                        modifier = Modifier.size(24.dp),
                                                        strokeWidth = 2.dp,
                                                        color = AppColor.Primary
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                }
            }
        }
    }

        // Payment QR Bottom Sheet
        when (val qrState = paymentQrState) {
            is PaymentQrState.Loading -> {
                AppBottomSheet(
                    onDismissRequest = { screenModel.dismissPaymentQr() }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(color = AppColor.Primary)
                            AppText(
                                text = "Đang tạo mã QR...",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            is PaymentQrState.Success -> {
                PaymentQrBottomSheet(
                    paymentQr = qrState.paymentQr,
                    onDismiss = { screenModel.dismissPaymentQr() }
                )
            }

            is PaymentQrState.Error -> {
                AppBottomSheet(
                    onDismissRequest = { screenModel.dismissPaymentQr() }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppDimen.p24),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AppText(
                            text = "Lỗi",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColor.Error
                        )
                        AppText(
                            text = qrState.message,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { screenModel.dismissPaymentQr() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AppColor.Primary)
                        ) {
                            AppText(
                                text = "Đóng",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            is PaymentQrState.PaymentCompleted -> {
                AppBottomSheet(
                    onDismissRequest = { screenModel.dismissPaymentCompleted() }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppDimen.p24),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(32.dp))
                                .background(Color(0xFF4CAF50).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            AppText(
                                text = "✓",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                        }
                        AppText(
                            text = "Thanh toán thành công!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                        AppText(
                            text = "Hóa đơn ${qrState.invoice.invoiceCode} đã được thanh toán.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            PaymentInfoRow(
                                label = "Tổng học phí",
                                value = formatCurrency(qrState.invoice.finalAmount)
                            )
                            PaymentInfoRow(
                                label = "Đã thanh toán",
                                value = formatCurrency(qrState.invoice.paidAmount),
                                valueColor = Color(0xFF4CAF50)
                            )
                            if (qrState.invoice.remainingAmount > 0) {
                                PaymentInfoRow(
                                    label = "Còn lại",
                                    value = formatCurrency(qrState.invoice.remainingAmount),
                                    valueColor = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { screenModel.dismissPaymentCompleted() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AppColor.Primary)
                        ) {
                            AppText(
                                text = "Hoàn tất",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            is PaymentQrState.Idle -> { /* No bottom sheet */
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun PaymentQrBottomSheet(
        paymentQr: PaymentQr,
        onDismiss: () -> Unit
    ) {
        AppBottomSheet(
            onDismissRequest = onDismiss
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppDimen.p24),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AppText(
                    text = "Quét mã QR để thanh toán",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                AppText(
                    text = paymentQr.invoiceCode,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // QR Code Image
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .aspectRatio(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AppImage(
                            source = CoreMediaSource.Url(paymentQr.qrUrl),
                            modifier = Modifier.fillMaxSize(),
                            contentDescription = "Mã QR thanh toán",
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                // Payment details
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PaymentInfoRow(
                        label = "Mã thanh toán",
                        value = paymentQr.paymentCode
                    )
                    if (paymentQr.installmentSequenceNo > 0) {
                        PaymentInfoRow(
                            label = "Đợt thanh toán",
                            value = "Đợt ${paymentQr.installmentSequenceNo}"
                        )
                    }
                    PaymentInfoRow(
                        label = "Số tiền thanh toán",
                        value = formatCurrency(paymentQr.paymentAmount),
                        valueColor = AppColor.Primary,
                        valueFontWeight = FontWeight.Bold
                    )
                    PaymentInfoRow(
                        label = "Tổng học phí",
                        value = formatCurrency(paymentQr.finalAmount)
                    )
                    PaymentInfoRow(
                        label = "Đã thanh toán",
                        value = formatCurrency(paymentQr.paidAmount)
                    )
                    PaymentInfoRow(
                        label = "Còn lại",
                        value = formatCurrency(paymentQr.remainingAmount),
                        valueColor = if (paymentQr.remainingAmount > 0) MaterialTheme.colorScheme.error else Color(
                            0xFF4CAF50
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Polling indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        strokeWidth = 2.dp,
                        color = AppColor.Primary
                    )
                    AppText(
                        text = "Đang chờ thanh toán...",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    AppText(
                        text = "Đóng",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    @Composable
    private fun InvoiceCard(
        invoice: Invoice,
        onPayClick: () -> Unit
    ) {
        val statusColor = when (invoice.paymentStatus.uppercase()) {
            "PAID" -> Color(0xFF4CAF50)
            "PENDING" -> Color(0xFFFF9800)
            "PARTIAL" -> Color(0xFF2196F3)
            "OVERDUE" -> Color(0xFFF44336)
            else -> Color(0xFF9E9E9E)
        }

        val statusText = when (invoice.paymentStatus.uppercase()) {
            "PAID" -> "Đã thanh toán"
            "PENDING" -> "Chờ thanh toán"
            "PARTIAL" -> "Thanh toán một phần"
            "OVERDUE" -> "Trễ hạn"
            else -> invoice.paymentStatus
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppDimen.p16),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppText(
                        text = invoice.invoiceCode,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(statusColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        AppText(
                            text = statusText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    InvoiceRowItem(
                        label = "Tổng học phí",
                        value = formatCurrency(invoice.finalAmount)
                    )
                    InvoiceRowItem(
                        label = "Đã thanh toán",
                        value = formatCurrency(invoice.paidAmount)
                    )
                    InvoiceRowItem(
                        label = "Còn lại",
                        value = formatCurrency(invoice.remainingAmount),
                        valueColor = if (invoice.remainingAmount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                        valueFontWeight = if (invoice.remainingAmount > 0) FontWeight.Bold else FontWeight.Normal
                    )
                    if (!invoice.dueDate.isNullOrBlank()) {
                        InvoiceRowItem(
                            label = "Hạn thanh toán",
                            value = formatDate(invoice.dueDate)
                        )
                    }
                }

                if (invoice.installments.isNotEmpty()) {
                    var showInstallments by remember { mutableStateOf(false) }
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showInstallments = !showInstallments }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppText(
                                text = "Chi tiết đợt đóng học phí",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            AppText(
                                text = if (showInstallments) "Ẩn" else "Hiện",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        if (showInstallments) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                invoice.installments.forEach { installment ->
                                    val instStatusColor = when (installment.status.uppercase()) {
                                        "PAID" -> Color(0xFF4CAF50)
                                        "PENDING" -> Color(0xFFFF9800)
                                        else -> Color(0xFF9E9E9E)
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        AppText(
                                            text = "Đợt ${installment.sequenceNo}: ${
                                                formatCurrency(
                                                    installment.amount
                                                )
                                            }",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        AppText(
                                            text = if (installment.status == "PAID") "Đã đóng" else "Chưa đóng",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = instStatusColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                val isPaid = invoice.paymentStatus.equals("PAID", ignoreCase = true)
                val isCancelled = invoice.paymentStatus.equals("CANCELLED", ignoreCase = true)
                if (!isPaid && !isCancelled) {
                    Button(
                        onClick = onPayClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        AppText(
                            text = "Thanh toán",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun PaymentInfoRow(
        label: String,
        value: String,
        valueColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        valueFontWeight: FontWeight = FontWeight.Normal
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppText(
                text = label,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
            AppText(
                text = value,
                fontSize = 13.sp,
                fontWeight = valueFontWeight,
                color = valueColor
            )
        }
    }

    @Composable
    private fun InvoiceRowItem(
        label: String,
        value: String,
        valueColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        valueFontWeight: FontWeight = FontWeight.Normal
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppText(
                text = label,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
            AppText(
                text = value,
                fontSize = 13.sp,
                fontWeight = valueFontWeight,
                color = valueColor
            )
        }
    }

    private fun formatCurrency(amount: Double): String {
        val value = amount.toLong()
        if (value == 0L) return "0đ"
        val builder = StringBuilder()
        var temp = value
        var count = 0
        while (temp > 0) {
            if (count > 0 && count % 3 == 0) {
                builder.append('.')
            }
            builder.append(temp % 10)
            temp /= 10
            count++
        }
        return builder.reverse().toString() + "đ"
    }

    private fun formatDate(dateStr: String?): String {
        if (dateStr.isNullOrBlank()) return ""
        val parts = dateStr.split('-')
        return if (parts.size == 3) {
            "${parts[2]}/${parts[1]}/${parts[0]}"
        } else {
            dateStr
        }
    }
}
