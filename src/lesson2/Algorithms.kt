@file:Suppress("UNUSED_PARAMETER")

package lesson2

import kotlin.math.max
import kotlin.properties.Delegates

/**
 * Получение наибольшей прибыли (она же -- поиск максимального подмассива)
 * Простая
 *
 * Во входном файле с именем inputName перечислены цены на акции компании в различные (возрастающие) моменты времени
 * (каждая цена идёт с новой строки). Цена -- это целое положительное число. Пример:
 *
 * 201
 * 196
 * 190
 * 198
 * 187
 * 194
 * 193
 * 185
 *
 * Выбрать два момента времени, первый из них для покупки акций, а второй для продажи, с тем, чтобы разница
 * между ценой продажи и ценой покупки была максимально большой. Второй момент должен быть раньше первого.
 * Вернуть пару из двух моментов.
 * Каждый момент обозначается целым числом -- номер строки во входном файле, нумерация с единицы.
 * Например, для приведённого выше файла результат должен быть Pair(3, 4)
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */
fun optimizeBuyAndSell(inputName: String): Pair<Int, Int> {
    TODO()
}

/**
 * Задача Иосифа Флафия.
 * Простая
 *
 * Образовав круг, стоят menNumber человек, пронумерованных от 1 до menNumber.
 *
 * 1 2 3
 * 8   4
 * 7 6 5
 *
 * Мы считаем от 1 до choiceInterval (например, до 5), начиная с 1-го человека по кругу.
 * Человек, на котором остановился счёт, выбывает.
 *
 * 1 2 3
 * 8   4
 * 7 6 х
 *
 * Далее счёт продолжается со следующего человека, также от 1 до choiceInterval.
 * Выбывшие при счёте пропускаются, и человек, на котором остановился счёт, выбывает.
 *
 * 1 х 3
 * 8   4
 * 7 6 Х
 *
 * Процедура повторяется, пока не останется один человек. Требуется вернуть его номер (в данном случае 3).
 *
 * 1 Х 3
 * х   4
 * 7 6 Х
 *
 * 1 Х 3
 * Х   4
 * х 6 Х
 *
 * х Х 3
 * Х   4
 * Х 6 Х
 *
 * Х Х 3
 * Х   х
 * Х 6 Х
 *
 * Х Х 3
 * Х   Х
 * Х х Х
 *
 * Общий комментарий: решение из Википедии для этой задачи принимается,
 * но приветствуется попытка решить её самостоятельно.
 */
fun josephTask(menNumber: Int, choiceInterval: Int): Int {
    TODO()
}

/**
 * Наибольшая общая подстрока.
 * Средняя
 *
 * Дано две строки, например ОБСЕРВАТОРИЯ и КОНСЕРВАТОРЫ.
 * Найти их самую длинную общую подстроку -- в примере это СЕРВАТОР.
 * Если общих подстрок нет, вернуть пустую строку.
 * При сравнении подстрок, регистр символов *имеет* значение.
 * Если имеется несколько самых длинных общих подстрок одной длины,
 * вернуть ту из них, которая встречается раньше в строке first.
 */
fun longestCommonSubstring(first: String, second: String): String {
    /*class State {
        var length by Delegates.notNull<Int>()
        var link = -1
        var next: MutableMap<Char, Int> = mutableMapOf()
    }

    val states = Array(max(first.length, second.length) * 2) { State() }
    var sz = 0
    var last = 0
    states[0].length = 0
    states[0].link = -1
    sz++

    fun extendSuffixAutomation(symbol: Char) {
        val current = sz++
        states[current].length = states[last].length + 1
        var p = last
        while (p != -1 && !states[p].next.keys.contains(symbol)) {
            states[p].next[symbol] = current
            p = states[p].link
        }
        if (p == -1)
            states[current].link = 0
        else {
            val q = states[p].next[symbol]!!
            if (states[p].length + 1 == states[q].length)
                states[current].link = q
            else {
                val clone = sz++
                states[clone].length = states[p].length + 1
                states[clone].next = states[q].next
                states[clone].link = states[q].link
                while (p != -1 && !states[p].next.keys.contains(symbol)) {
                    states[p].next[symbol] = clone
                    p = states[p].link
                }
                states[q].link = clone
                states[current].link = clone

            }
        }
        last = current
    }
    for (element in first) extendSuffixAutomation(element)
    var v = 0
    var l = 0
    var best = 0
    var bestpos = 0
    for (i in second.indices) {
        while (v != 0 && !states[v].next.containsKey(second[i])) {
            v = states[v].link
            l = states[v].length
        }
        if (states[v].next.contains(second[i])) {
            v = states[v].next[second[i]]!!
            l++
        }
        if (l > best) {
            best = l
            bestpos = i
        }
    }
    return second.substring(bestpos - best + 1, best + 1)*/
    TODO()
}

/**
 * Число простых чисел в интервале
 * Простая
 *
 * Рассчитать количество простых чисел в интервале от 1 до limit (включительно).
 * Если limit <= 1, вернуть результат 0.
 *
 * Справка: простым считается число, которое делится нацело только на 1 и на себя.
 * Единица простым числом не считается.
 */
fun calcPrimesNumber(limit: Int): Int {
    fun isPrime(n: Int): Int {
        if (n < 2) return 0
        if (n == 2) return 1
        if (n % 2 == 0) return 0
        for (m in 3..kotlin.math.sqrt(n.toDouble()).toInt() step 2) {
            if (n % m == 0) return 0
        }
        return 1
    }

    var count = 0
    for (i in 1..limit) {
        count += isPrime(i)
    }
    return count
}
