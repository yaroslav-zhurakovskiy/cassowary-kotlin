package kotlinx.algorithm.cassowary.tests

import kotlinx.algorithm.cassowary.*
import kotlinx.algorithm.cassowary.tests.ConstraintParser.CassowaryVariableResolver
import kotlinx.algorithm.cassowary.tests.ConstraintParser.parseConstraint
import kotlinx.algorithm.cassowary.Symbolics.add
import kotlinx.algorithm.cassowary.Symbolics.equals
import kotlin.test.*
import kotlinx.algorithm.cassowary.assert.*
import kotlin.time.*

@ExperimentalTime
class RealWorldTests {
    @Test
    fun testGridLayout() {
        val solver = Solver()
        val nodeHashMap = HashMap<String, HashMap<String, Variable>>()
        val variableResolver = createVariableResolver(solver, nodeHashMap)
        for (constraint in CONSTRAINTS) {
            val con = parseConstraint(constraint, variableResolver)
            solver.addConstraint(con)
        }
        solver.addConstraint(parseConstraint("container.width == 300", variableResolver))
        solver.addConstraint(parseConstraint("title0.intrinsicHeight == 100", variableResolver))
        solver.addConstraint(parseConstraint("title1.intrinsicHeight == 110", variableResolver))
        solver.addConstraint(parseConstraint("title2.intrinsicHeight == 120", variableResolver))
        solver.addConstraint(parseConstraint("title3.intrinsicHeight == 130", variableResolver))
        solver.addConstraint(parseConstraint("title4.intrinsicHeight == 140", variableResolver))
        solver.addConstraint(parseConstraint("title5.intrinsicHeight == 150", variableResolver))
        solver.addConstraint(parseConstraint("more.intrinsicHeight == 160", variableResolver))
        solver.updateVariables()
        assertEquals(20.0, nodeHashMap["thumb0"]!!["top"]!!.value, EPSILON)
        assertEquals(20.0, nodeHashMap["thumb1"]!!["top"]!!.value, EPSILON)
        assertEquals(85.0, nodeHashMap["title0"]!!["top"]!!.value, EPSILON)
        assertEquals(85.0, nodeHashMap["title1"]!!["top"]!!.value, EPSILON)
        assertEquals(210.0, nodeHashMap["thumb2"]!!["top"]!!.value, EPSILON)
        assertEquals(210.0, nodeHashMap["thumb3"]!!["top"]!!.value, EPSILON)
        assertEquals(275.0, nodeHashMap["title2"]!!["top"]!!.value, EPSILON)
        assertEquals(275.0, nodeHashMap["title3"]!!["top"]!!.value, EPSILON)
        assertEquals(420.0, nodeHashMap["thumb4"]!!["top"]!!.value, EPSILON)
        assertEquals(420.0, nodeHashMap["thumb5"]!!["top"]!!.value, EPSILON)
        assertEquals(485.0, nodeHashMap["title4"]!!["top"]!!.value, EPSILON)
        assertEquals(485.0, nodeHashMap["title5"]!!["top"]!!.value, EPSILON)
    }

    @Test
    fun testGridX1000() {
        val time = measureTime {
            for (i in 0..999) {
                testGridLayout()
            }
        }
        println("testGridX1000 took " + time.toString(DurationUnit.MILLISECONDS))
    }

    private fun createVariableResolver(solver: Solver, nodeHashMap: HashMap<String, HashMap<String, Variable>>): CassowaryVariableResolver {
        return object : CassowaryVariableResolver {
            private fun getVariableFromNode(node: HashMap<String, Variable>, variableName: String): Variable? {
                try {
                    return if (node.containsKey(variableName)) {
                        node[variableName]
                    } else {
                        val variable = Variable(variableName)
                        node[variableName] = variable
                        if (RIGHT == variableName) {
                            solver.addConstraint(equals(variable, add(getVariableFromNode(node, LEFT)!!, getVariableFromNode(node, WIDTH)!!)))
                        } else if (BOTTOM == variableName) {
                            solver.addConstraint(equals(variable, add(getVariableFromNode(node, TOP)!!, getVariableFromNode(node, HEIGHT)!!)))
                        } else if (CENTERX == variableName) {
                            // solver.addConstraint(Symbolics.equals(variable, Symbolics.add(Symbolics.divide(getVariableFromNode(node, WIDTH), 2), getVariableFromNode(node, LEFT)));
                        } else if (CENTERY == variableName) {
                            // solver.addConstraint(Symbolics.equals(variable, Symbolics.add(new Expression(Symbolics.divide(getVariableFromNode(node, HEIGHT), 2)), getVariableFromNode(node, TOP));
                        }
                        variable
                    }
                } catch (e: DuplicateConstraintException) {
                    e.printStackTrace()
                } catch (e: UnsatisfiableConstraintException) {
                    e.printStackTrace()
                }
                return null
            }

            private fun getNode(nodeName: String): HashMap<String, Variable> {
                val node: HashMap<String, Variable>
                if (nodeHashMap.containsKey(nodeName)) {
                    node = nodeHashMap[nodeName]!!
                } else {
                    node = HashMap()
                    nodeHashMap[nodeName] = node
                }
                return node
            }

            override fun resolveVariable(variableName: String): Variable {
                val stringArray = variableName.split(".").toTypedArray()
                return if (stringArray.size == 2) {
                    val nodeName = stringArray[0]
                    val propertyName = stringArray[1]
                    val node = getNode(nodeName)
                    getVariableFromNode(node, propertyName)!!
                } else {
                    throw RuntimeException("can't resolve variable")
                }
            }

            override fun resolveConstant(name: String): Expression? {
                return try {
                    Expression(name.toDouble())
                } catch (e: NumberFormatException) {
                    null
                }
            }
        }
    }

