package kotlinx.algorithm.cassowary

class Row {
    var constant: Double
    private var _cells = mutableMapOf<Symbol, Double>()
    val cells: Map<Symbol, Double> get() = _cells

    constructor(constant: Double) {
        this.constant = constant
    }

    constructor(other: Row) {
        _cells = other._cells.toMutableMap()
        constant = other.constant
    }

    /**
     * Add a constant value to the row constant.
     *
     * @return The new value of the constant
     */
    fun add(value: Double): Double {
        constant += value
        return constant
    }
    /**
     * Insert a symbol into the row with a given coefficient.
     *
     *
     * If the symbol already exists in the row, the coefficient will be
     * added to the existing coefficient. If the resulting coefficient
     * is zero, the symbol will be removed from the row
     */
    /**
     * Insert a symbol into the row with a given coefficient.
     *
     *
     * If the symbol already exists in the row, the coefficient will be
     * added to the existing coefficient. If the resulting coefficient
     * is zero, the symbol will be removed from the row
     */
    fun insert(symbol: Symbol, coefficient: Double = 1.0) {
        val finalCoefficient = coefficient + (_cells[symbol] ?: 0.0)
        if (Util.nearZero(finalCoefficient)) {
            _cells.remove(symbol)
        } else {
            _cells[symbol] = finalCoefficient
        }
    }
    /**
     * Insert a row into this row with a given coefficient.
     * The constant and the cells of the other row will be multiplied by
     * the coefficient and added to this row. Any cell with a resulting
     * coefficient of zero will be removed from the row.
     *
     * @param other
     * @param coefficient
     */
    /**
     * Insert a row into this row with a given coefficient.
     * The constant and the cells of the other row will be multiplied by
     * the coefficient and added to this row. Any cell with a resulting
     * coefficient of zero will be removed from the row.
     *
     * @param other
     */
    fun insert(other: Row, coefficient: Double = 1.0) {
        constant += other.constant * coefficient
        for ((symbol, otherValue) in other._cells) {
            val thisValue = _cells[symbol] ?: 0.0
            val coeff = otherValue * coefficient
            val newValue = thisValue + coeff
            if (Util.nearZero(newValue)) {
                _cells.remove(symbol)
            } else {
                _cells[symbol] = newValue
            }
        }
    }

    /**
     * Remove the given symbol from the row.
     */
    fun remove(symbol: Symbol) {
        _cells.remove(symbol)
    }

    /**
     * Reverse the sign of the constant and all cells in the row.
     */
    fun reverseSign() {
        constant = -constant
        val newCells = mutableMapOf<Symbol, Double>()
        for ((symbol, value) in _cells) {
            newCells[symbol] = -value
        }
        _cells = newCells
    }

    /**
     * Solve the row for the given symbol.
     *
     *
     * This method assumes the row is of the form a * x + b * y + c = 0
     * and (assuming solve for x) will modify the row to represent the
     * right hand side of x = -b/a * y - c / a. The target symbol will
     * be removed from the row, and the constant and other cells will
     * be multiplied by the negative inverse of the target coefficient.
     * The given symbol *must* exist in the row.
     *
     * @param symbol
     */
    fun solveFor(symbol: Symbol) {
        val coeff = -1.0 / _cells[symbol]!!
        _cells.remove(symbol)
        constant *= coeff

        val newCells = mutableMapOf<Symbol, Double>()
        for ((cellSymbol, cellValue) in _cells) {
            newCells[cellSymbol] = cellValue * coeff
        }
        _cells = newCells
    }

    /**
     * Solve the row for the given symbols.
     *
     *
     * This method assumes the row is of the form x = b * y + c and will
     * solve the row such that y = x / b - c / b. The rhs symbol will be
     * removed from the row, the lhs added, and the result divided by the
     * negative inverse of the rhs coefficient.
     * The lhs symbol *must not* exist in the row, and the rhs symbol
     * must* exist in the row.
     *
     * @param lhs
     * @param rhs
     */
    fun solveFor(lhs: Symbol, rhs: Symbol) {
        insert(lhs, -1.0)
        solveFor(rhs)
    }

    /**
     * Get the coefficient for the given symbol.
     *
     *
     * If the symbol does not exist in the row, zero will be returned.
     *
     * @return
     */
    fun coefficientFor(symbol: Symbol): Double {
        return _cells[symbol] ?: 0.0
    }

    /**
     * Substitute a symbol with the data from another row.
     *
     *
     * Given a row of the form a * x + b and a substitution of the
     * form x = 3 * y + c the row will be updated to reflect the
     * expression 3 * a * y + a * c + b.
     * If the symbol does not exist in the row, this is a no-op.
     */
    fun substitute(symbol: Symbol, row: Row) {
        val coefficient = _cells[symbol] ?: return
        _cells.remove(symbol)
        insert(row, coefficient)
    }
}
