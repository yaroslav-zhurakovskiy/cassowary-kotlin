package kotlinx.algorithm.cassowary.tests

import kotlinx.algorithm.cassowary.*
import kotlinx.algorithm.cassowary.Symbolics.add
import kotlinx.algorithm.cassowary.Symbolics.divide
import kotlinx.algorithm.cassowary.Symbolics.multiply
import kotlinx.algorithm.cassowary.Symbolics.subtract
import kotlin.text.*

object ConstraintParser {
    private val pattern = ("\\s*(.*?)\\s*(<=|==|>=|[GL]?EQ)\\s*(.*?)\\s*(!(required|strong|medium|weak))?").toRegex()
    const val OPS = "-+/*^"

    fun parseConstraint(constraintString: String, variableResolver: CassowaryVariableResolver): Constraint {
        val result = pattern.matchEntire(constraintString)
            ?:  throw Error("Could not parse $constraintString")

        val variable = variableResolver.resolveVariable(result.groupValues[1])
        val operator = parseOperator(result.groupValues[2])
        val expression = resolveExpression(result.groupValues[3], variableResolver)
        val strength = parseStrength(result.groupValues[4])

        return Constraint(subtract(variable, expression), operator!!, strength)
    }

    private fun parseOperator(operatorString: String): RelationalOperator? {
        var operator: RelationalOperator? = null
        if ("EQ" == operatorString || "==" == operatorString) {
            operator = RelationalOperator.Equals
        } else if ("GEQ" == operatorString || ">=" == operatorString) {
            operator = RelationalOperator.GreaterThan
        } else if ("LEQ" == operatorString || "<=" == operatorString) {
            operator = RelationalOperator.LessThan
        }
        return operator
    }

    private fun parseStrength(strengthString: String?): Double {
        var strength = Strength.REQUIRED
        if ("!required" == strengthString) {
            strength = Strength.REQUIRED
        } else if ("!strong" == strengthString) {
            strength = Strength.STRONG
        } else if ("!medium" == strengthString) {
            strength = Strength.MEDIUM
        } else if ("!weak" == strengthString) {
            strength = Strength.WEAK
        }
        return strength
    }

    fun resolveExpression(expressionString: String, variableResolver: CassowaryVariableResolver): Expression {
        val postFixExpression = infixToPostfix(tokenizeExpression(expressionString))
        val expressionStack = Stack<Expression>()
        for (expression in postFixExpression) {
            if ("+" == expression) {
                expressionStack.push(add(expressionStack.pop(), expressionStack.pop()))
            } else if ("-" == expression) {
                val a = expressionStack.pop()
                val b = expressionStack.pop()
                expressionStack.push(subtract(b, a))
            } else if ("/" == expression) {
                val denominator = expressionStack.pop()
                val numerator = expressionStack.pop()
                expressionStack.push(divide(numerator, denominator))
            } else if ("*" == expression) {
                expressionStack.push(multiply(expressionStack.pop(), expressionStack.pop()))
            } else {
                var linearExpression = variableResolver.resolveConstant(expression)
                if (linearExpression == null) {
                    linearExpression = Expression(Term(variableResolver.resolveVariable(expression)))
                }
                expressionStack.push(linearExpression)
            }
        }
        return expressionStack.pop()
    }

    fun infixToPostfix(tokenList: List<String>): List<String> {
        val s = Stack<Int>()
        val postFix = ArrayList<String>()
        for (token in tokenList) {
            val c = token[0]
            val idx = OPS.indexOf(c)
            if (idx != -1 && token.length == 1) {
                if (s.isEmpty()) s.push(idx) else {
                    while (!s.isEmpty()) {
                        val prec2 = s.peek() / 2
                        val prec1 = idx / 2
                        if (prec2 > prec1 || prec2 == prec1 && c != '^') {
                            postFix.add(OPS[s.pop()].toString())
                        } else {
                            break
                        }
                    }
                    s.push(idx)
                }
            } else if (c == '(') {
                s.push(-2)
            } else if (c == ')') {
                while (s.peek() != -2) {
                    postFix.add(OPS[s.pop()].toString())
                }
                s.pop()
            } else {
                postFix.add(token)
            }
        }
        while (!s.isEmpty()) {
            postFix.add(OPS[s.pop()].toString())
        }
        return postFix
    }

    fun tokenizeExpression(expressionString: String): List<String> {
        val tokenList = ArrayList<String>()
        val stringBuilder = StringBuilder()
        var i: Int
        i = 0
        while (i < expressionString.length) {
            val c = expressionString[i]
            when (c) {
                '+', '-', '*', '/', '(', ')' -> {
                    if (stringBuilder.length > 0) {
                        tokenList.add(stringBuilder.toString())
                        stringBuilder.setLength(0)
                    }
                    tokenList.add(c.toString())
                }
                ' ' -> {
                }
                else -> stringBuilder.append(c)
            }
            i++
        }
        if (stringBuilder.length > 0) {
            tokenList.add(stringBuilder.toString())
        }
        return tokenList
    }

    interface CassowaryVariableResolver {
        fun resolveVariable(variableName: String): Variable
        fun resolveConstant(name: String): Expression?
    }
}
