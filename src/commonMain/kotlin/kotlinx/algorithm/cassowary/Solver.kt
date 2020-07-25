package kotlinx.algorithm.cassowary

class Solver {
    private val constraints = mutableMapOf<Constraint, Tag>()
    private val rows = mutableMapOf<Symbol, Row>()
    private val vars = mutableMapOf<Variable, Symbol>()
    private val edits = mutableMapOf<Variable, EditInfo>()
    private val infeasibleRows = mutableListOf<Symbol>()
    private val objective = Row(0.0)
    private var artificial: Row? = null // TODO: It does not belong here

    /**
     * Add a constraint to the solver.
     *
     * @param constraint
     * @throws DuplicateConstraintException The given constraint has already been added to the solver.
     * @throws UnsatisfiableConstraintException The given constraint is required and cannot be satisfied.
     */
    fun addConstraint(constraint: Constraint) {
        if (constraints.containsKey(constraint)) {
            throw DuplicateConstraintException(constraint)
        }
        val tag = Tag()
        val row = createRow(constraint, tag)
        var subject = chooseSubject(row, tag)
        if (subject.type == SymbolType.INVALID && rowContainsOnlyDummies(row)) {
            subject = if (!Util.nearZero(row.constant)) {
                throw UnsatisfiableConstraintException(constraint)
            } else {
                tag.marker
            }
        }
        if (subject.type == SymbolType.INVALID) {
            if (!addWithArtificialVariable(row)) {
                throw UnsatisfiableConstraintException(constraint)
            }
        } else {
            row.solveFor(subject)
            substitute(subject, row)
            rows[subject] = row
        }
        constraints[constraint] = tag
        optimize(objective)
    }

    fun removeConstraint(constraint: Constraint) {
        val tag = constraints[constraint] ?: throw UnknownConstraintException(constraint)
        constraints.remove(constraint)
        removeConstraintEffects(constraint, tag)
        var row = rows[tag.marker]
        if (row != null) {
            rows.remove(tag.marker)
        } else {
            row = getMarkerLeavingRow(tag.marker)
            if (row == null) {
                throw InternalSolverError("internal solver error")
            }

            //This looks wrong! changes made below
            //Symbol leaving = tag.marker;
            //rows.remove(tag.marker);
            var leaving: Symbol? = null
            for ((currentSymbol, currentRow) in rows) {
                if (currentRow === row) {
                    leaving = currentSymbol
                    // TODO: Maybe optimize. Early break
                }
            }
            if (leaving == null) {
                throw InternalSolverError("internal solver error")
            }
            rows.remove(leaving)
            row.solveFor(leaving, tag.marker)
            substitute(tag.marker, row)
        }
        optimize(objective)
    }

    private fun removeConstraintEffects(constraint: Constraint, tag: Tag) {
        if (tag.marker.type == SymbolType.ERROR) {
            removeMarkerEffects(tag.marker, constraint.strength)
        } else if (tag.other.type == SymbolType.ERROR) {
            removeMarkerEffects(tag.other, constraint.strength)
        }
    }

    fun removeMarkerEffects(marker: Symbol, strength: Double) {
        val row = rows[marker]
        if (row != null) {
            objective.insert(row, -strength)
        } else {
            objective.insert(marker, -strength)
        }
    }

    private fun getMarkerLeavingRow(marker: Symbol): Row? {
        var r1 = Double.MAX_VALUE
        var r2 = Double.MAX_VALUE
        var first: Row? = null
        var second: Row? = null
        var third: Row? = null
        for (s in rows.keys) {
            val candidateRow = rows[s]
            val c = candidateRow!!.coefficientFor(marker)
            if (c == 0.0) {
                continue
            }
            if (s.type == SymbolType.EXTERNAL) {
                third = candidateRow
            } else if (c < 0.0) {
                val r = -candidateRow.constant / c
                if (r < r1) {
                    r1 = r
                    first = candidateRow
                }
            } else {
                val r = candidateRow.constant / c
                if (r < r2) {
                    r2 = r
                    second = candidateRow
                }
            }
        }
        return first ?: (second ?: third)
    }

    fun hasConstraint(constraint: Constraint): Boolean {
        return constraints.containsKey(constraint)
    }

