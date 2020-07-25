package kotlinx.algorithm.cassowary.tests

import kotlinx.algorithm.cassowary.Solver
import kotlinx.algorithm.cassowary.Symbolics.equals
import kotlinx.algorithm.cassowary.Symbolics.greaterThanOrEqualTo
import kotlinx.algorithm.cassowary.Symbolics.lessThanOrEqualTo
import kotlinx.algorithm.cassowary.UnsatisfiableConstraintException
import kotlinx.algorithm.cassowary.Variable
import kotlin.test.*
import kotlinx.algorithm.cassowary.assert.*

class ConstantVariableTest {
    @Test
    fun lessThanEqualTo() {
        val x = Variable("x")
        val solver = Solver()
        solver.addConstraint(lessThanOrEqualTo(100.0, x))
        solver.updateVariables()
        assertLessThanOrEqualTo(10.0, x.value)
        solver.addConstraint(equals(x, 110.0))
        solver.updateVariables()
        assertEquals(110.0, x.value, EPSILON)
    }

    @Test
    fun lessThanEqualToUnsatisfiable() {
        assertFailsWith(UnsatisfiableConstraintException::class) {
            val x = Variable("x")
            val solver = Solver()
            solver.addConstraint(lessThanOrEqualTo(100.0, x))
            solver.updateVariables()
            assertLessThanOrEqualTo(100.0, x.value)
            solver.addConstraint(equals(x, 10.0))
            solver.updateVariables()
        }
    }

    @Test
    fun greaterThanEqualTo() {
        val x = Variable("x")
        val solver = Solver()
        solver.addConstraint(greaterThanOrEqualTo(100.0, x))
        solver.updateVariables()
        assertGreaterThanOrEqualTo(100.0, x.value)
        solver.addConstraint(equals(x, 90.0))
        solver.updateVariables()
        assertEquals(90.0, x.value, EPSILON)
    }

    @Test
    fun greaterThanEqualToUnsatisfiable() {
        assertFailsWith(UnsatisfiableConstraintException::class) {
            val x = Variable("x")
            val solver = Solver()
            solver.addConstraint(greaterThanOrEqualTo(100.0, x))
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
