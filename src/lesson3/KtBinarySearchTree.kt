package lesson3

import java.util.*
import kotlin.NoSuchElementException
import kotlin.math.max

// attention: Comparable is supported but Comparator is not
class KtBinarySearchTree<T : Comparable<T>> : AbstractMutableSet<T>(), CheckableSortedSet<T> {

    private class Node<T>(
        val value: T
    ) {
        var left: Node<T>? = null
        var right: Node<T>? = null
    }

    private var root: Node<T>? = null

    override var size = 0
        private set

    private fun find(value: T): Node<T>? =
        root?.let { find(it, value) }

    private fun find(start: Node<T>, value: T): Node<T> {
        val comparison = value.compareTo(start.value)
        return when {
            comparison == 0 -> start
            comparison < 0 -> start.left?.let { find(it, value) } ?: start
            else -> start.right?.let { find(it, value) } ?: start
        }
    }

    private fun findWithParent(value: T): Pair<Node<T>?, Node<T>?> {
        var child = root ?: return null to null
        var parent: Node<T>? = null
        while (true) {
            val comparison = value.compareTo(child.value)
            when {
                comparison == 0 -> return child to parent
                comparison < 0 -> if (child.left != null) {
                    parent = child
                    child = child.left!!
                } else return null to null
                else -> if (child.right != null) {
                    parent = child
                    child = child.right!!
                } else return null to null
            }
        }
    }

    override operator fun contains(element: T): Boolean {
        val closest = find(element)
        return closest != null && element.compareTo(closest.value) == 0
    }

    /**
     * Добавление элемента в дерево
     *
     * Если элемента нет в множестве, функция добавляет его в дерево и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     *
     * Спецификация: [java.util.Set.add] (Ctrl+Click по add)
     *
     * Пример
     */
    override fun add(element: T): Boolean {
        val closest = find(element)
        val comparison = if (closest == null) -1 else element.compareTo(closest.value)
        if (comparison == 0) {
            return false
        }
        val newNode = Node(element)
        when {
            closest == null -> root = newNode
            comparison < 0 -> {
                assert(closest.left == null)
                closest.left = newNode
            }
            else -> {
                assert(closest.right == null)
                closest.right = newNode
            }
        }
        size++
        return true
    }

    /**
     * Удаление элемента из дерева
     *
     * Если элемент есть в множестве, функция удаляет его из дерева и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     * Высота дерева не должна увеличиться в результате удаления.
     *
     * Спецификация: [java.util.Set.remove] (Ctrl+Click по remove)
     * (в Котлине тип параметра изменён с Object на тип хранимых в дереве данных)
     *
     * Средняя
     */
    override fun remove(element: T): Boolean {
        // трудоёмкость: O(BST.height)
        // ресурсоёмкость: O(1)
        val closest = findWithParent(element)
        return remove(closest.first, closest.second)
    }

    private fun remove(nodeToRemove: Node<T>?, parent: Node<T>?): Boolean {
        // трудоёмкость: O(1)
        // ресурсоёмкость: O(1)
        if (nodeToRemove == null) return false else
            when {
                nodeToRemove.left == null && nodeToRemove.right == null -> {
                    if (parent != null) {
                        if (parent.left === nodeToRemove) parent.left = null
                        else parent.right = null
                    } else root = null
                }
                (nodeToRemove.left == null && nodeToRemove.right != null) ||
                        (nodeToRemove.left != null && nodeToRemove.right == null) -> {
                    val child = nodeToRemove.left ?: nodeToRemove.right!!
                    if (parent == null) {
                        root = child
                    } else {
                        if (parent.left === nodeToRemove) parent.left = child else parent.right = child
                    }
                }
                else -> {
                    var replacement = nodeToRemove.right!!
                    var replacementParent = nodeToRemove
                    while (replacement.left != null) {
                        replacementParent = replacement
                        replacement = replacement.left!!
                    }
                    if (replacement === replacementParent!!.right) {
                        replacement.left = nodeToRemove.left
                        if (parent != null) {
                            if (parent.left === nodeToRemove) parent.left = replacement else parent.right = replacement
                        } else root = replacement
                    } else {
                        replacementParent!!.left = replacement.right
                        replacement.right = nodeToRemove.right
                        replacement.left = nodeToRemove.left
                        if (parent != null) {
                            if (parent.left === nodeToRemove) parent.left = replacement else parent.right = replacement
                        } else root = replacement
                    }
                }
            }
        nodeToRemove.left = null
        nodeToRemove.right = null
        size--
        return true
    }

    override fun comparator(): Comparator<in T>? =
        null

    override fun iterator(): MutableIterator<T> =
        BinarySearchTreeIterator()

