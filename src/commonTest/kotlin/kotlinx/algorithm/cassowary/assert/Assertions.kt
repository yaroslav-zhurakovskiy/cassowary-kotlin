package kotlinx.algorithm.cassowary.assert

import kotlin.math.abs
import kotlin.test.assertTrue

fun assertEquals(actual: Double, expected: Double, epsilon: Double, message: String? = null) {
    assertTrue(
        abs(actual - expected) <= epsilon,
        message ?: "$actual == $expected within $epsilon"
    )
}

fun <Value: Comparable<Value>> assertLessThanOrEqualTo(actual: Value, expected: Value, message: String? = null) {
    assertTrue(actual <= expected, message ?: "$actual <= $expected")
}

fun <Value: Comparable<Value>> assertGreaterThanOrEqualTo(actual: Value, expected: Value, message: String? = null) {
    assertTrue(actual >= expected, message ?: "$actual >= $expected")
}
