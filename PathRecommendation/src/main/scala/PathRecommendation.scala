import org.apache.spark.graphx._
import org.apache.spark.graphx.lib.ShortestPaths
import org.apache.spark.graphx.lib.ShortestPaths.SPMap
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer
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

    var flag = true
    var from: Int = 0
    var to: Int = 0
    while (flag) {
      println("Please input your visiting area(0 ~ 1088092):")
      print("from: ")
      from = Source.stdin.bufferedReader().readLine().toInt
      print("to: ")
      to = Source.stdin.bufferedReader().readLine().toInt

      val r = to - from match {
        case r if r < 0 => println("from must be less than to, please re-enter: ")
        case r if r > 100000 => println("Too large area, please re-enter the area: ")
        case _ => flag = false
      }

    }

    println("Please input your location nodeId:")
    val location = Source.stdin.bufferedReader().readLine().toInt
    println("Your location is at Node: No." + location)

    println("Please enter the number of destinations today:")
    val len = Source.stdin.bufferedReader().readLine().toInt
    val pointList = new Array[Int](len)
    for (i <- Range(0, len)) {
      println("Please enter the No." + (i + 1) + " destinations:")
      pointList(i) = Source.stdin.bufferedReader().readLine().toInt
    }
    (location, pointList, from, to)
  }

  def readGraph(path: String, sc: SparkContext) = {
    GraphLoader.edgeListFile(sc, path)
  }

  override def main(args: Array[String]): Unit = {
    val data = userData
    val startpoint = data._1
    val destination = data._2
    val from = data._3
    val to = data._4

    // path for Windows
    val datapath = "data\\roadNet-PA.txt"
    val testpath = "data\\test.txt"

    // path for Mac
    val datapathmac = "data/roadNet-PA.txt"
    val testpathmac = "data/test.txt"

    val sc = new SparkContext(new SparkConf().setAppName("pathfinder").setMaster("local[*]"))
    val pa = readGraph(datapath, sc)

    val gu = new GraphUtils(pa)

    val subgraph = gu.getSubgraph(pa, from, to)

    //    val fromStart: Array[(VertexId, SPMap)] = gu.getShortestPath(startpoint,destination,subgraph).collect
    val allPath: ArrayBuffer[(VertexId, SPMap)] = new ArrayBuffer[(VertexId, SPMap)]()

    val buff = destination.toBuffer

    for (i <- Range(0, destination.length - 1)) {
      buff.remove(0)
      val leftDestination = buff.toArray
      allPath ++= gu.getShortestPath(destination(i), leftDestination, subgraph).collect
    }

    print(allPath.mkString)
  }
}