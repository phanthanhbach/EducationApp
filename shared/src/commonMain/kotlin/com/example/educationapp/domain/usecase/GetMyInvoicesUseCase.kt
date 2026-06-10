package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.network.PaginationResponse
import com.example.educationapp.domain.entity.Invoice
import com.example.educationapp.domain.repository.InvoiceRepository

class GetMyInvoicesUseCase(
    private val repository: InvoiceRepository
) {
    suspend operator fun invoke(
        classId: Int? = null,
        studentId: Int? = null,
        status: String? = null,
        paid: Boolean? = null,
        page: Int = 0,
        size: Int = 20
    ): ApiResult<PaginationResponse<Invoice>> {
        return repository.getMyInvoices(classId, studentId, status, paid, page, size)
    }
}
