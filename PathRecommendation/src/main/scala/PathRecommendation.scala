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

    var flag = true
    while(flag){
      println("Please input your visiting area(0 ~ 1088092):")
      print("from: ")
      val from: Int = Source.stdin.bufferedReader().readLine().toInt
      print("to: ")
      val to: Int = Source.stdin.bufferedReader().readLine().toInt

      (to - from) match{
        case _ < 0 => println("from must be less than to, please re-enter: ")
        case _ > 100000 => println("Too large area, please re-enter the area: ")
        case _ =>  flag = false
      }

    }

    println("Please input your location nodeId:")
    val location = Source.stdin.bufferedReader().readLine().toInt
    println("Your location is at Node: No." + location)

    println("Please enter the number of destinations today:")
    val len = Source.stdin.bufferedReader().readLine().toInt
    val pointList = new Array[Int](len)
    for (i <- Range(0, len)) {
      println("Please enter the No." + (i+1) + " destinations:")
      pointList(i) = Source.stdin.bufferedReader().readLine().toInt
    }
    (location, pointList, from, to)
  }

  def readGraph(path: String, sc: SparkContext) = {
    GraphLoader.edgeListFile(sc, path)
  }




  override def main(args: Array[String]): Unit = {
    val data: (PartitionID, Array[PartitionID], Any, Any) = userData
    val startpoint = data._1
    val destination = data._2
    val from = data._3
    val to = data._4

    // path for Windows
    val datapath = "data\\roadNet-PA.txt"
    val testpath = "data\\test.txt"

    // path for Mac
    val datapath = "data/roadNet-PA.txt"
    val testpath = "data/test.txt"

    val sc = new SparkContext(new SparkConf().setAppName("pathfinder").setMaster("local[*]"))
    val ca: Graph[PartitionID, PartitionID] = readGraph(datapath, sc)

    val gu = new GraphUtils(ca)



    val subgraph = gu.getSubgraph(ca,

    val shortPath1 = ShortestPaths.run(subgraph1, Seq(1))
      .vertices.filter({ case (vid, v) => destination.contains(vid.toInt) })
      .collect
      .mkString

    val shortPath2 = ShortestPaths.run(subgraph2, Seq(1))
      .vertices
      .filter({ case (vid, v) => destination.contains(vid.toInt) })
      .collect
      .mkString
  }
}
