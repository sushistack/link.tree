package com.sushistack.linktree.config.measure

import io.github.oshai.kotlinlogging.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import kotlin.system.measureTimeMillis

@Aspect
class MeasureTimeAspect {
    private val log = KotlinLogging.logger {}

    init {
        log.info { "✅ MeasureTimeAspect가 로드되었습니다!" }
    }

    @Around("call(@com.sushistack.linktree.config.measure.MeasureTime * *(..))")
    fun measureExecutionTime(joinPoint: ProceedingJoinPoint): Any? {
        val methodName = "${joinPoint.signature.declaringType.simpleName}.${joinPoint.signature.name}"

        var result: Any? = null
        val timeMillis = measureTimeMillis {
            result = try {
                joinPoint.proceed()
            } catch (e: Throwable) {
                log.error(e) { "$methodName 실행 중 예외 발생" }
                throw e
            }
        }

        log.info { "$methodName 실행 시간: ${timeMillis}ms" }
        return result
    }
}