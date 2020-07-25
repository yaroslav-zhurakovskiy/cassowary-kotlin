package kotlinx.algorithm.cassowary.tests

import kotlinx.algorithm.cassowary.LinkedList
import kotlin.test.*

class LinkedListCreationTests {
    private lateinit var list: LinkedList<Int>

    @BeforeTest
    fun setup() {
        list = LinkedList()
    }

    @Test
    fun shouldBeEmpty() {
        assertTrue(list.isEmpty())
        assertFalse(list.isNotEmpty())
    }

    @Test
    fun shouldHaveNoHear() {
        assertNull(list.headOrNull())
    }

    @Test
    fun shouldHaveNoTail() {
        assertNull(list.tailOrNull())
    }
}

internal class LinkedListAddingOneElementTests {
    private lateinit var list: LinkedList<Int>
    private val value = 10

    @BeforeTest
    fun setup() {
        list = LinkedList()
        list.add(value)
    }

    @Test
    fun shouldNotBeEmpty() {
        assertTrue(list.isNotEmpty())
        assertFalse(list.isEmpty())
    }

    @Test
    fun shouldHaveHead() {
        assertNotNull(list.headOrNull())
        assertEquals(value, list.headOrNull()!!)
    }

    @Test
    fun shouldHaveTail() {
        assertNotNull(list.tailOrNull())
        assertEquals(value, list.tailOrNull()!!)
    }
}


internal class LinkedListAddingMultipleElementTests {
    private lateinit var list: LinkedList<Int>
    private val value1 = 10
    private val value2 = 20

    @BeforeTest
    fun setup() {
        list = LinkedList()
        list.add(value1)
        list.add(value2)
    }

    @Test
    fun shouldNotBeEmpty() {
        assertTrue(list.isNotEmpty())
        assertFalse(list.isEmpty())
    }

    @Test
    fun shouldHaveHead() {
        assertNotNull(list.headOrNull())
        assertEquals(value1, list.headOrNull()!!)
    }

    @Test
    fun shouldHaveTail() {
        assertNotNull(list.tailOrNull())
        assertEquals(value2, list.tailOrNull()!!)
    }
}

class LinkedListPoppingLastElementTests {
    private lateinit var list: LinkedList<Int>
    private val value1 = 10
    private val value2 = 20
    private var poppedElement: Int? = null

    @BeforeTest
    fun setup() {
        list = LinkedList()

        list.add(value1)
        list.add(value2)

        poppedElement = list.pop()
    }

    @Test
    fun shouldPopTheLastElement() {
        assertNotNull(poppedElement)
        assertEquals(value2, poppedElement!!)
    }

    @Test
    fun shouldNotBeEmpty() {
        assertTrue(list.isNotEmpty())
        assertFalse(list.isEmpty())
    }

    @Test
    fun shouldHaveHead() {
        assertNotNull(list.headOrNull())
        assertEquals(value1, list.headOrNull()!!)
    }

    @Test
    fun shouldHaveTail() {
        assertNotNull(list.tailOrNull())
        assertEquals(value1, list.tailOrNull()!!)
    }
}

class LinkedListPoppingMultipleElementTests {
    private lateinit var list: LinkedList<Int>
    private val value1 = 10
    private val value2 = 20
    private lateinit var poppedElements: List<Int?>

    @BeforeTest
    fun setup() {
        list = LinkedList()

        list.add(value1)
        list.add(value2)

        poppedElements = listOf(list.pop(), list.pop())
    }

    @Test
    fun shouldPopAllElements() {
        assertEquals(2, poppedElements.size)
        assertEquals(value2, poppedElements[0])
        assertEquals(value1, poppedElements[1])
    }

    @Test
    fun shouldBeEmpty() {
        assertTrue(list.isEmpty())
        assertFalse(list.isNotEmpty())
    }

    @Test
    fun shouldHaveNoHead() {
        assertNull(list.headOrNull())
    }

    @Test
    fun shouldHaveNoTail() {
        assertNull(list.tailOrNull())
    }
}

class LinkedListClearTests {
    private lateinit var list: LinkedList<Int>
    private val value1 = 10
    private val value2 = 20

    @BeforeTest
    fun setup() {
        list = LinkedList()

        list.add(value1)
        list.add(value2)

        list.clear()
    }

    @Test
    fun shouldBeEmpty() {
        assertTrue(list.isEmpty())
        assertFalse(list.isNotEmpty())
    }

    @Test
    fun shouldHaveNoHead() {
        assertNull(list.headOrNull())
    }

    @Test
    fun shouldHaveNoTail() {
        assertNull(list.tailOrNull())
    }
}
