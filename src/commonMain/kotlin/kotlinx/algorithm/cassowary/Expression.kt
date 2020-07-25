package kotlinx.algorithm.cassowary

class Expression {
    private var _terms: MutableList<Term>
    val terms: List<Term>
        get() = _terms

    var constant: Double

    constructor(constant: Double = 0.0) {
        this.constant = constant
        _terms = mutableListOf()
    }

    constructor(term: Term, constant: Double = 0.0) {
        _terms = mutableListOf()
        _terms.add(term)
        this.constant = constant
    }

    constructor(terms: List<Term>, constant: Double = 0.0) {
        this._terms = terms.toMutableList()
        this.constant = constant
    }

    val value: Double
        get() {
            var result = constant
            for (term in _terms) {
                result += term.value
            }
            return result
        }

    fun isConstant(): Boolean {
        return _terms.size == 0
    }

    override fun toString(): String {
        return buildString {
            append("isConstant: " + isConstant() + " constant: " + constant)
            if (!isConstant()) {
                append(" terms: [")
                for (term in _terms) {
                    append("(")
                    append(term)
                    append(")")
                }
                append("] ")
            }
        }
    }
}
