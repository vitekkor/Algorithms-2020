@file:Suppress("UNUSED_PARAMETER", "unused")

package lesson8

import lesson6.Graph
import lesson6.Path
import lesson7.knapsack.Fill
import lesson7.knapsack.Item
import kotlin.math.pow

// Примечание: в этом уроке достаточно решить одну задачу

/**
 * Решить задачу о ранце (см. урок 6) любым эвристическим методом
 *
 * Очень сложная
 *
 * load - общая вместимость ранца, items - список предметов
 *
 * Используйте parameters для передачи дополнительных параметров алгоритма
 * (не забудьте изменить тесты так, чтобы они передавали эти параметры)
 */
fun fillKnapsackHeuristics(load: Int, items: List<Item>, vararg parameters: Any): Fill {
    TODO()
}

/**
 * Решить задачу коммивояжёра (см. урок 6) методом колонии муравьёв
 * или любым другим эвристическим методом, кроме генетического и имитации отжига
 * (этими двумя методами задача уже решена в под-пакетах annealing & genetic).
 *
 * Очень сложная
 *
 * Граф передаётся через получатель метода
 *
 * Используйте parameters для передачи дополнительных параметров алгоритма
 * (не забудьте изменить тесты так, чтобы они передавали эти параметры)
 */
fun Graph.findVoyagingPathHeuristics(
    countOfAnts: Int,
    iterationsNumber: Int,
    startingPheromone: Double = 1.0,
    alpha: Double = -2.0,
    beta: Double = 1.0,
    ro: Double = 0.3
): Path {
    // трудоёмкость: O(iterationsNumber * countOfAnts * Graph.vertices.size)
    // ресурсоёмкость: O(countOfAnts * (Graph.edges.size + Graph.vertices.size))
    val graph = this

    data class Ant(
        val edgePheromone: MutableMap<Graph.Edge, Double>,
        val visitedVertices: MutableList<Graph.Vertex> = mutableListOf(),
        val visitedEdges: MutableSet<Graph.Edge> = mutableSetOf(),
        var alive: Boolean = true
    ) {
        fun getNextVertex(current: Graph.Vertex): Graph.Vertex? {
            visitedVertices.add(current)
            return graph.getNeighbors(current).filter { it !in visitedVertices }
                .maxByOrNull { getProbability(current, it) }
        }

        private fun getProbability(i: Graph.Vertex, j: Graph.Vertex): Double {
            var sum = 0.0
            val currentEdge = graph.getConnection(i, j) ?: return 0.0
            // вычисляем уровень феромона для данного ребра
            var currentT = edgePheromone.getValue(currentEdge).pow(alpha)
            // вычисляем эвристическое расстояние для данного ребра
            var currentN = (1.0 / currentEdge.weight).pow(beta)
            // коэффициент для текущего ребра
            val currentEdgeCoefficient = currentT * currentN
            sum += currentEdgeCoefficient
            // суммируем коэффициенты для ребер, соединяющих соседние вершины с ребром i
            for (neighbourVertex in graph.getNeighbors(i)) {
                if (neighbourVertex !in visitedVertices) { // те, которые уже посетили не учитываем
                    val edge = graph.getConnection(i, neighbourVertex)!!
                    currentT = edgePheromone.getValue(edge).pow(alpha)
                    currentN = (1.0 / edge.weight).pow(beta)
                    sum += currentN * currentT
                }
            }
            return currentEdgeCoefficient / sum
        }

        fun getPath(): Path? {
            // воссоздаем путь муравья
            var path = Path(visitedVertices.first())
            for (vertex in visitedVertices.drop(1)) {
                if (graph.getConnection(path.vertices.last(), vertex) == null) return null
                path = Path(path, graph, vertex)
            }
            if (graph.getConnection(path.vertices.last(), path.vertices.first()) == null) return null
            path = Path(path, graph, path.vertices.first())
            return path
        }

        fun updatePheromone() {
            // добавляем феромон
            // чем длиннее был путь - тем меньше феромона добавляем
            val deltaT = 1.0 / visitedEdges.sumBy { it.weight }
            for (edge in visitedEdges) {
                edgePheromone[edge] = edgePheromone.getValue(edge) + deltaT
            }
        }

        fun giveNewLife() {
            // возрождаем муравья, стирая ему память о посещенных вершинах и ребрах
            alive = true
            visitedEdges.clear()
            visitedVertices.clear()
        }

    }

    var bestPath: Path? = null
    val startingVertex = vertices.first()
    val pheromone = mutableMapOf<Graph.Edge, Double>()
    edges.forEach { pheromone[it] = startingPheromone } // задаём начальный уровень феромона
    val ants = List(countOfAnts) { Ant(pheromone) } // создаем колонию муравьёв
    for (i in 1..iterationsNumber) {
        for (ant in ants) {
            var currentVertex = startingVertex
            while (ant.visitedVertices.size != vertices.size - 1) {
                val next = ant.getNextVertex(currentVertex)
                if (next == null) { // если муравью некуда идти, то убиваем его
                    ant.alive = false
                    break
                }
                graph.getConnection(currentVertex, next)?.let { ant.visitedEdges.add(it) }
                currentVertex = next
            }
            ant.visitedVertices.add(currentVertex)
            val finalEdge = this.getConnection(currentVertex, startingVertex)
            if (finalEdge == null) ant.alive = false // если нет ребра из последней посещённой вершины в начальную,
            // то убиваем муравья
            else ant.visitedEdges.add(finalEdge)
        }
        // выбираем наилучший путь
        val minPath = ants.minByOrNull { it.getPath()?.length ?: Int.MAX_VALUE }?.getPath()
        if (bestPath == null || minPath?.length ?: Int.MAX_VALUE < bestPath.length) {
            bestPath = minPath
        }
        // испарение феромона
        pheromone.mapValues { it.value * (1.0 - ro) }
        // добавляем феромоны только выживших муравьев. Оживляем мертвых и стираем память всем
        ants.forEach { if (it.alive) it.updatePheromone(); it.giveNewLife() }
    }
    println(bestPath)
    return bestPath!!
}

