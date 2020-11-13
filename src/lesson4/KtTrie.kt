package lesson4

import java.util.*
import kotlin.NoSuchElementException

/**
 * Префиксное дерево для строк
 */
class KtTrie : AbstractMutableSet<String>(), MutableSet<String> {

    private class Node {
        val children: MutableMap<Char, Node> = linkedMapOf()
        var parent: Node? = null
    }

    private var root = Node()

    override var size: Int = 0
        private set

    override fun clear() {
        root.children.clear()
        size = 0
    }

    private fun String.withZero() = this + 0.toChar()

    private fun findNode(element: String): Node? {
        var current = root
        for (char in element) {
            current = current.children[char] ?: return null
        }
        return current
    }

    override fun contains(element: String): Boolean =
        findNode(element.withZero()) != null

    override fun add(element: String): Boolean {
        var current = root
        var modified = false
        for (char in element.withZero()) {
            val child = current.children[char]
            if (child != null) {
                current = child
            } else {
                modified = true
                val newChild = Node()
                newChild.parent = current
                current.children[char] = newChild
                current = newChild
            }
        }
        if (modified) {
            size++
        }
        return modified
    }

    override fun remove(element: String): Boolean {
        val current = findNode(element) ?: return false
        if (current.children.remove(0.toChar()) != null) {
            size--
            return true
        }
        return false
    }

    /**
     * Итератор для префиксного дерева
     *
     * Спецификация: [java.util.Iterator] (Ctrl+Click по Iterator)
     *
     * Сложная
     */
    override fun iterator(): MutableIterator<String> {
        return TrieIterator()
    }

    inner class TrieIterator internal constructor() : MutableIterator<String> {
        private var next: String?
        private var current: String? = null
        private val buffer = StringBuilder()
        private val deque: Deque<Iterator<Map.Entry<Char, Node>>> = ArrayDeque()
        private lateinit var nextParent: MutableMap<Char, Node>
        private lateinit var currentParent: MutableMap<Char, Node>

        init {
            // трудоёмкость: O(length of first word in trie (1 if it is empty string)) - is average
            // O(length of all words (if words were added and then all of them were removed)) - is worst
            // ресурсоёмкость: O(1)
            deque.push(root.children.entries.iterator())
            next = findNext()
        }

        private fun findNext(): String? {
            // трудоёмкость: O(length of first word in trie (1 if it is empty string)) - is average
            // O(length of all words (if words were added and then all of them were removed)) - is worst
            // ресурсоёмкость: O(length of the longest word)
            var iterator: Iterator<Map.Entry<Char, Node>>? = deque.peek()
            while (iterator != null) {
                while (iterator!!.hasNext()) {
                    val entry = iterator.next()
                    val key = entry.key
                    val value = entry.value
                    if (key == 0.toChar()) {
                        nextParent = (value.parent ?: root).children
                        return buffer.toString()
                    }
                    buffer.append(key)
                    iterator = value.children.entries.iterator()
                    deque.push(iterator)
                }
                deque.pop()
                if (buffer.isNotEmpty()) buffer.deleteCharAt(buffer.length - 1)
                iterator = deque.peek()
            }
            return null
        }

        override fun hasNext(): Boolean {
            // трудоёмкость: O(1)
            // ресурсоёмкость: O(1)
            return next != null
        }

        override fun next(): String {
            // ресурсоёмкость: O(1)
            // трудоёмксость: similar to findNext()
            if (!hasNext()) throw NoSuchElementException()
            current = next!!
            currentParent = nextParent
            next = findNext()
            return current!!
        }

        override fun remove() {
            // ресурсоёмкость: O(1)
            // трудоёмксость: similar to findNext()
            check(current != null)
            check(currentParent.remove(0.toChar()) != null)
            current = null
            size--
        }

    }

}