package kotlinx.algorithm.cassowary.benchmarks

import kotlinx.algorithm.cassowary.tests.ConstraintParser.CassowaryVariableResolver
import kotlinx.algorithm.cassowary.tests.ConstraintParser.parseConstraint
import kotlinx.algorithm.cassowary.Expression
import kotlinx.algorithm.cassowary.Solver
import kotlinx.algorithm.cassowary.Variable
import kotlin.time.*

private fun main() {
    val benchmarks = Benchmarks()
    benchmarks.testAddingLotsOfConstraints()
}

@OptIn(kotlin.time.ExperimentalTime::class)
private class Benchmarks {
    fun testAddingLotsOfConstraints() {
        val solver = Solver()
        val variables = mutableMapOf<String, Variable>()
        val variableResolver = object : CassowaryVariableResolver {
            override fun resolveVariable(variableName: String): Variable {
                val variable: Variable
                val foundVariable = variables[variableName]
                if (foundVariable != null) {
                    variable = foundVariable
                } else {
                    variable = Variable(variableName)
                    variables[variableName] = variable
                }
                return variable
            }

            override fun resolveConstant(name: String): Expression? {
                return try { Expression(name.toDouble()) } catch (_: NumberFormatException) { null }
            }
        }
        solver.addConstraint(parseConstraint("variable0 == 100", variableResolver))
        for (i in 1..2999) {
            val constraintString = getVariableName(i) + " == 100 + " + getVariableName(i - 1)
            val constraint = parseConstraint(constraintString, variableResolver)
            val time = measureTime { solver.addConstraint(constraint) }
            println(i.toString() + "," + time.toString(DurationUnit.MICROSECONDS))
        }
    }

    private fun getVariableName(number: Int): String {
        return "getVariable$number"
    }
}
