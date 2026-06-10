package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.PaymentQr
import com.example.educationapp.domain.repository.InvoiceRepository

class GetPaymentQrUseCase(
    private val repository: InvoiceRepository
) {
    suspend operator fun invoke(invoiceId: Int, installmentId: Int?): ApiResult<PaymentQr> {
        return repository.getPaymentQr(invoiceId, installmentId)
    }
}
