package kotlinx.algorithm.cassowary

internal class LinkedList<E> {
    private var head: Node<E>? = null
    private var tail: Node<E>? = null

    fun headOrNull(): E? {
        return head?.value
    }

    fun tailOrNull(): E? {
        return tail?.value
    }

    fun isEmpty(): Boolean {
        return headOrNull() == null
    }

    fun isNotEmpty(): Boolean {
        return !isEmpty()
    }

    fun add(element: E) {
        if (head == null && tail == null) {
            val node = Node(next = null, value = element, previous = head)
            head = node
            tail = node
        } else {
            val oldTail = tail
            tail = Node(next = null, value = element, previous = oldTail)
            oldTail?.next = tail // TODO: Add tests for this condition
        }
    }

    fun pop(): E? {
        val actualTail = tail ?: return null

        val previousNode = actualTail.previous
        if (previousNode == null) {
            head = null
            tail = null
        } else {
            previousNode.next = null
            tail = previousNode
        }

        return actualTail.value
    }

    fun clear() {
        while (isNotEmpty()) {
            pop()
        }
    }

    private class Node<E>(
        val value: E,
        val previous: Node<E>?,
        var next: Node<E>?
    )
}