    fun addEditVariable(variable: Variable, strength: Double) {
        if (edits.containsKey(variable)) {
            throw DuplicateEditVariableException()
        }
        val clippedStrength = Strength.clip(strength)
        if (clippedStrength == Strength.REQUIRED) {
            throw RequiredFailureException()
        }
        val terms = mutableListOf<Term>()
        terms.add(Term(variable))
        val constraint = Constraint(Expression(terms), RelationalOperator.Equals, clippedStrength)
        // TODO: What is going on here? Why the errors are silence?
        try {
            addConstraint(constraint)
        } catch (e: DuplicateConstraintException) {
            e.printStackTrace()
        } catch (e: UnsatisfiableConstraintException) {
            e.printStackTrace()
        }
        val info = EditInfo(constraint, constraints[constraint], 0.0)
        edits[variable] = info
    }

    fun removeEditVariable(variable: Variable) {
        val edit = edits[variable] ?: throw UnknownEditVariableException()
        try {
            removeConstraint(edit.constraint)
        } catch (e: UnknownConstraintException) {
            e.printStackTrace()
        }
        edits.remove(variable)
    }

    fun hasEditVariable(variable: Variable): Boolean {
        return edits.containsKey(variable)
    }

    fun suggestValue(variable: Variable, value: Double) {
        val info = edits[variable] ?: throw UnknownEditVariableException()
        val delta = value - info.constant
        info.constant = value
        var row = rows[info.tag!!.marker]
        if (row != null) {
            if (row.add(-delta) < 0.0) {
                infeasibleRows.add(info.tag!!.marker)
            }
            dualOptimize()
            return
        }
        row = rows[info.tag!!.other]
        if (row != null) {
            if (row.add(delta) < 0.0) {
                infeasibleRows.add(info.tag!!.other)
            }
            dualOptimize()
            return
        }
        for (s in rows.keys) {
            val currentRow = rows[s]
            val coefficient = currentRow!!.coefficientFor(info.tag!!.marker)
            if (coefficient != 0.0 && currentRow.add(delta * coefficient) < 0.0 && s.type != SymbolType.EXTERNAL) {
                infeasibleRows.add(s)
            }
        }
        dualOptimize()
    }

    /**
     * Update the values of the external solver variables.
     */
    fun updateVariables() {
        for ((variable, value) in vars) {
            val row = rows[value]
            if (row == null) {
                variable.value = 0.0
            } else {
                variable.value = row.constant
            }
        }
    }

    /**
     * Create a new Row object for the given constraint.
     *
     *
     * The terms in the constraint will be converted to cells in the row.
     * Any term in the constraint with a coefficient of zero is ignored.
     * This method uses the `getVarSymbol` method to get the symbol for
     * the variables added to the row. If the symbol for a given cell
     * variable is basic, the cell variable will be substituted with the
     * basic row.
     *
     *
     * The necessary slack and error variables will be added to the row.
     * If the constant for the row is negative, the sign for the row
     * will be inverted so the constant becomes positive.
     *
     *
     * The tag will be updated with the marker and error symbols to use
     * for tracking the movement of the constraint in the tableau.
     */
    private fun createRow(constraint: Constraint, tag: Tag): Row {
        val expression = constraint.expression
        val row = Row(expression.constant)
        for (term in expression.terms) {
            if (!Util.nearZero(term.coefficient)) {
                val symbol = getVarSymbolOrCreateExternal(term.variable)
                val otherRow = rows[symbol]
                if (otherRow == null) {
                    row.insert(symbol, term.coefficient)
                } else {
                    row.insert(otherRow, term.coefficient)
                }
            }
        }
        when (constraint.operator) {
            RelationalOperator.LessThan, RelationalOperator.GreaterThan -> {
                val coeff = if (constraint.operator == RelationalOperator.LessThan) 1.0 else -1.0
                val slack = Symbol(SymbolType.SLACK)
                tag.marker = slack
                row.insert(slack, coeff)
                if (constraint.strength < Strength.REQUIRED) {
                    val error = Symbol(SymbolType.ERROR)
                    tag.other = error
                    row.insert(error, -coeff)
                    objective.insert(error, constraint.strength)
                }
            }
            RelationalOperator.Equals -> {
                if (constraint.strength < Strength.REQUIRED) {
                    val errplus = Symbol(SymbolType.ERROR)
                    val errminus = Symbol(SymbolType.ERROR)
                    tag.marker = errplus
                    tag.other = errminus
                    row.insert(errplus, -1.0) // v = eplus - eminus
                    row.insert(errminus, 1.0) // v - eplus + eminus = 0
                    objective.insert(errplus, constraint.strength)
                    objective.insert(errminus, constraint.strength)
                } else {
                    val dummy = Symbol(SymbolType.DUMMY)
                    tag.marker = dummy
                    row.insert(dummy)
                }
            }
        }

        // Ensure the row as a positive constant.
        if (row.constant < 0.0) {
            row.reverseSign()
        }
        return row
    }

