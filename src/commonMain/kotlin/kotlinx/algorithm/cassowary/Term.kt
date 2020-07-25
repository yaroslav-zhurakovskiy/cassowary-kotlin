package kotlinx.algorithm.cassowary

class Term (
    var variable: Variable,
    var coefficient: Double = 1.0
) {
    val value: Double
        get() = coefficient * variable.value

    override fun toString(): String {
        return "variable: ($variable) coefficient: $coefficient"
    }
}
