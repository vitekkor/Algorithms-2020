@file:Suppress("UNUSED_PARAMETER")

package lesson2

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
    // трудоёмкость: O(first.length + second.length)
    // ресурсоёмкость: O(second.length)
    class State {
        var length by Delegates.notNull<Int>()
        var link = -1 //суффиксная ссылка; -1 - фиктивное состояние. На него ведёт только корень
        var next: MutableMap<Char, Int> = mutableMapOf() //таблица переходов
    }

    if (second.isEmpty()) return ""
    val states = Array(second.length * 2) { State() }
    var size = 0
    var last = 0
    states[0].length = 0
    states[0].link = -1
    size++

    fun extendSuffixAutomation(symbol: Char) { // функция расширяющая суффиксный автомат
        val current = size++ //создаём новое состояние
        states[current].length = states[last].length + 1
        var i = last
        while (i != -1 && states[i].next[symbol] == null) { // если из последнего состояния нет перехода
            states[i].next[symbol] = current // по symbol, то добавляем переход
            i = states[i].link // и переходим по суффиксной ссылке
        }
        if (i == -1) {
            // перехода ни разу не было обнаружено -> мы дошли до фиктивного состояния,
            // в которое мы попали по суффиксной ссылке из начального состояния (из 0).
            states[current].link = 0
        } else {
            // был обнаружен переход в состоянии номер i
            // т.е. мы пытаемся добавить в автомат строку x + symbol
            //которая уже была добавлена
            val target = states[i].next[symbol]!!
            if (states[i].length + 1 == states[target].length)
            //требуется провести суффиксную ссылку в такое состояние,
            //в котором длиннейшей строкой будет являться x + symbol
            //т.е. length для этого состояния должен быть равен length(i) + 1.
                states[current].link = target
            else {
                //переход по ветке else означает, что состоянию target соответствует не только
                //подстрока x+c длины len(i) + 1, но также и подстроки большей длины
                //клонируем состояние target с увеличением длины на 1
                val clone = size++
                states[clone].length = states[i].length + 1
                states[clone].next.putAll(states[target].next)
                states[clone].link = states[target].link
                states[target].link = clone //ссылку из target направляем в clone.
                states[current].link = clone //проводим суффиксную ссылку из current в clone
                //проходимся дальше по суффиксным ссылкам и если они вели в состояние target
                while (i != -1 && states[i].next[symbol] == target) {
                    states[i].next[symbol] = clone //то перенаправляем в состояние clone
                    i = states[i].link
                }
            }
        }
        last = current //обновляем последнее состояние
    }
    for (element in second.withIndex()) extendSuffixAutomation(element.value) // создаём автомат по второй строке
    var currentState = 0
    var currentLength = 0
    var bestLength = 0
    var bestPosition = 0
    for (i in first.indices) {
        //идём по символам первой строки
        while (currentState != 0 && states[currentState].next[first[i]] == null) {
            //если из состояния currentState нет требуемого перехода, то переходим по суффиксной ссылке
            //пока не дойдём до фиктивного состояния или не найдём переход
            currentState = states[currentState].link
            currentLength = states[currentState].length
        }
        if (states[currentState].next[first[i]] != null) {
            //если был найден переход, то совершаем его и увеличиваем currentLength на 1
            currentState = states[currentState].next[first[i]]!!
            currentLength++
        }
        //проверяем, нашли ли мы самую длинную подстроку
        if (currentLength > bestLength) {
            bestLength = currentLength
            bestPosition = i
        }
    }
    return first.substring(bestPosition - bestLength + 1, bestPosition + 1)
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
    // трудоёмкость: O(sqrt(limit)*limit)
    // ресурсоёмкость: O(1)
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
