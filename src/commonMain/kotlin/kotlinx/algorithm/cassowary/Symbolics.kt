package kotlinx.algorithm.cassowary

// TODO: Rework
object Symbolics {
    fun multiply(variable: Variable, coefficient: Double): Term {
        return Term(variable, coefficient)
    }

    fun divide(variable: Variable, denominator: Double): Term {
        return multiply(variable, 1.0 / denominator)
    }

    fun negate(variable: Variable): Term {
        return multiply(variable, -1.0)
    }

    fun multiply(term: Term, coefficient: Double): Term {
        return Term(term.variable, term.coefficient * coefficient)
    }

    fun divide(term: Term, denominator: Double): Term {
        return multiply(term, 1.0 / denominator)
    }

    fun negate(term: Term): Term {
        return multiply(term, -1.0)
    }

    fun multiply(expression: Expression, coefficient: Double): Expression {
        val terms = mutableListOf<Term>()
        for (term in expression.terms) {
            terms.add(multiply(term, coefficient))
        }
        return Expression(terms, expression.constant * coefficient)
    }

    fun multiply(expression1: Expression, expression2: Expression): Expression {
        return if (expression1.isConstant()) {
            multiply(expression1.constant, expression2)
        } else if (expression2.isConstant()) {
            multiply(expression2.constant, expression1)
        } else {
            throw NonlinearExpressionException()
        }
    }

    fun divide(expression: Expression, denominator: Double): Expression {
        return multiply(expression, 1.0 / denominator)
    }

    fun divide(expression1: Expression, expression2: Expression): Expression {
        return if (expression2.isConstant()) {
            divide(expression1, expression2.constant)
        } else {
            throw NonlinearExpressionException()
        }
    }

    fun negate(expression: Expression): Expression {
        return multiply(expression, -1.0)
    }

    fun multiply(coefficient: Double, expression: Expression): Expression {
        return multiply(expression, coefficient)
    }

    fun multiply(coefficient: Double, term: Term): Term {
        return multiply(term, coefficient)
    }

    fun multiply(coefficient: Double, variable: Variable): Term {
        return multiply(variable, coefficient)
    }

    fun add(first: Expression, second: Expression): Expression {
        // TODO do we need to copy term objects?
        val terms = ArrayList<Term>(first.terms.size + second.terms.size)
        terms.addAll(first.terms)
        terms.addAll(second.terms)
        return Expression(terms, first.constant + second.constant)
    }

    fun add(first: Expression, second: Term): Expression {
        // TODO do we need to copy term objects?
        val terms = ArrayList<Term>(first.terms.size + 1)
        terms.addAll(first.terms)
        terms.add(second)
        return Expression(terms, first.constant)
    }

    fun add(expression: Expression, variable: Variable): Expression {
        return add(expression, Term(variable))
    }

    fun add(expression: Expression, constant: Double): Expression {
        return Expression(expression.terms, expression.constant + constant)
    }

    fun subtract(first: Expression, second: Expression): Expression {
        return add(first, negate(second))
    }

    fun subtract(expression: Expression, term: Term): Expression {
        return add(expression, negate(term))
    }

    fun subtract(expression: Expression, variable: Variable): Expression {
        return add(expression, negate(variable))
    }

    fun subtract(expression: Expression, constant: Double): Expression {
        return add(expression, -constant)
    }

    fun add(term: Term, expression: Expression): Expression {
        return add(expression, term)
    }

    fun add(first: Term, second: Term): Expression {
        val terms: MutableList<Term> = ArrayList(2)
        terms.add(first)
        terms.add(second)
        return Expression(terms)
    }

    fun add(term: Term, variable: Variable): Expression {
        return add(term, Term(variable))
    }

    fun add(term: Term, constant: Double): Expression {
        return Expression(term, constant)
    }

    fun subtract(term: Term, expression: Expression): Expression {
        return add(negate(expression), term)
    }

    fun subtract(first: Term, second: Term): Expression {
        return add(first, negate(second))
    }