    /**
     * Add the row to the tableau using an artificial variable.
     *
     *
     * This will return false if the constraint cannot be satisfied.
     */
    private fun addWithArtificialVariable(row: Row): Boolean {
        //TODO check this

        // Create and add the artificial variable to the tableau
        val art = Symbol(SymbolType.SLACK)
        rows[art] = Row(row)
        artificial = Row(row)

        // Optimize the artificial objective. This is successful
        // only if the artificial objective is optimized to zero.
        optimize(artificial!!)
        val success = Util.nearZero(artificial!!.constant)
        artificial = null

        // If the artificial variable is basic, pivot the row so that
        // it becomes basic. If the row is constant, exit early.
        val artificialRow = rows[art]
        if (artificialRow != null) {
            val deleteQueue = LinkedList<Symbol>()
            for ((symbol, currentRow) in rows) {
                if (currentRow === artificialRow) {
                    deleteQueue.add(symbol)
                }
            }
            while (deleteQueue.isNotEmpty()) {
                rows.remove(deleteQueue.pop())
            }
            deleteQueue.clear()
            if (artificialRow.cells.isEmpty()) {
                return success
            }
            val entering = anyPivotableSymbol(artificialRow)
            if (entering.type == SymbolType.INVALID) {
                return false // unsatisfiable (will this ever happen?)
            }
            artificialRow.solveFor(art, entering)
            substitute(entering, artificialRow)
            rows[entering] = artificialRow
        }

        // Remove the artificial variable from the tableau.
        for ((_, value) in rows) {
            value.remove(art)
        }
        objective.remove(art)
        return success
    }

    /**
     * Substitute the parametric symbol with the given row.
     *
     *
     * This method will substitute all instances of the parametric symbol
     * in the tableau and the objective function with the given row.
     */
    private fun substitute(symbol: Symbol, row: Row) {
        for ((key, value) in rows) {
            value.substitute(symbol, row)
            if (key.type != SymbolType.EXTERNAL && value.constant < 0.0) {
                infeasibleRows.add(key)
            }
        }
        objective.substitute(symbol, row)
        artificial?.substitute(symbol, row)
    }

    /**
     * Optimize the system for the given objective function.
     *
     *
     * This method performs iterations of Phase 2 of the simplex method
     * until the objective function reaches a minimum.
     *
     * @throws InternalSolverError The value of the objective function is unbounded.
     */
    private fun optimize(objective: Row) {
        while (true) {
            val entering = getEnteringSymbolOrCreateInvalid(objective)
            if (entering.type == SymbolType.INVALID) {
                return
            }
            val entry = getLeavingRow(entering) ?: throw InternalSolverError("The objective is unbounded.")
            var leaving: Symbol? = null
            for (key in rows.keys) {
                if (rows[key] === entry) {
                    leaving = key
                }
            }
            var entryKey: Symbol? = null
            for (key in rows.keys) {
                if (rows[key] === entry) {
                    entryKey = key
                }
            }
            rows.remove(entryKey)
            entry.solveFor(leaving!!, entering)
            substitute(entering, entry)
            rows[entering] = entry
        }
    }

    private fun dualOptimize() {
        while (infeasibleRows.isNotEmpty()) {
            val leaving = infeasibleRows.removeAt(infeasibleRows.size - 1)
            val row = rows[leaving]
            if (row != null && row.constant < 0.0) {
                val entering = getDualEnteringSymbol(row)
                if (entering.type == SymbolType.INVALID) {
                    throw InternalSolverError("internal solver error")
                }
                rows.remove(leaving)
                row.solveFor(leaving, entering)
                substitute(entering, row)
                rows[entering] = row
            }
        }
    }

