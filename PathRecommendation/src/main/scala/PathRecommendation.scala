import org.apache.avro.SchemaBuilder.ArrayBuilder
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.graphx._
import org.apache.spark.graphx.lib.ShortestPaths
import org.apache.spark.graphx.lib.ShortestPaths.SPMap
import org.apache.spark.sql.SparkSession
import org.apache.spark.rdd.RDD

import scala.io.Source

case class GraphUtils(graph: Graph[Int, Int]) {
  def getComponents(graph: Graph[Int, Int]) = {
    graph.connectedComponents().vertices
  }

  def getSubgraph(graph: Graph[Int, Int], scopeLeft: Int, scopeRight: Int) = {
    graph.subgraph(vpred = (vid, v) => vid <= scopeRight && vid >= scopeLeft)
  }

  def getShortestPath(src: Int, target: Array[Int], g: Graph[Int, Int]) = {
    ShortestPaths.run(g, Seq(src))
      .vertices
      .filter({ case (vid, v) => target.contains(vid) })
  }

  def shortestPathBetween(src: Int, target: Int, g: Graph[Int, Int]) = {
    ShortestPaths.run(g, Seq(src))
      .vertices
      .filter({ case (vid, v) => vid == target })
  }
}

object PathRecommendation extends App {
  def userData = {
    println("Please input your location nodeId:")
    val location = Source.stdin.bufferedReader().readLine().toInt
    println("Your location is at Node: No." + location)
    println("Please enter the number of destinations today:")
    val len = Source.stdin.bufferedReader().readLine().toInt
    val pointList = new Array[Int](len)
    for (i <- Range(1, len + 1)) {
      println("Please enter the No." + i + " destinations:")
      pointList(i) = Source.stdin.bufferedReader().readLine().toInt
    }
    (location, pointList)
  }

  def readGraph(path: String, sc: SparkContext) = {
    GraphLoader.edgeListFile(sc, path)
  }

  override def main(args: Array[String]): Unit = {
    val startpoint = userData._1
    val destination = userData._2

    val datapath = "data\\roadNet-PA.txt"
    val testpath = "data\\test.txt"

    val sc = new SparkContext(new SparkConf().setAppName("pathfinder").setMaster("local[*]"))
    val ca: Graph[PartitionID, PartitionID] = readGraph(datapath, sc)
    val gu = new GraphUtils(ca)

    val subgraph1 = gu.getSubgraph(ca, 0, 1000)
    val subgraph2 = gu.getSubgraph(ca, 0, 10000)

    val shortPath1 = ShortestPaths.run(subgraph1, Seq(1))
      .vertices.filter({ case (vid, v) => destination.contains(vid.toInt) })
      .collect
      .mkString

    val shortPath2 = ShortestPaths.run(subgraph2, Seq(1))
      .vertices
      .filter({ case (vid, v) => destination.contains(vid.toInt) })
      .collect
      .mkString

    //    (200,Map())(100,Map())(1000,Map())(300,Map()) 1000
    //    (200,Map(1 -> 42))(100,Map(1 -> 48))(1000,Map(1 -> 32))(300,Map(1 -> 48)) 10000
  }
}
