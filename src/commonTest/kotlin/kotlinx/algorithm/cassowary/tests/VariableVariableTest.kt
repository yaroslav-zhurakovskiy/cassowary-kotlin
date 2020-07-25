package kotlinx.algorithm.cassowary.tests

import kotlinx.algorithm.cassowary.Solver
import kotlinx.algorithm.cassowary.Symbolics.equals
import kotlinx.algorithm.cassowary.Symbolics.greaterThanOrEqualTo
import kotlinx.algorithm.cassowary.Symbolics.lessThanOrEqualTo
import kotlinx.algorithm.cassowary.UnsatisfiableConstraintException
import kotlinx.algorithm.cassowary.Variable
import kotlin.test.*
import kotlinx.algorithm.cassowary.assert.*

class VariableVariableTest {
    @Test
    fun lessThanEqualTo() {
        val solver = Solver()
        val x = Variable("x")
        val y = Variable("y")
        solver.addConstraint(equals(y, 100.0))
        solver.addConstraint(lessThanOrEqualTo(x, y))
        solver.updateVariables()
        assertLessThanOrEqualTo(x.value, 100.0)
        solver.addConstraint(equals(x, 90.0))
        solver.updateVariables()
        assertEquals(x.value, 90.0, EPSILON)
    }

    @Test
    fun lessThanEqualToUnsatisfiable() {
        assertFailsWith(UnsatisfiableConstraintException::class) {
            val solver = Solver()
            val x = Variable("x")
            val y = Variable("y")
            solver.addConstraint(equals(y, 100.0))
            solver.addConstraint(lessThanOrEqualTo(x, y))
            solver.updateVariables()
            assertLessThanOrEqualTo(x.value, 100.0)
            solver.addConstraint(equals(x, 110.0))
            solver.updateVariables()
        }
    }

    @Test
    fun greaterThanEqualTo() {
        val solver = Solver()
        val x = Variable("x")
        val y = Variable("y")
        solver.addConstraint(equals(y, 100.0))
        solver.addConstraint(greaterThanOrEqualTo(x, y))
        solver.updateVariables()
        assertGreaterThanOrEqualTo(x.value, 100.0)
        solver.addConstraint(equals(x, 110.0))
        solver.updateVariables()
        assertEquals(x.value, 110.0, EPSILON)
    }

    @Test
    fun greaterThanEqualToUnsatisfiable() {
        assertFailsWith(UnsatisfiableConstraintException::class) {
            val solver = Solver()
            val x = Variable("x")
            val y = Variable("y")
            solver.addConstraint(equals(y, 100.0))
            solver.addConstraint(greaterThanOrEqualTo(x, y))
            solver.updateVariables()
            assertGreaterThanOrEqualTo(x.value, 100.0)
            solver.addConstraint(equals(x, 90.0))
            solver.updateVariables()
        }
    }

    companion object {
        private const val EPSILON = 1.0e-8
    }
}