    private fun getDualEnteringSymbol(row: Row): Symbol {
        var entering = Symbol()
        var ratio = Double.MAX_VALUE
        for ((s, currentCell) in row.cells) {
            if (s.type != SymbolType.DUMMY) {
                if (currentCell > 0.0) {
                    val coefficient = objective.coefficientFor(s)
                    val r = coefficient / currentCell
                    if (r < ratio) {
                        ratio = r
                        entering = s
                    }
                }
            }
        }
        return entering
    }

    /**
     * Get the first Slack or Error symbol in the row.
     *
     *
     * If no such symbol is present, and Invalid symbol will be returned.
     */
    private fun anyPivotableSymbol(row: Row): Symbol {
        var symbol = Symbol()
        for ((key) in row.cells) {
            if (key.type == SymbolType.SLACK || key.type == SymbolType.ERROR) {
                symbol = key
            }
        }
        return symbol
    }

    /**
     * Compute the row which holds the exit symbol for a pivot.
     *
     *
     * This documentation is copied from the C++ version and is outdated
     *
     *
     *
     *
     * This method will return a row in the row map
     * which holds the exit symbol. If no appropriate exit symbol is
     * found, null will be returned. This indicates that
     * the objective function is unbounded.
     */
    private fun getLeavingRow(entering: Symbol): Row? {
        var ratio = Double.MAX_VALUE
        var row: Row? = null
        for ((key, candidateRow) in rows) {
            if (key.type != SymbolType.EXTERNAL) {
                val temp = candidateRow.coefficientFor(entering)
                if (temp < 0) {
                    val tempRatio = -candidateRow.constant / temp
                    if (tempRatio < ratio) {
                        ratio = tempRatio
                        row = candidateRow
                    }
                }
            }
        }
        return row
    }

    private fun getVarSymbolOrCreateExternal(variable: Variable): Symbol {
        var foundSymbol = vars[variable]
        if (foundSymbol == null) {
            foundSymbol = Symbol(SymbolType.EXTERNAL)
            vars[variable] = foundSymbol
        }
        return foundSymbol
    }

    companion object {
        /**
         * Choose the subject for solving for the row
         *
         *
         * This method will choose the best subject for using as the solve
         * target for the row. An invalid symbol will be returned if there
         * is no valid target.
         * The symbols are chosen according to the following precedence:
         * 1) The first symbol representing an external variable.
         * 2) A negative slack or error tag variable.
         * If a subject cannot be found, an invalid symbol will be returned.
         */
        private fun chooseSubject(row: Row, tag: Tag): Symbol {
            for ((key) in row.cells) {
                if (key.type == SymbolType.EXTERNAL) {
                    return key
                }
            }
            if (tag.marker.type == SymbolType.SLACK || tag.marker.type == SymbolType.ERROR) {
                if (row.coefficientFor(tag.marker) < 0.0) return tag.marker
            }
            if ((tag.other.type == SymbolType.SLACK || tag.other.type == SymbolType.ERROR)) {
                if (row.coefficientFor(tag.other) < 0.0) return tag.other
            }
            return Symbol()
        }

        /**
         * Compute the entering variable for a pivot operation.
         *
         *
         * This method will return first symbol in the objective function which
         * is non-dummy and has a coefficient less than zero. If no symbol meets
         * the criteria, it means the objective function is at a minimum, and an
         * invalid symbol is returned.
         */
        private fun getEnteringSymbolOrCreateInvalid(objective: Row): Symbol {
            for ((key, value) in objective.cells) {
                if (key.type != SymbolType.DUMMY && value < 0.0) {
                    return key
                }
            }
            return Symbol()
        }

        private fun rowContainsOnlyDummies(row: Row): Boolean {
            for ((key) in row.cells) {
                if (key.type != SymbolType.DUMMY) {
                    return false
                }
            }
            return true
        }
    }

    private class Tag(
        var marker: Symbol,
        var other: Symbol
    ) {
        constructor(): this(
            marker = Symbol(),
            other = Symbol()
        )
    }

    private class EditInfo(
        var constraint: Constraint,
        var tag: Tag?,
        var constant: Double
    )
}
