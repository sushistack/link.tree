package com.sushistack.linktree.config.measure

import org.junit.jupiter.api.Test

class MeasureTimeAspectTest {

    @Test
    fun aspectTest() {
        staticfunction()
    }

    companion object {
        @MeasureTime
        @JvmStatic
        fun staticfunction() {
            Thread.sleep(2000)
            print("asdasda")
        }
    }

}