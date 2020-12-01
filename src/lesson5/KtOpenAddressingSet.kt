package lesson5

import kotlin.properties.Delegates

/**
 * Множество(таблица) с открытой адресацией на 2^bits элементов без возможности роста.
 */
class KtOpenAddressingSet<T : Any>(private val bits: Int) : AbstractMutableSet<T>() {
    init {
        require(bits in 2..31)
    }

    private val capacity = 1 shl bits

    private val storage = Array<Any?>(capacity) { null }

    override var size: Int = 0

    /**
     * Индекс в таблице, начиная с которого следует искать данный элемент
     */
    private fun T.startingIndex(): Int {
        return hashCode() and (0x7FFFFFFF shr (31 - bits))
    }

    /**
     * Проверка, входит ли данный элемент в таблицу
     */
    override fun contains(element: T): Boolean {
        return find(element) != null
    }

    private fun find(element: T): Int? {
        var index = element.startingIndex()
        val startingIndex = index
        var current = storage[index]
        while (current != element) {
            index = (index + 1) % capacity
            if (index == startingIndex) return null
            current = storage[index]
        }
        return index
    }

    /**
     * Добавление элемента в таблицу.
     *
     * Не делает ничего и возвращает false, если такой же элемент уже есть в таблице.
     * В противном случае вставляет элемент в таблицу и возвращает true.
     *
     * Бросает исключение (IllegalStateException) в случае переполнения таблицы.
     * Обычно Set не предполагает ограничения на размер и подобных контрактов,
     * но в данном случае это было введено для упрощения кода.
     */
    override fun add(element: T): Boolean {
        val startingIndex = element.startingIndex()
        var index = startingIndex
        var current = storage[index]
        while (current != null) {
            if (current == element) {
                return false
            }
            index = (index + 1) % capacity
            check(index != startingIndex) { "Table is full" }
            current = storage[index]
        }
        storage[index] = element
        size++
        return true
    }

    /**
     * Удаление элемента из таблицы
     *
     * Если элемент есть в таблице, функция удаляет его из дерева и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     * Высота дерева не должна увеличиться в результате удаления.
     *
     * Спецификация: [java.util.Set.remove] (Ctrl+Click по remove)
     *
     * Средняя
     */
    override fun remove(element: T): Boolean {
        val elementIndex = find(element) ?: return false
        storage[elementIndex] = null
        size--
        return true
    }

    /**
     * Создание итератора для обхода таблицы
     *
     * Не забываем, что итератор должен поддерживать функции next(), hasNext(),
     * и опционально функцию remove()
     *
     * Спецификация: [java.util.Iterator] (Ctrl+Click по Iterator)
     *
     * Средняя (сложная, если поддержан и remove тоже)
     */
    override fun iterator(): MutableIterator<T> {
        return KtOpenAddressingSetIterator()
    }

    inner class KtOpenAddressingSetIterator<T> : MutableIterator<T> {
        private var index = 0
        private var nextIndex by Delegates.notNull<Int>()
        private var removable = false
        private var launched = false

        override fun hasNext(): Boolean {
            // трудоёмкость: O(capacity) is worst
            // ресурсоёмкость: O(1)
            if (launched && index == 0) return false
            while (storage[index] == null) {
                index = (index + 1) % capacity
                launched = true
                if (index == 0) return false
            }
            return true
        }

        override fun next(): T {
            // трудоёмкость: O(1) (if hasNext() was already called)
            // or similar to hasNext() (if hasNext() has not been called yet)
            // ресурсоёмкость: O(1)
            if (!hasNext()) throw NoSuchElementException()
            nextIndex = index
            index = (index + 1) % capacity
            removable = true
            return storage[nextIndex] as T
        }

        override fun remove() {
            // трудоёмкость: O(1)
            // ресурсоёмкость: O(1)
            check(removable)
            removable = false
            storage[nextIndex] = null
            size--
        }
    }
}