    fun subtract(term: Term, variable: Variable): Expression {
        return add(term, negate(variable))
    }

    fun subtract(term: Term, constant: Double): Expression {
        return add(term, -constant)
    }

    fun add(variable: Variable, expression: Expression): Expression {
        return add(expression, variable)
    }

    fun add(variable: Variable, term: Term): Expression {
        return add(term, variable)
    }

    fun add(first: Variable, second: Variable): Expression {
        return add(Term(first), second)
    }

    fun add(variable: Variable, constant: Double): Expression {
        return add(Term(variable), constant)
    }

    fun subtract(variable: Variable, expression: Expression): Expression {
        return add(variable, negate(expression))
    }

    fun subtract(variable: Variable, term: Term): Expression {
        return add(variable, negate(term))
    }

    fun subtract(first: Variable, second: Variable): Expression {
        return add(first, negate(second))
    }

    fun subtract(variable: Variable, constant: Double): Expression {
        return add(variable, -constant)
    }

    fun add(constant: Double, expression: Expression): Expression {
        return add(expression, constant)
    }

    fun add(constant: Double, term: Term): Expression {
        return add(term, constant)
    }

    fun add(constant: Double, variable: Variable): Expression {
        return add(variable, constant)
    }

    fun subtract(constant: Double, expression: Expression): Expression {
        return add(negate(expression), constant)
    }

    fun subtract(constant: Double, term: Term): Expression {
        return add(negate(term), constant)
    }

    fun subtract(constant: Double, variable: Variable): Expression {
        return add(negate(variable), constant)
    }

    fun equals(first: Expression, second: Expression): Constraint {
        return Constraint(subtract(first, second), RelationalOperator.Equals)
    }

    fun equals(expression: Expression, term: Term): Constraint {
        return equals(expression, Expression(term))
    }

    fun equals(expression: Expression, variable: Variable): Constraint {
        return equals(expression, Term(variable))
    }

    fun equals(expression: Expression, constant: Double): Constraint {
        return equals(expression, Expression(constant))
    }

    fun lessThanOrEqualTo(first: Expression, second: Expression): Constraint {
        return Constraint(subtract(first, second), RelationalOperator.LessThan)
    }

    fun lessThanOrEqualTo(expression: Expression, term: Term): Constraint {
        return lessThanOrEqualTo(expression, Expression(term))
    }

    fun lessThanOrEqualTo(expression: Expression, variable: Variable): Constraint {
        return lessThanOrEqualTo(expression, Term(variable))
    }

    fun lessThanOrEqualTo(expression: Expression, constant: Double): Constraint {
        return lessThanOrEqualTo(expression, Expression(constant))
    }

    fun greaterThanOrEqualTo(first: Expression, second: Expression): Constraint {
        return Constraint(subtract(first, second), RelationalOperator.GreaterThan)
    }

    fun greaterThanOrEqualTo(expression: Expression, term: Term): Constraint {
        return greaterThanOrEqualTo(expression, Expression(term))
    }

    fun greaterThanOrEqualTo(expression: Expression, variable: Variable): Constraint {
        return greaterThanOrEqualTo(expression, Term(variable))
    }

    fun greaterThanOrEqualTo(expression: Expression, constant: Double): Constraint {
        return greaterThanOrEqualTo(expression, Expression(constant))
    }

    fun equals(term: Term, expression: Expression): Constraint {
        return equals(expression, term)
    }

    fun equals(first: Term, second: Term): Constraint {
        return equals(Expression(first), second)
    }

    fun equals(term: Term, variable: Variable): Constraint {
        return equals(Expression(term), variable)
    }

    fun equals(term: Term, constant: Double): Constraint {
        return equals(Expression(term), constant)
    }

    fun lessThanOrEqualTo(term: Term, expression: Expression): Constraint {
        return lessThanOrEqualTo(Expression(term), expression)
    }

