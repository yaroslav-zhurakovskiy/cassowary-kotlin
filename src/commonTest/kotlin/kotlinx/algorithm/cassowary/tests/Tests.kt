package kotlinx.algorithm.cassowary.tests

import kotlinx.algorithm.cassowary.Solver
import kotlinx.algorithm.cassowary.Strength
import kotlinx.algorithm.cassowary.Symbolics.add
import kotlinx.algorithm.cassowary.Symbolics.equals
import kotlinx.algorithm.cassowary.Symbolics.greaterThanOrEqualTo
import kotlinx.algorithm.cassowary.Symbolics.lessThanOrEqualTo
import kotlinx.algorithm.cassowary.Symbolics.multiply
import kotlinx.algorithm.cassowary.UnsatisfiableConstraintException
import kotlinx.algorithm.cassowary.Variable
import kotlin.test.*
import kotlin.math.*
import kotlinx.algorithm.cassowary.assert.*

class Tests {
    @Test
    fun simpleNew() {
        val solver = Solver()
        val x = Variable("x")
        solver.addConstraint(equals(add(x, 2.0), 20.0))
        solver.updateVariables()
        assertEquals(x.value, 18.0, EPSILON)
    }

    @Test
    fun simple0() {
        val solver = Solver()
        val x = Variable("x")
        val y = Variable("y")
        solver.addConstraint(equals(x, 20.0))
        solver.addConstraint(equals(add(x, 2.0), add(y, 10.0)))
        solver.updateVariables()
        println("x " + x.value + " y " + y.value)
        assertEquals(y.value, 12.0, EPSILON)
        assertEquals(x.value, 20.0, EPSILON)
    }

    @Test
    fun simple1() {
        val x = Variable("x")
        val y = Variable("y")
        val solver = Solver()
        solver.addConstraint(equals(x, y))
        solver.updateVariables()
        assertEquals(x.value, y.value, EPSILON)
    }

    @Test
    fun casso1() {
        val x = Variable("x")
        val y = Variable("y")
        val solver = Solver()
        solver.addConstraint(lessThanOrEqualTo(x, y))
        solver.addConstraint(equals(y, add(x, 3.0)))
        solver.addConstraint(equals(x, 10.0).setStrength(Strength.WEAK))
        solver.addConstraint(equals(y, 10.0).setStrength(Strength.WEAK))
        solver.updateVariables()
        if (abs(x.value - 10.0) < EPSILON) {
            assertEquals(10.0, x.value, EPSILON)
            assertEquals(13.0, y.value, EPSILON)
        } else {
            assertEquals(7.0, x.value, EPSILON)
            assertEquals(10.0, y.value, EPSILON)
        }
    }

    @Test
    fun addDelete1() {
        val x = Variable("x")
        val solver = Solver()
        solver.addConstraint(lessThanOrEqualTo(x, 100.0).setStrength(Strength.WEAK))
        solver.updateVariables()
        assertEquals(100.0, x.value, EPSILON)
        val c10 = lessThanOrEqualTo(x, 10.0)
        val c20 = lessThanOrEqualTo(x, 20.0)
        solver.addConstraint(c10)
        solver.addConstraint(c20)
        solver.updateVariables()
        assertEquals(10.0, x.value, EPSILON)
        solver.removeConstraint(c10)
        solver.updateVariables()
        assertEquals(20.0, x.value, EPSILON)
        solver.removeConstraint(c20)
        solver.updateVariables()
        assertEquals(100.0, x.value, EPSILON)
        val c10again = lessThanOrEqualTo(x, 10.0)
        solver.addConstraint(c10again)
        solver.addConstraint(c10)
        solver.updateVariables()
        assertEquals(10.0, x.value, EPSILON)
        solver.removeConstraint(c10)
        solver.updateVariables()
        assertEquals(10.0, x.value, EPSILON)
        solver.removeConstraint(c10again)
        solver.updateVariables()
        assertEquals(100.0, x.value, EPSILON)
    }

    @Test
    fun addDelete2() {
        val x = Variable("x")
        val y = Variable("y")
        val solver = Solver()
        solver.addConstraint(equals(x, 100.0).setStrength(Strength.WEAK))
        solver.addConstraint(equals(y, 120.0).setStrength(Strength.STRONG))
        val c10 = lessThanOrEqualTo(x, 10.0)
        val c20 = lessThanOrEqualTo(x, 20.0)
        solver.addConstraint(c10)
        solver.addConstraint(c20)
        solver.updateVariables()
        assertEquals(10.0, x.value, EPSILON)
        assertEquals(120.0, y.value, EPSILON)
        solver.removeConstraint(c10)
        solver.updateVariables()
        assertEquals(20.0, x.value, EPSILON)
        assertEquals(120.0, y.value, EPSILON)
        val cxy = equals(multiply(x, 2.0), y)
        solver.addConstraint(cxy)
        solver.updateVariables()
        assertEquals(20.0, x.value, EPSILON)
        assertEquals(40.0, y.value, EPSILON)
        solver.removeConstraint(c20)
        solver.updateVariables()
        assertEquals(60.0, x.value, EPSILON)
        assertEquals(120.0, y.value, EPSILON)
        solver.removeConstraint(cxy)
        solver.updateVariables()
        assertEquals(100.0, x.value, EPSILON)
        assertEquals(120.0, y.value, EPSILON)
    }

    @Test
    fun inconsistent1() {
        assertFailsWith(UnsatisfiableConstraintException::class) {
            val x = Variable("x")
            val solver = Solver()
            solver.addConstraint(equals(x, 10.0))
            solver.addConstraint(equals(x, 5.0))
            solver.updateVariables()
        }
    }

    @Test
    fun inconsistent2() {
        assertFailsWith(UnsatisfiableConstraintException::class) {
            val x = Variable("x")
            val solver = Solver()
            solver.addConstraint(greaterThanOrEqualTo(x, 10.0))
            solver.addConstraint(lessThanOrEqualTo(x, 5.0))
            solver.updateVariables()
        }
    }

    @Test
    fun inconsistent3() {
        assertFailsWith(UnsatisfiableConstraintException::class) {
            val w = Variable("w")
            val x = Variable("x")
            val y = Variable("y")
            val z = Variable("z")
            val solver = Solver()
            solver.addConstraint(greaterThanOrEqualTo(w, 10.0))
            solver.addConstraint(greaterThanOrEqualTo(x, w))
            solver.addConstraint(greaterThanOrEqualTo(y, x))
            solver.addConstraint(greaterThanOrEqualTo(z, y))
            solver.addConstraint(greaterThanOrEqualTo(z, 8.0))
            solver.addConstraint(lessThanOrEqualTo(z, 4.0))
            solver.updateVariables()
        }
    }

    companion object {
        private const val EPSILON = 1.0e-8
    }
}
