@file:Suppress("UNUSED_PARAMETER")

package lesson1

import java.io.File
import java.lang.Integer.max
import java.lang.Integer.min

/**
 * Сортировка времён
 *
 * Простая
 * (Модифицированная задача с сайта acmp.ru)
 *
 * Во входном файле с именем inputName содержатся моменты времени в формате ЧЧ:ММ:СС AM/PM,
 * каждый на отдельной строке. См. статью википедии "12-часовой формат времени".
 *
 * Пример:
 *
 * 01:15:19 PM
 * 07:26:57 AM
 * 10:00:03 AM
 * 07:56:14 PM
 * 01:15:19 PM
 * 12:40:31 AM
 *
 * Отсортировать моменты времени по возрастанию и вывести их в выходной файл с именем outputName,
 * сохраняя формат ЧЧ:ММ:СС AM/PM. Одинаковые моменты времени выводить друг за другом. Пример:
 *
 * 12:40:31 AM
 * 07:26:57 AM
 * 10:00:03 AM
 * 01:15:19 PM
 * 01:15:19 PM
 * 07:56:14 PM
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */
private fun <E> writeToFile(fileName: String, data: List<E>) {
    File(fileName).bufferedWriter().use {
        for (i in data.indices) {
            it.write(data[i].toString())
            if (i != data.lastIndex) it.newLine()
        }
    }
}

fun sortTimes(inputName: String, outputName: String) {
    // трудоёмкость: O(nlog(n))
    // ресурсоёмкость: O(n)
    class Time(momentOfTime: String) : Comparable<Time> {
        private val hours: Int
        private val minutes: Int
        private val seconds: Int
        private val totalTime: Int
        private val stringRepresentation: String

        init {
            check(momentOfTime.matches(Regex("""[01]\d:[0-5]\d:[0-5]\d [AP]M""")))
            val times = momentOfTime.split(" ")
            val partsOfTimes = times[0].split(":")
            check(partsOfTimes[0].toInt() in 0..12)
            hours =
                partsOfTimes[0].toInt() +
                        if (times[1] == "AM" && partsOfTimes[0] == "12") -12 else
                            if (times[1] == "PM" && partsOfTimes[0] != "12") 12 else 0
            minutes = partsOfTimes[1].toInt()
            seconds = partsOfTimes[2].toInt()
            totalTime = hours * 3600 + minutes * 60 + seconds
            stringRepresentation = momentOfTime
        }

        override fun compareTo(other: Time): Int {
            return this.totalTime.compareTo(other.totalTime)
        }

        override fun toString(): String {
            return stringRepresentation
        }

    }

    val inputTimes = File(inputName).readLines().map { Time(it) }.sorted()
    writeToFile(outputName, inputTimes)
}

/**
 * Сортировка адресов
 *
 * Средняя
 *
 * Во входном файле с именем inputName содержатся фамилии и имена жителей города с указанием улицы и номера дома,
 * где они прописаны. Пример:
 *
 * Петров Иван - Железнодорожная 3
 * Сидоров Петр - Садовая 5
 * Иванов Алексей - Железнодорожная 7
 * Сидорова Мария - Садовая 5
 * Иванов Михаил - Железнодорожная 7
 *
 * Людей в городе может быть до миллиона.
 *
 * Вывести записи в выходной файл outputName,
 * упорядоченными по названию улицы (по алфавиту) и номеру дома (по возрастанию).
 * Людей, живущих в одном доме, выводить через запятую по алфавиту (вначале по фамилии, потом по имени). Пример:
 *
 * Железнодорожная 3 - Петров Иван
 * Железнодорожная 7 - Иванов Алексей, Иванов Михаил
 * Садовая 5 - Сидоров Петр, Сидорова Мария
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */
fun sortAddresses(inputName: String, outputName: String) {
    TODO()
}

