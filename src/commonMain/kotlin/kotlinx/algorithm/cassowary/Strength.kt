package kotlinx.algorithm.cassowary

import kotlin.math.*

object Strength {
    val REQUIRED = create(1000.0, 1000.0, 1000.0)
    val STRONG = create(1.0, 0.0, 0.0)
    val MEDIUM = create(0.0, 1.0, 0.0)
    val WEAK = create(0.0, 0.0, 1.0)

    fun create(a: Double, b: Double, c: Double, w: Double = 1.0): Double {
        var result = 0.0
        result += max(0.0, min(1000.0, a * w)) * 1000000.0
        result += max(0.0, min(1000.0, b * w)) * 1000.0
        result += max(0.0, min(1000.0, c * w))
        return result
    }

    fun clip(value: Double): Double {
        return max(0.0, min(REQUIRED, value))
    }
}
