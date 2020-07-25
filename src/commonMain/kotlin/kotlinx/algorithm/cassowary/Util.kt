package kotlinx.algorithm.cassowary

object Util {
    inline fun nearZero(value: Double): Boolean {
        val EPS = 1.0e-8
        return if (value < 0.0) -value < EPS else value < EPS
    }
}
