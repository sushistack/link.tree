package com.sushistack.linktree.jobs.link.gen.tasklet

import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.external.excel.ExcelUtil
import com.sushistack.linktree.external.excel.model.ExcelSheet
import com.sushistack.linktree.model.LinkTable
import com.sushistack.linktree.service.LinkNodeService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.file.Paths
import java.time.LocalDate

@JobScope
@Component
class ReportTasklet(
    private val appHomeDir: String,
    private val linkNodeService: LinkNodeService
): Tasklet {
    companion object {
        private const val FIRST_TIER_SHEET_NAME = "기사형 1티어 백링크"
        private const val SECOND_TIER_SHEET_NAME = "기사형 2티어 백링크"
        private const val THIRD_TIER_SHEET_NAME = "댓글형 3티어 백링크"
        private const val REPORT_NAME = "백링크 빌딩 결과 보고서"
    }

    private val log = KotlinLogging.logger {}

    @Value("#{jobExecutionContext['order']}")
    lateinit var order: Order

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        val first = linkNodeService.findAllByOrderAndTier(order, tier = 1)
        val second = linkNodeService.findAllByOrderAndTier(order, tier = 2)
        val third = linkNodeService.findAllByOrderAndTier(order, tier = 3)

        log.info { "first tier := [${first.size}]" }
        log.info { "second tier := [${second.size}]" }
        log.info { "third tier := [${third.size}]" }

        val sheets = listOf(
            createSummarySheet(first.size, second.size, third.size),
            ExcelSheet(FIRST_TIER_SHEET_NAME, first.map { LinkTable(order.targetUrl, it.url, it.createdDate.toString()) }),
            ExcelSheet(SECOND_TIER_SHEET_NAME, second.map { LinkTable(first.find { f -> f.nodeSeq == it.parentNodeSeq }?.url ?: "", it.url, it.createdDate.toString()) }),
            ExcelSheet(THIRD_TIER_SHEET_NAME, third.map { LinkTable(second.find { f -> f.nodeSeq == it.parentNodeSeq }?.url ?: "", it.url, it.createdDate.toString()) })
        )

        ExcelUtil.writeExcel(sheets, Paths.get("${appHomeDir}/files/excel/${order.customerName}_${order.orderSeq}_${LocalDate.now()}.xlsx"))
        return RepeatStatus.FINISHED
    }

    private fun createSummarySheet(firstSize: Int, secondSize: Int, thirdSize: Int) =
        ExcelSheet(
            REPORT_NAME,
            listOf(
                mutableListOf(REPORT_NAME),
                mutableListOf(),
                mutableListOf(FIRST_TIER_SHEET_NAME, "$firstSize", "개", "(타겟 URL을 직접 링크합니다.)"),
                mutableListOf(SECOND_TIER_SHEET_NAME, "$secondSize", "개", "(위의 기사형 1티어 백링크를 링크합니다.)"),
                mutableListOf(THIRD_TIER_SHEET_NAME, "$thirdSize", "개", "(위의 기사형 2티어 백링크를 링크합니다.)"),
                mutableListOf(),
                mutableListOf("총합", "${firstSize + secondSize + thirdSize}", "개", "의 피라미드 구조 백링크가 생성 완료되었습니다."),
                mutableListOf(),
                mutableListOf("수정가능한 유지보수 기간은 1개월이며, 링크는 평생 유지됩니다."),
                mutableListOf("유지보수 기간 내 이탈한 수량은 추가 진행으로 보상해드립니다."),
                mutableListOf("3티어 댓글형 백링크는 인덱스의 용도로 100%의 연결 신뢰도를 갖지 않습니다."),
                mutableListOf("문제 발생 시 메시지 부탁드립니다."),
                mutableListOf(),
                mutableListOf("이용해주셔서 감사합니다."),
                mutableListOf("by JETT Analysis")
            )
        )
}