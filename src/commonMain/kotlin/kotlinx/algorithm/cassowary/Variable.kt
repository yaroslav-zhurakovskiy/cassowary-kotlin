package kotlinx.algorithm.cassowary

class Variable(
    val name: String?,
    var value: Double
) {
    constructor(value: Double): this(value = value, name = null)
    constructor(name: String): this(name = name, value = 0.0)

    override fun toString(): String {
        return "name: $name value: $value"
    }
}
