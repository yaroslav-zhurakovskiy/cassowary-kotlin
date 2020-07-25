package kotlinx.algorithm.cassowary.benchmarks

import kotlinx.algorithm.cassowary.*
import kotlinx.algorithm.cassowary.Symbolics.add
import kotlinx.algorithm.cassowary.Symbolics.equals
import kotlinx.algorithm.cassowary.assert.assertEquals
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime
private fun main() {
    val time = measureTime {
        runTest()
    }
    println("KiwiJS Benchmark time is ${time.toString(DurationUnit.MILLISECONDS)}")
}

private fun runTest() {
    var solver = Solver()
    var strength = Strength.create(0.0, 900.0, 1000.0)

    // super-view
    val superView = View()
    solver.addConstraint(equals(superView.left, 0.0))
    solver.addConstraint(equals(superView.top, 0.0))
    solver.addConstraint(equals(superView.right, add(superView.left, superView.width)))
    solver.addConstraint(equals(superView.bottom, add(superView.top, superView.height)))

    solver.addEditVariable(superView.width, Strength.create(999.0, 1000.0, 1000.0));
    solver.addEditVariable(superView.height, Strength.create(999.0, 1000.0, 1000.0));
    solver.suggestValue(superView.width, 300.0);
    solver.suggestValue(superView.height, 200.0);

    // subView1
    var subView1 = View()
    solver.addConstraint(equals(subView1.right, add(subView1.left, subView1.width)))
    solver.addConstraint(equals(subView1.bottom, add(subView1.top, subView1.height)))

    // subView2
    var subView2 = View()
    solver.addConstraint(equals(subView2.right, add(subView2.left, (subView2.width))))
    solver.addConstraint(equals(subView2.bottom, add(subView2.top, (subView2.height))))

    // Position sub-views in super-view
    solver.addConstraint(equals(subView1.left, superView.left).setStrength(strength))
    solver.addConstraint(equals(subView1.top, superView.top).setStrength(strength))
    solver.addConstraint(equals(subView1.bottom, superView.bottom).setStrength(strength))
    solver.addConstraint(equals(subView1.width, superView.width).setStrength(strength))
    solver.addConstraint(equals(subView1.right, superView.left).setStrength(strength))
    solver.addConstraint(equals(subView1.right, superView.right).setStrength(strength))
    solver.addConstraint(equals(subView1.top, superView.top).setStrength(strength))
    solver.addConstraint(equals(subView1.bottom, superView.bottom).setStrength(strength))

    // Calculate
    solver.updateVariables();

    // Uncomment to verify results
    //console.log('superView: ' + JSON.stringify(superView, undefined, 2));
    //console.log('subView1: ' + JSON.stringify(subView1, undefined, 2));
    //console.log('subView2: ' + JSON.stringify(subView2, undefined, 2));
    val EPSILONE = 1.0e-8
    assertEquals(150.0, subView1.width.value, EPSILONE)
    assertEquals(150.0, subView1.left.value, EPSILONE)
}

private class View() {
    val left = Variable(0.0)
    val top = Variable(0.0)
    val width = Variable(0.0)
    val height = Variable(0.0)
    val right = Variable(0.0)
    val bottom = Variable(0.0)
}
