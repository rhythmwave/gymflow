package com.rhythmwave.gymflow.engine

import org.junit.Assert.*
import org.junit.Test

class ProgressionEngineTest {

    @Test
    fun `estimateOneRepMax with 1 rep returns same weight`() {
        assertEquals(100.0, ProgressionEngine.estimateOneRepMax(100.0, 1), 0.01)
    }

    @Test
    fun `estimateOneRepMax with 5 reps is higher than weight`() {
        val est1RM = ProgressionEngine.estimateOneRepMax(80.0, 5)
        assertTrue(est1RM > 80.0)
        assertEquals(93.33, est1RM, 0.1)
    }

    @Test
    fun `estimateOneRepMax with 10 reps is significantly higher`() {
        val est1RM = ProgressionEngine.estimateOneRepMax(60.0, 10)
        assertEquals(80.0, est1RM, 0.01)
    }

    @Test
    fun `isNewPersonalRecord returns true when no existing PR`() {
        assertTrue(ProgressionEngine.isNewPersonalRecord(80.0, 5, null))
    }

    @Test
    fun `isNewPersonalRecord returns true when beating existing PR`() {
        assertTrue(ProgressionEngine.isNewPersonalRecord(85.0, 5, 90.0))
    }

    @Test
    fun `isNewPersonalRecord returns false when not beating PR`() {
        assertFalse(ProgressionEngine.isNewPersonalRecord(70.0, 5, 100.0))
    }

    @Test
    fun `calculateDeloadWeight reduces by 10 percent`() {
        assertEquals(90.0, ProgressionEngine.calculateDeloadWeight(100.0, 10), 0.01)
    }

    @Test
    fun `calculateDeloadWeight rounds to nearest 2`() {
        assertEquals(67.5, ProgressionEngine.calculateDeloadWeight(75.0, 10), 0.01)
    }

    @Test
    fun `calculateDeloadWeight never goes below zero`() {
        assertEquals(0.0, ProgressionEngine.calculateDeloadWeight(5.0, 10), 0.01)
    }
}
