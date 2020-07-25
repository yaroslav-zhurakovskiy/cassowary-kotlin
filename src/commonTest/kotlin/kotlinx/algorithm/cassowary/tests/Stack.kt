package kotlinx.algorithm.cassowary.tests

import kotlinx.algorithm.cassowary.LinkedList

internal class Stack<E> {
    private val linkedList = LinkedList<E>()

    fun push(element: E) {
        linkedList.add(element)
    }

    fun isEmpty(): Boolean {
        return linkedList.isEmpty()
    }

    fun clear() {
        linkedList.clear()
    }

    fun peek(): E {
        return linkedList.tailOrNull() ?: throw EmptyStackError()
    }

    fun pop(): E {
        return linkedList.pop() ?: throw EmptyStackError()
    }
}

internal class EmptyStackError: Error("Stack is empty")
