import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.graphx._
import org.apache.spark.graphx.lib.ShortestPaths
import org.apache.spark.graphx.lib.ShortestPaths.SPMap
import org.apache.spark.sql.SparkSession
import org.apache.spark.rdd.RDD

case class GraphUtils(graph: Graph[Int, Int]) {
  def getComponents(graph: Graph[Int, Int]) = {
    graph.connectedComponents().vertices
  }

  def getSubgraph(graph: Graph[Int, Int], scopeLeft: Int, scopeRight: Int) = {
    graph.subgraph(vpred = (vid, v) => vid <= scopeRight && vid >= scopeLeft)
  }
}

object PathRecommendation extends App {

  override def main(args: Array[String]): Unit = {

    def readGraph(path: String, sc: SparkContext) = {
      GraphLoader.edgeListFile(sc, path)
    }

    val datapath = "data\\roadNet-PA.txt"
    val testpath = "data\\test.txt"

    val sc = new SparkContext(new SparkConf().setAppName("pathfinder").setMaster("local[*]"))
    val ca: Graph[PartitionID, PartitionID] = readGraph(datapath, sc)
    val pointList = List(100, 200, 300, 1000)
    val gu = new GraphUtils(ca)

    val subgraph1 = gu.getSubgraph(ca, 0, 1000)
    val subgraph2 = gu.getSubgraph(ca, 0, 10000)

    val shortPath1 = ShortestPaths.run(subgraph1, Seq(1))
      .vertices      .filter({ case (vid, v) => pointList.contains(vid.toInt) })
      .collect
      .mkString

    val shortPath2 = ShortestPaths.run(subgraph2, Seq(1))
      .vertices
      .filter({ case (vid, v) => pointList.contains(vid.toInt) })
      .collect
      .mkString

    //    (200,Map())(100,Map())(1000,Map())(300,Map()) 1000
    //    (200,Map(1 -> 42))(100,Map(1 -> 48))(1000,Map(1 -> 32))(300,Map(1 -> 48)) 10000
  }
}
