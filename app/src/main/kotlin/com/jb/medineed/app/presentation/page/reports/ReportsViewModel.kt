package com.jb.medineed.app.presentation.page.reports

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.*
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.jb.medineed.app.data.local.entity.TransactionType
import com.jb.medineed.app.data.repository.MedicineRepository
import com.jb.medineed.app.domain.model.Medicine
import com.jb.medineed.app.domain.model.StockTransaction
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*
import kotlin.math.abs

enum class ReportPeriod(val label: String, val days: Long) {
    TODAY("Today", 1),
    THIS_WEEK("This Week", 7),
    THIS_MONTH("This Month", 30),
    THIS_QUARTER("3 Months", 90),
    THIS_YEAR("This Year", 365)
}

data class ReportsUiState(
    val period: ReportPeriod = ReportPeriod.THIS_MONTH,
    val totalSold: Int = 0,
    val totalSales: Double = 0.0,
    val outOfStockCount: Int = 0,
    val lowStockCount: Int = 0,
    val totalMedicines: Int = 0,
    val transactions: List<StockTransaction> = emptyList(),
    val outOfStockMedicines: List<Medicine> = emptyList(),
    val lowStockMedicines: List<Medicine> = emptyList(),
    val allMedicines: List<Medicine> = emptyList(),
    val isLoading: Boolean = true,
    val isGeneratingPdf: Boolean = false,
    val pdfUri: Uri? = null,
    val errorMessage: String? = null
)

