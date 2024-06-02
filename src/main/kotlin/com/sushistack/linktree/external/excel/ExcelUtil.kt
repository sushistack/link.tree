package com.sushistack.linktree.external.excel

import com.sushistack.linktree.external.excel.config.ExcelColumn
import com.sushistack.linktree.external.excel.model.ExcelSheet
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Path
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

class ExcelUtil {
    companion object {
        private fun setHeaderRow(excelSheet: Sheet, clazz: KClass<out Any>) {
            val columns = clazz.declaredMemberProperties
                .sortedBy { it.annotations.filterIsInstance<ExcelColumn>().firstOrNull()?.order ?: Int.MAX_VALUE }
                .map { prop -> prop.findAnnotation<ExcelColumn>()?.name ?: prop.name }

            excelSheet.createRow(0).also {
                for (i in columns.indices) {
                    val cell = it.createCell(i)
                    cell.setCellValue(columns[i])
                }
            }
        }

        private fun setDataRows(excelSheet: Sheet, dataRows: List<Any>) {
            dataRows.mapIndexed { index, row ->
                excelSheet.createRow(index + 1).also {

                    val memberValues = row::class.memberProperties
                        .sortedBy { prop -> prop.annotations.filterIsInstance<ExcelColumn>().firstOrNull()?.order ?: Int.MAX_VALUE }
                        .map { prop -> prop.getter.call(row) }

                    for (i in memberValues.indices) {
                        val cell = it.createCell(i)
                        cell.setCellValue(memberValues[i].toString())
                    }
                }
            }
        }


        private fun save(workbook: Workbook, filePath: Path) {
            try {
                FileOutputStream(filePath.toFile()).use { outputStream ->
                    workbook.write(outputStream)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                workbook.close()
            }
        }

        fun writeExcel(sheets: List<ExcelSheet>, filePath: Path) {
            val workbook: Workbook = XSSFWorkbook()

            for (sheet in sheets) {
                val excelSheet = workbook.createSheet(sheet.name)

                setHeaderRow(excelSheet, sheet.rows[0]::class)
                setDataRows(excelSheet, sheet.rows)
            }

            save(workbook, filePath)
        }
    }
}