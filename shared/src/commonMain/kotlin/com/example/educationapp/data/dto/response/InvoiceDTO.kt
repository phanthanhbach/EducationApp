package com.example.educationapp.data.dto.response

import com.example.educationapp.domain.entity.Invoice
import com.example.educationapp.domain.entity.Installment
import com.example.educationapp.domain.entity.PaymentQr
import kotlinx.serialization.Serializable

@Serializable
data class InvoiceDTO(
    val id: Int,
    val studentId: Int,
    val studentName: String,
    val studentClassId: Int,
    val invoiceCode: String,
    val amount: Double,
    val discountAmount: Double,
    val finalAmount: Double,
    val paidAmount: Double,
    val remainingAmount: Double,
    val fullyPaid: Boolean,
    val paymentStatus: String,
    val paymentMethod: String? = null,
    val paidDate: String? = null,
    val dueDate: String? = null,
    val note: String? = null,
    val branchId: Int,
    val branchName: String,
    val createdAt: String,
    val installments: List<InstallmentDTO> = emptyList()
)

@Serializable
data class InstallmentDTO(
    val installmentId: Int,
    val sequenceNo: Int,
    val amount: Double,
    val dueDate: String? = null,
    val status: String,
    val paymentMethod: String? = null,
    val paidDate: String? = null
)

fun InvoiceDTO.toDomain() = Invoice(
    id = id.toLong(),
    studentId = studentId.toLong(),
    studentName = studentName,
    studentClassId = studentClassId.toLong(),
    invoiceCode = invoiceCode,
    amount = amount,
    discountAmount = discountAmount,
    finalAmount = finalAmount,
    paidAmount = paidAmount,
    remainingAmount = remainingAmount,
    fullyPaid = fullyPaid,
    paymentStatus = paymentStatus,
    paymentMethod = paymentMethod,
    paidDate = paidDate,
    dueDate = dueDate,
    note = note,
    branchId = branchId.toLong(),
    branchName = branchName,
    createdAt = createdAt,
    installments = installments.map { it.toDomain() }
)

fun InstallmentDTO.toDomain() = Installment(
    installmentId = installmentId.toLong(),
    sequenceNo = sequenceNo,
    amount = amount,
    dueDate = dueDate,
    status = status,
    paymentMethod = paymentMethod,
    paidDate = paidDate
)

@Serializable
data class PaymentQrDTO(
    val invoiceId: Int,
    val installmentId: Int? = null,
    val installmentSequenceNo: Int? = null,
    val invoiceCode: String,
    val paymentCode: String,
    val finalAmount: Double,
    val paidAmount: Double,
    val remainingAmount: Double,
    val paymentAmount: Double,
    val qrUrl: String
)

fun PaymentQrDTO.toDomain() = PaymentQr(
    invoiceId = invoiceId.toLong(),
    installmentId = installmentId?.toLong() ?: 0L,
    installmentSequenceNo = installmentSequenceNo ?: 0,
    invoiceCode = invoiceCode,
    paymentCode = paymentCode,
    finalAmount = finalAmount,
    paidAmount = paidAmount,
    remainingAmount = remainingAmount,
    paymentAmount = paymentAmount,
    qrUrl = qrUrl
)
