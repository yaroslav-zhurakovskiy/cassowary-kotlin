package kotlinx.algorithm.cassowary

class Constraint(
    expression: Expression,
    var operator: RelationalOperator,
    strength: Double = Strength.REQUIRED
) {
    val expression: Expression = reduce(expression)
    private var _strength: Double = Strength.clip(strength)
    val strength: Double get() = _strength

    constructor(other: Constraint, strength: Double) : this(other.expression, other.operator, strength)

    fun setStrength(strength: Double): Constraint {
        _strength = strength
        return this
    }

    override fun toString(): String {
        return "expression: ($expression) strength: $_strength operator: $operator"
    }

    companion object {
        private fun reduce(expr: Expression): Expression {
            val vars = mutableMapOf<Variable, Double>()
            for (term in expr.terms) {
                var value = vars[term.variable]
                if (value == null) {
                    value = 0.0
                }
                value += term.coefficient
                vars[term.variable] = value
            }
            val reducedTerms = mutableListOf<Term>()
            for (variable in vars.keys) {
                reducedTerms.add(Term(variable, vars[variable]!!))
            }
            return Expression(reducedTerms, expr.constant)
        }
    }
}
