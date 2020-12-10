package lesson8

import lesson6.Graph
import lesson6.Path
import lesson6.impl.GraphBuilder
import lesson7.knapsack.Fill
import lesson7.knapsack.Item
import lesson7.knapsack.fillKnapsackGreedy
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

abstract class AbstractHeuristicsTests {

    fun fillKnapsackCompareWithGreedyTest(fillKnapsackHeuristics: (Int, List<Item>) -> Fill) {
        for (i in 0..9) {
            val items = mutableListOf<Item>()
            val random = Random()
            for (j in 0 until 10000) {
                items += Item(1 + random.nextInt(10000), 300 + random.nextInt(600))
            }
            try {
                val fillHeuristics = fillKnapsackHeuristics(1000, items)
                println("Heuristics score = " + fillHeuristics.cost)
                val fillGreedy = fillKnapsackGreedy(1000, items)
                println("Greedy score = " + fillGreedy.cost)
                assertTrue(fillHeuristics.cost >= fillGreedy.cost)
            } catch (e: StackOverflowError) {
                println("Greedy failed with Stack Overflow")
            }
        }
    }

    fun findVoyagingPathHeuristics(findVoyagingPathHeuristics: Graph.() -> Path) {
        var graph = GraphBuilder().apply {
            val a = addVertex("A")
            val b = addVertex("B")
            val c = addVertex("C")
            val d = addVertex("D")
            val e = addVertex("E")
            val f = addVertex("F")
            addConnection(a, b, 10)
            addConnection(b, c, 15)
            addConnection(c, f, 30)
            addConnection(a, d, 20)
            addConnection(d, e, 25)
            addConnection(e, f, 15)
            addConnection(a, f, 40)
            addConnection(b, d, 10)
            addConnection(c, e, 5)
        }.build()
        var path = graph.findVoyagingPathHeuristics()
        assertEquals(105, path.length)
        var vertices = path.vertices
        assertEquals(vertices.first(), vertices.last(), "Voyaging path $vertices must be loop!")
        var withoutLast = vertices.dropLast(1)
        var expected = listOf(graph["A"], graph["D"], graph["B"], graph["C"], graph["E"], graph["F"])
        assertEquals(expected.size, withoutLast.size, "Voyaging path $vertices must travel through all vertices!")
        expected.forEach {
            assertTrue(it in vertices, "Voyaging path $vertices must travel through all vertices!")
        }

        graph = GraphBuilder().apply {
            val a = addVertex("A")
            val b = addVertex("B")
            val c = addVertex("C")
            val d = addVertex("D")
            val e = addVertex("E")
            val f = addVertex("F")
            addConnection(a, b, 27)
            addConnection(b, a, 21)
            addConnection(b, c, 15)
            addConnection(c, b, 15)
            addConnection(c, d, 12)
            addConnection(d, c, 9)
            addConnection(d, f, 18)
            addConnection(f, d, 27)
            addConnection(f, a, 21)
            addConnection(a, f, 15)
            addConnection(f, e, 18)
            addConnection(a, e, 18)
            addConnection(e, a, 27)
            addConnection(b, e, 21)
            addConnection(e, b, 15)
            addConnection(e, c, 15)
            addConnection(d, e, 12)
            addConnection(e, d, 9)
        }.build()
        path = graph.findVoyagingPathHeuristics()
        assertEquals(87, path.length)
        vertices = path.vertices
        assertEquals(vertices.first(), vertices.last(), "Voyaging path $vertices must be loop!")
        withoutLast = vertices.dropLast(1)
        expected = listOf(graph["A"], graph["F"], graph["E"], graph["D"], graph["C"], graph["B"])
        assertEquals(expected.size, withoutLast.size, "Voyaging path $vertices must travel through all vertices!")
        expected.forEach {
            assertTrue(it in vertices, "Voyaging path $vertices must travel through all vertices!")
        }
    }

}