    private companion object {
        private const val EPSILON = 1.0e-2
        const val LEFT = "left"
        const val RIGHT = "right"
        const val TOP = "top"
        const val BOTTOM = "bottom"
        const val HEIGHT = "height"
        const val WIDTH = "width"
        const val CENTERX = "centerX"
        const val CENTERY = "centerY"
        private val CONSTRAINTS = arrayOf(
            "container.columnWidth == container.width * 0.4",
            "container.thumbHeight == container.columnWidth / 2",
            "container.padding == container.width * (0.2 / 3)",
            "container.leftPadding == container.padding",
            "container.rightPadding == container.width - container.padding",
            "container.paddingUnderThumb == 5",
            "container.rowPadding == 15",
            "container.buttonPadding == 20",
            "thumb0.left == container.leftPadding",
            "thumb0.top == container.padding",
            "thumb0.height == container.thumbHeight",
            "thumb0.width == container.columnWidth",
            "title0.left == container.leftPadding",
            "title0.top == thumb0.bottom + container.paddingUnderThumb",
            "title0.height == title0.intrinsicHeight",
            "title0.width == container.columnWidth",
            "thumb1.right == container.rightPadding",
            "thumb1.top == container.padding",
            "thumb1.height == container.thumbHeight",
            "thumb1.width == container.columnWidth",
            "title1.right == container.rightPadding",
            "title1.top == thumb0.bottom + container.paddingUnderThumb",
            "title1.height == title1.intrinsicHeight",
            "title1.width == container.columnWidth",
            "thumb2.left == container.leftPadding",
            "thumb2.top >= title0.bottom + container.rowPadding",
            "thumb2.top == title0.bottom + container.rowPadding !weak",
            "thumb2.top >= title1.bottom + container.rowPadding",
            "thumb2.top == title1.bottom + container.rowPadding !weak",
            "thumb2.height == container.thumbHeight",
            "thumb2.width == container.columnWidth",
            "title2.left == container.leftPadding",
            "title2.top == thumb2.bottom + container.paddingUnderThumb",
            "title2.height == title2.intrinsicHeight",
            "title2.width == container.columnWidth",
            "thumb3.right == container.rightPadding",
            "thumb3.top == thumb2.top",
            "thumb3.height == container.thumbHeight",
            "thumb3.width == container.columnWidth",
            "title3.right == container.rightPadding",
            "title3.top == thumb3.bottom + container.paddingUnderThumb",
            "title3.height == title3.intrinsicHeight",
            "title3.width == container.columnWidth",
            "thumb4.left == container.leftPadding",
            "thumb4.top >= title2.bottom + container.rowPadding",
            "thumb4.top >= title3.bottom + container.rowPadding",
            "thumb4.top == title2.bottom + container.rowPadding !weak",
            "thumb4.top == title3.bottom + container.rowPadding !weak",
            "thumb4.height == container.thumbHeight",
            "thumb4.width == container.columnWidth",
            "title4.left == container.leftPadding",
            "title4.top == thumb4.bottom + container.paddingUnderThumb",
            "title4.height == title4.intrinsicHeight",
            "title4.width == container.columnWidth",
            "thumb5.right == container.rightPadding",
            "thumb5.top == thumb4.top",
            "thumb5.height == container.thumbHeight",
            "thumb5.width == container.columnWidth",
            "title5.right == container.rightPadding",
            "title5.top == thumb5.bottom + container.paddingUnderThumb",
            "title5.height == title5.intrinsicHeight",
            "title5.width == container.columnWidth",
            "line.height == 1",
            "line.width == container.width",
            "line.top >= title4.bottom + container.rowPadding",
            "line.top >= title5.bottom + container.rowPadding",
            "more.top == line.bottom + container.buttonPadding",
            "more.height == more.intrinsicHeight",
            "more.left == container.leftPadding",
            "more.right == container.rightPadding",
            "container.height == more.bottom + container.buttonPadding"
        )

        private fun printNodes(variableHashMap: HashMap<String, HashMap<String, Variable>>) {
            val it: Iterator<Map.Entry<String, HashMap<String, Variable>>> = variableHashMap.entries.iterator()
            while (it.hasNext()) {
                val pairs = it.next()
                println("node " + pairs.key)
                printVariables(pairs.value)
            }
        }

        private fun printVariables(variableHashMap: HashMap<String, Variable>) {
            val it: Iterator<Map.Entry<String, Variable>> = variableHashMap.entries.iterator()
            while (it.hasNext()) {
                val pairs = it.next()
                println(" " + pairs.key + " = " + pairs.value.value + " (address:" + pairs.value.hashCode() + ")")
            }
        }
    }
}
