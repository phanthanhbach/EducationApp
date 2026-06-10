package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.Invoice
import com.example.educationapp.domain.repository.InvoiceRepository

class GetInvoiceByIdUseCase(
    private val repository: InvoiceRepository
) {
    suspend operator fun invoke(invoiceId: Int): ApiResult<Invoice> {
        return repository.getInvoiceById(invoiceId)
    }
}
