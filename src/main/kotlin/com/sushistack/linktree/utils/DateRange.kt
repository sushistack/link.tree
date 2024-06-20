package com.sushistack.linktree.utils

import java.time.LocalDate
import java.util.concurrent.ThreadLocalRandom



data class DateRange(
    val from: LocalDate = LocalDate.now().minusDays(20),
    val to: LocalDate = LocalDate.now().minusDays(1)
)

fun DateRange.pick(): LocalDate =
    ThreadLocalRandom
        .current()
        .nextLong(this.from.toEpochDay(), this.to.toEpochDay() + 1)
        .let { LocalDate.ofEpochDay(it) }