class ReportsViewModel(
    private val repository: MedicineRepository,
    private val context: Context
) : ViewModel() {

    private val _period = MutableStateFlow(ReportPeriod.THIS_MONTH)
    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllMedicines().collect { medicines ->
                _uiState.update { it.copy(allMedicines = medicines, totalMedicines = medicines.size) }
            }
        }
        viewModelScope.launch {
            repository.getOutOfStockMedicines().collect { list ->
                _uiState.update { it.copy(outOfStockMedicines = list, outOfStockCount = list.size) }
            }
        }
        viewModelScope.launch {
            repository.getLowStockMedicines().collect { list ->
                _uiState.update { it.copy(lowStockMedicines = list, lowStockCount = list.size) }
            }
        }
        viewModelScope.launch {
            _period.collect { period ->
                loadPeriodData(period)
            }
        }
    }

    private suspend fun loadPeriodData(period: ReportPeriod) {
        _uiState.update { it.copy(isLoading = true) }
        val toMillis = System.currentTimeMillis()
        val fromMillis = toMillis - period.days * 24 * 60 * 60 * 1000L

        val salesTxns = repository.getTransactionsByTypeAndPeriod(TransactionType.SALE, fromMillis, toMillis)
        val totalSold = salesTxns.sumOf { abs(it.quantityChange) }

        // Estimate revenue from transactions × medicine price
        val allMedicines = _uiState.value.allMedicines
        val totalRevenue = salesTxns.sumOf { txn ->
            val price = allMedicines.find { it.id == txn.medicineId }?.pricePerUnit ?: 0.0
            abs(txn.quantityChange) * price
        }

        repository.getTransactionsBetween(fromMillis, toMillis)
            .first()
            .let { txns ->
                _uiState.update {
                    it.copy(
                        transactions = txns,
                        totalSold = totalSold,
                        totalSales = totalRevenue,
                        isLoading = false
                    )
                }
            }
    }

    fun onPeriodChange(p: ReportPeriod) {
        _period.value = p
        _uiState.update { it.copy(period = p) }
    }

    fun generateAndSharePdf() {
        viewModelScope.launch {
            _uiState.update { it.copy(isGeneratingPdf = true, pdfUri = null) }
            try {
                val state = _uiState.value
                val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                val fileName = "MediStock_Report_${sdf.format(Date())}.pdf"
                val file = File(context.getExternalFilesDir(null), fileName)

                PdfWriter(file.absolutePath).use { writer ->
                    val pdfDoc = PdfDocument(writer)
                    val document = Document(pdfDoc)

                    val teal = DeviceRgb(0, 121, 107)
                    val lightGray = DeviceRgb(245, 247, 246)
                    val darkGray = DeviceRgb(80, 80, 80)
                    val red = DeviceRgb(211, 47, 47)
                    val orange = DeviceRgb(245, 124, 0)

                    // Header
                    document.add(
                        Paragraph("MediStock Inventory Report")
                            .setFontSize(22f)
                            .setFontColor(teal)
                            .setTextAlignment(TextAlignment.CENTER)
                    )
                    val now = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault()).format(Date())
                    document.add(
                        Paragraph("Generated on $now  |  Period: ${state.period.label}")
                            .setFontSize(10f)
                            .setFontColor(darkGray)
                            .setTextAlignment(TextAlignment.CENTER)
                    )
                    document.add(Paragraph("\n"))

                    // Summary statistics
                    addSectionTitle(document, "📊 Summary Statistics", teal)
                    val summaryTable = Table(UnitValue.createPercentArray(floatArrayOf(1f, 1f, 1f, 1f)))
                        .useAllAvailableWidth()
                    addSummaryCell(summaryTable, "Total Medicines", state.totalMedicines.toString(), teal)
                    addSummaryCell(summaryTable, "Units Sold (${state.period.label})", state.totalSold.toString(), teal)
                    addSummaryCell(summaryTable, "Revenue (${state.period.label})", "₹%.2f".format(state.totalSales), DeviceRgb(56, 142, 60))
                    addSummaryCell(summaryTable, "Out of Stock", state.outOfStockCount.toString(), red)
                    document.add(summaryTable)
                    document.add(Paragraph("\n"))

                    // Out of stock list
                    if (state.outOfStockMedicines.isNotEmpty()) {
                        addSectionTitle(document, "🚫 Out of Stock Medicines (${state.outOfStockMedicines.size})", red)
                        val table = buildMedicineTable(state.outOfStockMedicines, teal)
                        document.add(table)
                        document.add(Paragraph("\n"))
                    }

                    // Low stock list
                    if (state.lowStockMedicines.isNotEmpty()) {
                        addSectionTitle(document, "⚠️ Low Stock Medicines (${state.lowStockMedicines.size})", orange)
                        val table = buildMedicineTable(state.lowStockMedicines, teal)
                        document.add(table)
                        document.add(Paragraph("\n"))
                    }

                    // Recent transactions
                    if (state.transactions.isNotEmpty()) {
                        addSectionTitle(document, "📋 Transaction History – ${state.period.label}", teal)
                        val txnTable = Table(UnitValue.createPercentArray(floatArrayOf(2f, 1.5f, 1f, 1f, 1f, 2f)))
                            .useAllAvailableWidth()
                        // Header
                        listOf("Medicine", "Type", "Change", "Before", "After", "Date").forEach { h ->
                            txnTable.addHeaderCell(
                                Cell().add(Paragraph(h).simulateBold().setFontSize(9f))
                                    .setBackgroundColor(teal)
                                    .setFontColor(ColorConstants.WHITE)
                            )
                        }
                        val allMeds = state.allMedicines.associateBy { it.id }
                        val txnSdf = SimpleDateFormat("dd MMM yy HH:mm", Locale.getDefault())
                        state.transactions.take(100).forEach { txn ->
                            val medicineName = allMeds[txn.medicineId]?.name ?: "Unknown"
                            val changeStr = when {
                                txn.quantityChange > 0 -> "+${txn.quantityChange}"
                                else -> txn.quantityChange.toString()
                            }
                            listOf(medicineName, txn.transactionType.name, changeStr,
                                txn.quantityBefore.toString(), txn.quantityAfter.toString(),
                                txnSdf.format(Date(txn.timestamp))).forEach { cell ->
                                txnTable.addCell(Cell().add(Paragraph(cell).setFontSize(8f)))
                            }
                        }
                        document.add(txnTable)
                        document.add(Paragraph("\n"))
                    }

                    // Full inventory
                    addSectionTitle(document, "📦 Full Inventory", teal)
                    document.add(buildMedicineTable(state.allMedicines, teal))

                    // Footer
                    document.add(Paragraph("\n"))
                    document.add(
                        Paragraph("— End of Report —  Generated by MediStock")
                            .setFontSize(9f)
                            .setFontColor(darkGray)
                            .setTextAlignment(TextAlignment.CENTER)
                    )

                    document.close()
                }

                val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                _uiState.update { it.copy(pdfUri = uri, isGeneratingPdf = false) }

            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "PDF generation failed: ${e.message}", isGeneratingPdf = false) }
            }
        }
    }

    fun sharePdf(uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Report").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    fun clearPdfUri() = _uiState.update { it.copy(pdfUri = null) }
    fun clearError() = _uiState.update { it.copy(errorMessage = null) }

    private fun addSectionTitle(doc: Document, text: String, color: DeviceRgb) {
        doc.add(Paragraph(text).setFontSize(13f).simulateBold().setFontColor(color))
        doc.add(Paragraph("").setMarginTop(-8f))
    }

    private fun addSummaryCell(table: Table, label: String, value: String, color: DeviceRgb) {
        table.addCell(
            Cell().add(
                Paragraph()
                    .add(Text("$value\n").setFontSize(16f).simulateBold().setFontColor(color))
                    .add(Text(label).setFontSize(9f).setFontColor(DeviceRgb(100, 100, 100)))
            ).setTextAlignment(TextAlignment.CENTER).setPadding(10f)
        )
    }

    private fun buildMedicineTable(medicines: List<Medicine>, headerColor: DeviceRgb): Table {
        val table = Table(UnitValue.createPercentArray(floatArrayOf(2f, 1.5f, 1f, 1f, 1.2f, 1.2f)))
            .useAllAvailableWidth()
        val sdf = SimpleDateFormat("dd MMM yy", Locale.getDefault())
        listOf("Name", "Generic", "Category", "Qty", "Mfg Date", "Exp Date").forEach { h ->
            table.addHeaderCell(
                Cell().add(Paragraph(h).simulateBold().setFontSize(9f))
                    .setBackgroundColor(headerColor).setFontColor(ColorConstants.WHITE)
            )
        }
        medicines.forEach { m ->
            val mfgMillis = m.manufacturingDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val expMillis = m.expiryDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            listOf(m.name, m.genericName, m.category, m.quantity.toString(),
                sdf.format(Date(mfgMillis)), sdf.format(Date(expMillis))).forEach { cell ->
                table.addCell(Cell().add(Paragraph(cell).setFontSize(8f)))
            }
        }
        return table
    }
}