package kotlinx.algorithm.cassowary

class UnsatisfiableConstraintException(private val constraint: Constraint) : RuntimeException(constraint.toString())