    fun lessThanOrEqualTo(first: Term, second: Term): Constraint {
        return lessThanOrEqualTo(Expression(first), second)
    }

    fun lessThanOrEqualTo(term: Term, variable: Variable): Constraint {
        return lessThanOrEqualTo(Expression(term), variable)
    }

    fun lessThanOrEqualTo(term: Term, constant: Double): Constraint {
        return lessThanOrEqualTo(Expression(term), constant)
    }

    fun greaterThanOrEqualTo(term: Term, expression: Expression): Constraint {
        return greaterThanOrEqualTo(Expression(term), expression)
    }

    fun greaterThanOrEqualTo(first: Term, second: Term): Constraint {
        return greaterThanOrEqualTo(Expression(first), second)
    }

    fun greaterThanOrEqualTo(term: Term, variable: Variable): Constraint {
        return greaterThanOrEqualTo(Expression(term), variable)
    }

    fun greaterThanOrEqualTo(term: Term, constant: Double): Constraint {
        return greaterThanOrEqualTo(Expression(term), constant)
    }

    fun equals(variable: Variable, expression: Expression): Constraint {
        return equals(expression, variable)
    }

    fun equals(variable: Variable, term: Term): Constraint {
        return equals(term, variable)
    }

    fun equals(first: Variable, second: Variable): Constraint {
        return equals(Term(first), second)
    }

    fun equals(variable: Variable, constant: Double): Constraint {
        return equals(Term(variable), constant)
    }

    fun lessThanOrEqualTo(variable: Variable, expression: Expression): Constraint {
        return lessThanOrEqualTo(Term(variable), expression)
    }

    fun lessThanOrEqualTo(variable: Variable, term: Term): Constraint {
        return lessThanOrEqualTo(Term(variable), term)
    }

    fun lessThanOrEqualTo(first: Variable, second: Variable): Constraint {
        return lessThanOrEqualTo(Term(first), second)
    }

    fun lessThanOrEqualTo(variable: Variable, constant: Double): Constraint {
        return lessThanOrEqualTo(Term(variable), constant)
    }

    fun greaterThanOrEqualTo(variable: Variable, expression: Expression): Constraint {
        return greaterThanOrEqualTo(Term(variable), expression)
    }

    fun greaterThanOrEqualTo(variable: Variable, term: Term): Constraint {
        return greaterThanOrEqualTo(term, variable)
    }

    fun greaterThanOrEqualTo(first: Variable, second: Variable): Constraint {
        return greaterThanOrEqualTo(Term(first), second)
    }

    fun greaterThanOrEqualTo(variable: Variable, constant: Double): Constraint {
        return greaterThanOrEqualTo(Term(variable), constant)
    }

    fun equals(constant: Double, expression: Expression): Constraint {
        return equals(expression, constant)
    }

    fun equals(constant: Double, term: Term): Constraint {
        return equals(term, constant)
    }

    fun equals(constant: Double, variable: Variable): Constraint {
        return equals(variable, constant)
    }

    fun lessThanOrEqualTo(constant: Double, expression: Expression): Constraint {
        return lessThanOrEqualTo(Expression(constant), expression)
    }

    fun lessThanOrEqualTo(constant: Double, term: Term): Constraint {
        return lessThanOrEqualTo(constant, Expression(term))
    }

    fun lessThanOrEqualTo(constant: Double, variable: Variable): Constraint {
        return lessThanOrEqualTo(constant, Term(variable))
    }

    fun greaterThanOrEqualTo(constant: Double, term: Term): Constraint {
        return greaterThanOrEqualTo(Expression(constant), term)
    }

    fun greaterThanOrEqualTo(constant: Double, variable: Variable): Constraint {
        return greaterThanOrEqualTo(constant, Term(variable))
    }

    fun modifyStrength(constraint: Constraint, strength: Double): Constraint {
        return Constraint(constraint, strength)
    }

    fun modifyStrength(strength: Double, constraint: Constraint): Constraint {
        return modifyStrength(strength, constraint)
    }
}
