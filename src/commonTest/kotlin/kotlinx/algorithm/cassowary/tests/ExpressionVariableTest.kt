package kotlinx.algorithm.cassowary.tests

import kotlinx.algorithm.cassowary.*
import kotlinx.algorithm.cassowary.Symbolics.equals
import kotlinx.algorithm.cassowary.Symbolics.greaterThanOrEqualTo
import kotlinx.algorithm.cassowary.Symbolics.lessThanOrEqualTo
import kotlin.test.*
import kotlinx.algorithm.cassowary.assert.*

class ExpressionVariableTest {
    @Test
    fun lessThanEqualTo() {
        val x = Variable("x")
        val solver = Solver()
        solver.addConstraint(lessThanOrEqualTo(Expression(100.0), x))
        solver.updateVariables()
        assertLessThanOrEqualTo(100.0, x.value)
        solver.addConstraint(equals(x, 110.0))
        solver.updateVariables()
        assertEquals(x.value, 110.0, EPSILON)
    }

    @Test
    fun lessThanEqualToUnsatisfiable() {
        assertFailsWith(UnsatisfiableConstraintException::class) {
            val x = Variable("x")
            val solver = Solver()
            solver.addConstraint(lessThanOrEqualTo(Expression(100.0), x))
            solver.updateVariables()
            assertLessThanOrEqualTo(x.value, 100.0)
            solver.addConstraint(equals(x, 10.0))
            solver.updateVariables()
        }
    }

    @Test
    fun greaterThanEqualTo() {
        val x = Variable("x")
        val solver = Solver()
        solver.addConstraint(greaterThanOrEqualTo(Expression(100.0), x))
        solver.updateVariables()
        assertGreaterThanOrEqualTo(100.0, x.value)
        solver.addConstraint(equals(x, 90.0))
        solver.updateVariables()
        assertEquals(x.value, 90.0, EPSILON)
    }

    @Test
    fun greaterThanEqualToUnsatisfiable() {
        assertFailsWith(UnsatisfiableConstraintException::class) {
            val x = Variable("x")
            val solver = Solver()
            solver.addConstraint(greaterThanOrEqualTo(Expression(100.0), x))
            solver.updateVariables()
            assertGreaterThanOrEqualTo(100.0, x.value)
            solver.addConstraint(equals(x, 110.0))
            solver.updateVariables()
        }
    }

    companion object {
        private const val EPSILON = 1.0e-8
    }
}
