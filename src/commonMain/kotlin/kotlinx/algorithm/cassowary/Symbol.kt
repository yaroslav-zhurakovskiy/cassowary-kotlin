package kotlinx.algorithm.cassowary

class Symbol constructor(val type: SymbolType) {
    constructor(): this(type = SymbolType.INVALID)
}

inline class SymbolType(private val rawValue: Int) {
    companion object {
        val INVALID = SymbolType(0)
        val EXTERNAL = SymbolType(1)
        val SLACK = SymbolType(2)
        val ERROR = SymbolType(3)
        val DUMMY = SymbolType(4)
    }
}
