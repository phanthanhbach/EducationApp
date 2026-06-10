package com.example.educationapp.data.repository

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.network.BaseResponse
import com.example.educationapp.core.network.PaginationResponse
import com.example.educationapp.core.network.safeApiCall
import com.example.educationapp.data.dto.response.InvoiceDTO
import com.example.educationapp.data.dto.response.PaymentQrDTO
import com.example.educationapp.data.dto.response.toDomain
import com.example.educationapp.data.endpoint.InvoiceEndpoint
import com.example.educationapp.domain.entity.Invoice
import com.example.educationapp.domain.entity.PaymentQr
import com.example.educationapp.domain.repository.InvoiceRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class InvoiceRepositoryImpl(
    private val httpClient: HttpClient
) : InvoiceRepository {

    override suspend fun getMyInvoices(
        classId: Int?,
        studentId: Int?,
        status: String?,
        paid: Boolean?,
        page: Int,
        size: Int
    ): ApiResult<PaginationResponse<Invoice>> {
        return safeApiCall {
            val response = httpClient.get(InvoiceEndpoint.ME) {
                if (classId != null) parameter("classId", classId)
                if (studentId != null) parameter("studentId", studentId)
                if (status != null) parameter("status", status)
                if (paid != null) parameter("paid", paid)
                parameter("page", page)
                parameter("size", size)
            }.body<BaseResponse<PaginationResponse<InvoiceDTO>>>()

            val paginatedData = response.data
            PaginationResponse(
                content = paginatedData.content.map { it.toDomain() },
                number = paginatedData.number,
                size = paginatedData.size,
                totalElements = paginatedData.totalElements,
                totalPages = paginatedData.totalPages,
                last = paginatedData.last,
                first = paginatedData.first
            )
        }
    }

    override suspend fun getInvoiceById(invoiceId: Int): ApiResult<Invoice> {
        return safeApiCall {
            val response = httpClient.get(InvoiceEndpoint.byId(invoiceId))
                .body<BaseResponse<InvoiceDTO>>()
            response.data.toDomain()
        }
    }

    override suspend fun getPaymentQr(invoiceId: Int, installmentId: Int?): ApiResult<PaymentQr> {
        return safeApiCall {
            val response = httpClient.get(InvoiceEndpoint.paymentQr(invoiceId)) {
                if (installmentId != null) {
                    parameter("installmentId", installmentId)
                }
            }.body<BaseResponse<PaymentQrDTO>>()
            response.data.toDomain()
        }
    }
}
