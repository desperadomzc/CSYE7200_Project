import java.util

import org.apache.spark.graphx.{Edge, Graph, PartitionID, VertexId}

import scala.collection.mutable.ArrayBuffer
import java.util._

import PathRecommendation.readGraph
import org.apache.spark.{SparkConf, SparkContext}

import scala.io.Source

object DFS {
  def gameSolver(g: Graph[PartitionID, PartitionID], start: Int) = {
    val count = (g.numVertices - 1).toInt;
    val visited = new util.ArrayList[VertexId]()
    visited.add(start)
    val temp = new util.ArrayList[VertexId]()
    temp.add(start)
    val res = new ArrayList[ArrayList[VertexId]]()

    def dfs(g: Graph[PartitionID, PartitionID], visited: ArrayList[VertexId], temp: ArrayList[VertexId], start: VertexId, count: Int, res: ArrayList[ArrayList[VertexId]]): Boolean = {
      if (count == 0) {
        res.add(new ArrayList(temp))
        true
      } else {
        val directions: Array[VertexId] = g.edges.filter({ case Edge(a, b, c) => a == start }).collect.map({ case Edge(a, b, c) => b })
        for (i <- Range(0, directions.length) ) {
          val d: VertexId = directions(i)
          if(!visited.contains(d)){
            temp.add(d)
            visited.add(directions(i))
            if (dfs(g, visited, temp, directions(i), count - 1, res)) true
            temp.remove(temp.size() - 1)
            visited.remove(visited.size() - 1)
          }
        }
        return false
      }
    }

    dfs(g, visited, temp, start, count, res)
    res
  }

  def main(args: Array[String]): Unit = {
    val edgePath = "data\\edgeList.txt"
    val startPath = "data\\startPoint.txt"
    val sc = new SparkContext(new SparkConf().setAppName("pathfinder").setMaster("local[*]"))

    val gameGraph = readGraph(edgePath, sc)
    val start = Source.fromFile(startPath, "UTF-8").getLines().next().toInt
    print(gameSolver(gameGraph,start))
  }
}
