package com.sushistack.linkstacker.external.excel

import com.sushistack.linkstacker.external.excel.model.ExcelSheet
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream
import java.io.IOException

class ExcelUtil {
    companion object {
        private fun <T> createRowAsString(excelSheet: Sheet, dataRows: List<T>, rowNum: Int): Row =
            excelSheet.createRow(rowNum).also {
                for (i in dataRows.indices) {
                    val cell = it.createCell(i)
                    cell.setCellValue(dataRows[i].toString())
                }
            }

        private fun <T> createHeaderRow(excelSheet: Sheet, columns: List<T>, rowNum: Int = 0): Row =
            createRowAsString(excelSheet, columns, rowNum)

        private fun save(workbook: Workbook, filePath: String) {
            try {
                FileOutputStream(filePath).use { outputStream ->
                    workbook.write(outputStream)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                workbook.close()
            }
        }

        fun <T> writeExcel(excelSheets: List<ExcelSheet<T>>, filePath: String) {
            val workbook: Workbook = XSSFWorkbook()

            for (sheet in excelSheets) {
                val excelSheet = workbook.createSheet(sheet.name)

                createHeaderRow(excelSheet, sheet.columns)
                for (rowIndex in sheet.rows.indices) {
                    createRowAsString(excelSheet, sheet.rows, rowIndex + 1)
                }
            }

            save(workbook, filePath)
        }
    }
}