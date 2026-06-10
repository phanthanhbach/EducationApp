package com.example.educationapp.domain.entity

data class Invoice(
    val id: Long,
    val studentId: Long,
    val studentName: String,
    val studentClassId: Long,
    val invoiceCode: String,
    val amount: Double,
    val discountAmount: Double,
    val finalAmount: Double,
    val paidAmount: Double,
    val remainingAmount: Double,
    val fullyPaid: Boolean,
    val paymentStatus: String,
    val paymentMethod: String?,
    val paidDate: String?,
    val dueDate: String?,
    val note: String?,
    val branchId: Long,
    val branchName: String,
    val createdAt: String,
    val installments: List<Installment>
)

data class Installment(
    val installmentId: Long,
    val sequenceNo: Int,
    val amount: Double,
    val dueDate: String?,
    val status: String,
    val paymentMethod: String?,
    val paidDate: String?
)

data class PaymentQr(
    val invoiceId: Long,
    val installmentId: Long,
    val installmentSequenceNo: Int,
    val invoiceCode: String,
    val paymentCode: String,
    val finalAmount: Double,
    val paidAmount: Double,
    val remainingAmount: Double,
    val paymentAmount: Double,
    val qrUrl: String
)
