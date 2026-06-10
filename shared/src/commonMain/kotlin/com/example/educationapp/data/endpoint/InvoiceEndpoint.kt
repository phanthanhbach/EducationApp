package com.example.educationapp.data.endpoint

object InvoiceEndpoint {
    const val ME = "invoices/me"
    fun byId(invoiceId: Int) = "invoices/$invoiceId"
    fun paymentQr(invoiceId: Int) = "invoices/$invoiceId/payment/qr"
}
