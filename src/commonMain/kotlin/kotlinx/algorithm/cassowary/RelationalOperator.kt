package kotlinx.algorithm.cassowary

inline class RelationalOperator(val rawValue: Int) {
    companion object {
        val LessThan = RelationalOperator(0)
        val GreaterThan = RelationalOperator(1)
        val Equals = RelationalOperator(2)
    }
}
