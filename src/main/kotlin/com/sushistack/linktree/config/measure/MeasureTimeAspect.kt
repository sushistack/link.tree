package com.sushistack.linktree.config.measure

import io.github.oshai.kotlinlogging.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

@Aspect
@Component
class MeasureTimeAspect {
    private val logger = KotlinLogging.logger {}

    @Around("@annotation(com.sushistack.linktree.config.measure.MeasureTime)")
    fun measureExecutionTime(joinPoint: ProceedingJoinPoint): Any? {
        val methodName = "${joinPoint.signature.declaringType.simpleName}.${joinPoint.signature.name}"

        var result: Any?
        val timeMillis = measureTimeMillis {
            result = joinPoint.proceed()
        }

        logger.info { "$methodName 실행 시간: ${timeMillis}ms" }
        return result
    }
}