/**
 * Сортировка температур
 *
 * Средняя
 * (Модифицированная задача с сайта acmp.ru)
 *
 * Во входном файле заданы температуры различных участков абстрактной планеты с точностью до десятых градуса.
 * Температуры могут изменяться в диапазоне от -273.0 до +500.0.
 * Например:
 *
 * 24.7
 * -12.6
 * 121.3
 * -98.4
 * 99.5
 * -12.6
 * 11.0
 *
 * Количество строк в файле может достигать ста миллионов.
 * Вывести строки в выходной файл, отсортировав их по возрастанию температуры.
 * Повторяющиеся строки сохранить. Например:
 *
 * -98.4
 * -12.6
 * -12.6
 * 11.0
 * 24.7
 * 99.5
 * 121.3
 */
fun sortTemperatures(inputName: String, outputName: String) {
    //counting sort
    // трудоёмкость: O(n+7730)
    // ресурсоёмкость: O(7730)
    val limit = 2730 + 5000
    val inputTemperatures = File(inputName).readLines()
    val count = IntArray(limit + 1)
    fun normalize(temp: String): Int = (temp.replace(".", "").toInt() + 2730)
    for (temperature in inputTemperatures) {
        count[normalize(temperature)]++
    }
    for (j in 1..limit) {
        count[j] += count[j - 1]
    }
    val out = DoubleArray(inputTemperatures.size)
    for (j in inputTemperatures.indices.reversed()) {
        out[count[normalize(inputTemperatures[j])] - 1] = inputTemperatures[j].toDouble()
        count[normalize(inputTemperatures[j])]--
    }
    writeToFile(outputName, out.toList())
}

/**
 * Сортировка последовательности
 *
 * Средняя
 * (Задача взята с сайта acmp.ru)
 *
 * В файле задана последовательность из n целых положительных чисел, каждое в своей строке, например:
 *
 * 1
 * 2
 * 3
 * 2
 * 3
 * 1
 * 2
 *
 * Необходимо найти число, которое встречается в этой последовательности наибольшее количество раз,
 * а если таких чисел несколько, то найти минимальное из них,
 * и после этого переместить все такие числа в конец заданной последовательности.
 * Порядок расположения остальных чисел должен остаться без изменения.
 *
 * 1
 * 3
 * 3
 * 1
 * 2
 * 2
 * 2
 */
fun sortSequence(inputName: String, outputName: String) {
    // трудоёмкость: O(nlog(n))
    // ресурсоёмкость: O(n)
    val input = File(inputName).readLines()
    val sortedInput = input.sorted()
    var previousElement = -1
    var count = 1
    var maxCount = 1
    var minElement = Int.MAX_VALUE

    for (elem in sortedInput) {
        val element = elem.toInt()
        if (element == previousElement) count++ else {
            minElement = when (maxCount.compareTo(count)) {
                0 -> min(minElement, previousElement)
                -1 -> previousElement
                else -> minElement
            }
            maxCount = max(maxCount, count)
            count = 1
            previousElement = element
        }
    }
    minElement = when (maxCount.compareTo(count)) {
        0 -> min(minElement, previousElement)
        -1 -> previousElement
        else -> minElement
    }
    maxCount = max(maxCount, count)

    val output = mutableListOf<String>()
    for (number in input) {
        if (number.toInt() != minElement) output.add(number)
    }
    repeat(maxCount) { output.add("$minElement") }

    writeToFile(outputName, output)
}

/**
 * Соединить два отсортированных массива в один
 *
 * Простая
 *
 * Задан отсортированный массив first и второй массив second,
 * первые first.size ячеек которого содержат null, а остальные ячейки также отсортированы.
 * Соединить оба массива в массиве second так, чтобы он оказался отсортирован. Пример:
 *
 * first = [4 9 15 20 28]
 * second = [null null null null null 1 3 9 13 18 23]
 *
 * Результат: second = [1 3 4 9 9 13 15 20 23 28]
 */
fun <T : Comparable<T>> mergeArrays(first: Array<T>, second: Array<T?>) {
    var firstIndex = 0
    var secondIndex = first.size
    for (i in second.indices) {
        if (secondIndex >= second.size || (firstIndex < first.size && first[firstIndex] <= second[secondIndex]!!)) {
            second[i] = first[firstIndex]
            firstIndex++
        } else {
            second[i] = second[secondIndex]
            secondIndex++
        }
    }
}

