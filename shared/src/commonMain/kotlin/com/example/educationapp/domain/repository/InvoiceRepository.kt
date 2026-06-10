package com.example.educationapp.domain.repository

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.network.PaginationResponse
import com.example.educationapp.domain.entity.Invoice
import com.example.educationapp.domain.entity.PaymentQr

interface InvoiceRepository {
    suspend fun getMyInvoices(
        classId: Int?,
        studentId: Int?,
        status: String?,
        paid: Boolean?,
        page: Int,
        size: Int
    ): ApiResult<PaginationResponse<Invoice>>

    suspend fun getInvoiceById(invoiceId: Int): ApiResult<Invoice>

    suspend fun getPaymentQr(invoiceId: Int, installmentId: Int?): ApiResult<PaymentQr>
}
