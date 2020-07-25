package kotlinx.algorithm.cassowary

class UnknownConstraintException(val constraint: Constraint) : RuntimeException("Unknown constraint $constraint")
