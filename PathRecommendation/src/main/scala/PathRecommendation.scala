import java.awt.JobAttributes.DestinationType
import java.util
import java.util.ArrayList

import org.apache.spark.graphx._
import org.apache.spark.graphx.lib.ShortestPaths
import org.apache.spark.graphx.lib.ShortestPaths.SPMap
import org.apache.spark.sql.catalyst.expressions.ArrayBinaryLike
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

/** *
  * GraphUtils case class and it's related methods
  *
  * @author Zechuan Miao & Lingyu Chen
  * @param graph
  */
case class GraphUtils(graph: Graph[PartitionID, PartitionID]) {
  /**
    * Get the components of given Graph
    *
    * @param graph
    * @return VertexRDD of the components
    */
  def getStrongComponents(graph: Graph[PartitionID, PartitionID], numIter: Int) = {
    graph.stronglyConnectedComponents(numIter)
  }

  /**
    * Given the graph and user provided nodes range, return the sub graph for operating
    *
    * @param graph
    * @param scopeLeft
    * @param scopeRight
    * @return sub graph
    */
  def getSubgraph(graph: Graph[PartitionID, PartitionID], scopeLeft: VertexId, scopeRight: VertexId) = {
    graph.subgraph(vpred = (vid, v) => vid <= scopeRight && vid >= scopeLeft)
  }

  /**
    * Filter out the desired shortest paths based on given nodes
    *
    * @param res   result to store the
    * @param destination
    * @param paths the shortest paths from sub graph
    * @return Array buffer of (VertixId, SPMap)
    */
  def filter(res: ArrayBuffer[(VertexId, SPMap)], destination: Array[VertexId], paths: Array[(VertexId, SPMap)]) = {
    for (i <- Range(0, destination.length - 1)) {
      val src: Array[(VertexId, SPMap)] = paths.filter({ case (vid, v) => vid == i })
      for (j <- Range(i + 1, destination.length - 1)) {
        val path: Map[VertexId, PartitionID] = src(0)._2.filterKeys({ case x => x == destination(j) })
        res += destination(i) -> path
      }
    }
    res
  }

  /**
    * Get the shortest paths, the format of the sortest path is (VertexId, SPMap)
    *
    * @param graph
    * @param src    the start point
    * @param target seq of destination points
    * @return Array of shortest paths
    */
  def getShortestPath(graph: Graph[PartitionID, PartitionID], src: VertexId, target: Array[VertexId]) = {
    val shortGraph: Graph[SPMap, PartitionID] = ShortestPaths.run(graph, target.toSeq)
    val shortVertex: VertexRDD[SPMap] = shortGraph.vertices
    val all = (target.+:(src))
    val maps: Array[(VertexId, SPMap)] = shortVertex.filter({ case (vid, v) => all.contains(vid) }).collect
    maps
  }

  /**
    * Find the shortest path between two specific nodes
    *
    * @param src    source node
    * @param target target node
    * @param graph
    * @return
    */
  def shortestPathBetween(src: VertexId, target: VertexId, graph: Graph[PartitionID, PartitionID]) = {
    ShortestPaths.run(graph, Seq(src))
      .vertices
      .filter({ case (vid, v) => vid == target })
  }

  def DFS(destination: Array[VertexId]) = {
    def recurse(destination: Array[VertexId], start: Int, count: Int, temp: ArrayList[VertexId], res: ArrayList[ArrayList[VertexId]]): Unit = {
      if (count == 0) {
        print(temp)
        res.add(new util.ArrayList[VertexId](temp))
      } else {
        for (i <- Range(0, destination.length)) {
          temp.add(destination(i))
          val copy = destination.toBuffer
          copy.remove(i)
          recurse(copy.toArray, start + 1, count - 1, temp, res)
          temp.remove(temp.size() - 1)
        }
      }
    }

    val temp = new util.ArrayList[VertexId]()
    val res = new util.ArrayList[ArrayList[VertexId]]()
    recurse(destination, 0, destination.length, temp, res)
    res
  }

  def findOnePath(src: VertexId, maps: Array[(VertexId, SPMap)], destination: Array[VertexId]) = {
    var minPath: ArrayBuffer[(VertexId, SPMap)] = new ArrayBuffer[(VertexId, SPMap)]()
    val max = 0
    //the path from the startpoint
    val fromSrc = maps.filter({ case (vid, v) => vid == src })(0)
    //the path between all destinations
    val dess: util.ArrayList[util.ArrayList[VertexId]] = DFS(destination)
    for (p <- Range(0, dess.size())) {
      val path: util.ArrayList[VertexId] = dess(p)
      var sumlen = fromSrc._2.get(path(0)).get
      val onepath =  new ArrayBuffer[(VertexId, SPMap)]()
      for (i <- Range(0, destination.length - 1)) {
        val pi: VertexId = path(i)
        val pii: VertexId = path(i + 1)
        val m: (VertexId, SPMap) = maps.filter({ case (vid, map) => vid == pi && map.get(pii) != 0})(0)
        val len = m._2.filterKeys(_ == pii).get(pii)
        sumlen += len.get
        onepath += m
      }
      if(sumlen >= max){
        minPath = onepath
      }
    }
  }
}

object PathRecommendation extends App {
  /**
    * Read in data of users
    *
    * @return Tuple4 of required data: visiting area, location, destination list
    */
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
    val location: VertexId = Source.stdin.bufferedReader().readLine().toInt
    println("Your location is at Node: No." + location)

    println("Please enter the number of destinations today:")
    val len = Source.stdin.bufferedReader().readLine().toInt
    val pointList = new Array[VertexId](len)
    for (i <- Range(0, len)) {
      println("Please enter the No." + (i + 1) + " destinations:")
      pointList(i) = Source.stdin.bufferedReader().readLine().toInt
    }
    (location, pointList, from, to)
  }

  /**
    * Read in the graph data, be careful that the reading process is directly, which means it follow the rule (source, target)
    *
    * @param path the relative path of graph file
    * @param sc   target SparkContext to store the graph data
    * @return
    */
  def readGraph(path: String, sc: SparkContext) = {
    GraphLoader.edgeListFile(sc, path)
  }

  override def main(args: Array[String]): Unit = {
    val data = userData
    val startpoint: VertexId = data._1
    val destination: Array[VertexId] = data._2
    val from: PartitionID = data._3
    val to: PartitionID = data._4

    // path for Windows
    val datapath = "data\\roadNet-PA.txt"
    val testpath = "data\\test.txt"

    // path for Mac
    val datapathmac = "data/roadNet-PA.txt"
    val testpathmac = "data/test.txt"

    val sc = new SparkContext(new SparkConf().setAppName("pathfinder").setMaster("local[*]"))
    val pa: Graph[PartitionID, PartitionID] = readGraph(datapath, sc)


    val gu = new GraphUtils(pa)

    val subgraph = gu.getSubgraph(pa, from, to)
    //    val fromStart: Array[(VertexId, SPMap)] = gu.getShortestPath(src, destination, subgraph).collect

    //    val res: Array[(VertexId, SPMap)] = gu.getShortestPath(subgraph,startpoint,destination)
  }
}