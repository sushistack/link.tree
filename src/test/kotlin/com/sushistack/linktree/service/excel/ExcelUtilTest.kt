package com.sushistack.linktree.service.excel

import com.sushistack.linktree.utils.excel.annotation.ExcelColumn
import com.sushistack.linktree.utils.excel.model.ExcelSheet
import com.sushistack.linktree.utils.excel.ExcelUtil
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.nio.file.Paths
import kotlin.reflect.full.declaredMemberProperties

class ExcelUtilTest {
    private lateinit var excelSheets: List<ExcelSheet>
    private lateinit var testClass: TestClazz

    @BeforeEach
    fun setUp() {
        excelSheets = listOf(
            ExcelSheet(
                name = "test sheet1",
                rows = listOf(
                    TestClazz("Name1", 13, "seoul", "avcd@x.com"),
                    TestClazz("Name2", 9, "busan", "bbda@x.com"),
                    TestClazz("Name3", 53, "daejeon", "qweq@x.com")
                )
            ),
            ExcelSheet(
                name = "test sheet2",
                rows = listOf(
                    TestClazz("Name4", 18, "seoul2", "avcd@y.com"),
                    TestClazz("Name5", 19, "busan2", "bbda@y.com"),
                    TestClazz("Name6", 34, "daejeon2", "qweq@y.com")
                )
            )
        )

        testClass = TestClazz("Name1", 13, "seoul", "avcd@x.com")
    }

    @Test
    @DisplayName("write sheets to excel file.")
    fun writeExcelTest() {
        ExcelUtil.writeExcel(excelSheets, Paths.get("files/excel/abcd.xlsx"))
    }

    @Test
    @DisplayName("class members to map.")
    fun clazzMembersTest() {
        val map = TestClazz::class.declaredMemberProperties
            .sortedBy { it.annotations.filterIsInstance<ExcelColumn>().firstOrNull()?.order ?: Int.MAX_VALUE }
            .map { prop -> prop.name to prop.getter.call(testClass) }

        Assertions.assertThat(map[0]).isEqualTo("name" to "Name1")
        Assertions.assertThat(map[1]).isEqualTo("age" to 13)
        Assertions.assertThat(map[2]).isEqualTo("address" to "seoul")
        Assertions.assertThat(map[3]).isEqualTo("email" to "avcd@x.com")
    }

}

data class TestClazz(
    @ExcelColumn(name = "이름", order = 1) val name: String,
    @ExcelColumn(name = "나이", order = 2) val age: Int,
    @ExcelColumn(name = "주소", order = 3) val address: String,
    val email: String,
)