    inner class BinarySearchTreeIterator internal constructor() : MutableIterator<T> {
        private var next: Node<T>?
        private var parent: Node<T>? = null
        private val stack: Stack<Node<T>> = Stack()

        init {
            // трудоёмкость: O(BST.height)
            // ресурсоёмкость: O(BST.height)
            next = root
            while (next != null) {
                stack.push(next)
                parent = next
                next = next!!.left
            }
        }

        /**
         * Проверка наличия следующего элемента
         *
         * Функция возвращает true, если итерация по множеству ещё не окончена (то есть, если вызов next() вернёт
         * следующий элемент множества, а не бросит исключение); иначе возвращает false.
         *
         * Спецификация: [java.util.Iterator.hasNext] (Ctrl+Click по hasNext)
         *
         * Средняя
         */
        override fun hasNext(): Boolean {
            // трудоёмкость: O(1)
            // ресурсоёмкость: O(1)
            return stack.isNotEmpty()
        }

        /**
         * Получение следующего элемента
         *
         * Функция возвращает следующий элемент множества.
         * Так как BinarySearchTree реализует интерфейс SortedSet, последовательные
         * вызовы next() должны возвращать элементы в порядке возрастания.
         *
         * Бросает NoSuchElementException, если все элементы уже были возвращены.
         *
         * Спецификация: [java.util.Iterator.next] (Ctrl+Click по next)
         *
         * Средняя
         */
        override fun next(): T {
            // трудоёмкость: O(BST.height)
            // ресурсоёмкость: O(1) (учёл ресурсы в блоке init)
            if (!hasNext()) throw NoSuchElementException()
            var node = stack.pop()
            parent = when {
                stack.isNotEmpty() -> stack.peek()
                next?.right === node -> next
                else -> null
            }
            next = node
            if (node.right != null) {
                node = node.right
                while (node != null) {
                    stack.push(node)
                    node = node.left
                }
            }
            return next!!.value
        }

        /**
         * Удаление предыдущего элемента
         *
         * Функция удаляет из множества элемент, возвращённый крайним вызовом функции next().
         *
         * Бросает IllegalStateException, если функция была вызвана до первого вызова next() или же была вызвана
         * более одного раза после любого вызова next().
         *
         * Спецификация: [java.util.Iterator.remove] (Ctrl+Click по remove)
         *
         * Сложная
         */
        override fun remove() {
            // трудоёмкость: O(1)
            // ресурсоёмкость: O(1)
            check(next != null)
            check(this@KtBinarySearchTree.remove(next, parent))
            next = null
        }

    }

    /**
     * Подмножество всех элементов в диапазоне [fromElement, toElement)
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева, которые
     * больше или равны fromElement и строго меньше toElement.
     * При равенстве fromElement и toElement возвращается пустое множество.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: [java.util.SortedSet.subSet] (Ctrl+Click по subSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Очень сложная (в том случае, если спецификация реализуется в полном объёме)
     */
    override fun subSet(fromElement: T, toElement: T): SortedSet<T> {
        require(fromElement <= toElement)
        return SubSet(this, fromElement, toElement)
    }

    private class SubSet<T : Comparable<T>>(
        private val tree: KtBinarySearchTree<T>,
        private val fromElement: T?,
        private val toElement: T?
    ) : TreeSet<T>() {

        override val size: Int
            get() = iterator().asSequence().count()

        override fun contains(element: T): Boolean {
            return inInterval(element) && tree.contains(element)
        }

        override fun add(element: T): Boolean {
            require(inInterval(element))
            return tree.add(element)
        }

        override fun remove(element: T): Boolean {
            require(inInterval(element))
            return tree.remove(element)
        }

        override fun comparator(): Comparator<in T>? = null

        inner class SubSetIterator internal constructor() : MutableIterator<T> {
            private val treeIterator = tree.iterator()

            private var next = if (treeIterator.hasNext()) treeIterator.next() else null
            private var current: T? = null

            init {
                while (next != null && !inInterval(next!!) && treeIterator.hasNext()) {
                    next = treeIterator.next()
                }
                if (next != null && !inInterval(next!!)) next = null
            }

            override fun hasNext(): Boolean {
                return next != null
            }

            override fun next(): T {
                if (!hasNext()) throw NoSuchElementException()
                current = next
                next = if (treeIterator.hasNext()) treeIterator.next() else null
                while (next != null && !inInterval(next!!) && treeIterator.hasNext()) {
                    next = treeIterator.next()
                }
                if (next != null && !inInterval(next!!)) next = null
                return current!!
            }

            override fun remove() {
                check(current != null)
                treeIterator.remove()
            }

        }

        override fun iterator(): MutableIterator<T> {
            return SubSetIterator()
        }

        private fun inInterval(element: T): Boolean = when {
            fromElement != null && toElement != null -> {
                element >= fromElement && element < toElement
            }
            fromElement != null -> {
                element >= fromElement
            }
            else -> {
                element < toElement!!
            }
        }

        override fun first(): T {
            return iterator().asSequence().first()
        }

        override fun last(): T {
            return iterator().asSequence().last()
        }

    }

    /**
     * Подмножество всех элементов строго меньше заданного
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева строго меньше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: [java.util.SortedSet.headSet] (Ctrl+Click по headSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Сложная
     */
    override fun headSet(toElement: T): SortedSet<T> {
        return SubSet(this, null, toElement)
    }

    /**
     * Подмножество всех элементов нестрого больше заданного
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева нестрого больше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: [java.util.SortedSet.tailSet] (Ctrl+Click по tailSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Сложная
     */
    override fun tailSet(fromElement: T): SortedSet<T> {
        return SubSet(this, fromElement, null)
    }

    override fun first(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.left != null) {
            current = current.left!!
        }
        return current.value
    }

    override fun last(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.right != null) {
            current = current.right!!
        }
        return current.value
    }

    override fun height(): Int =
        height(root)

    private fun height(node: Node<T>?): Int {
        if (node == null) return 0
        return 1 + max(height(node.left), height(node.right))
    }

    override fun checkInvariant(): Boolean =
        root?.let { checkInvariant(it) } ?: true

    private fun checkInvariant(node: Node<T>): Boolean {
        val left = node.left
        if (left != null && (left.value >= node.value || !checkInvariant(left))) return false
        val right = node.right
        return right == null || right.value > node.value && checkInvariant(right)
    }

}