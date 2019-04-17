import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.graphx._
import org.apache.spark.graphx.lib.ShortestPaths
import org.apache.spark.graphx.lib.ShortestPaths.SPMap
import org.apache.spark.sql.SparkSession
import org.apache.spark.rdd.RDD
object PathRecommendation extends App{


  def readGraph(path:String,sc:SparkContext)= {
    GraphLoader.edgeListFile(sc,path)
  }

  override def main(args: Array[String]): Unit = {
    val datapath = "data\\roadNet-CA.txt"
    val testpath = "data\\test.txt"
    val paPath = "data/roadNet-PA.txt"
    val conf = new SparkConf().setAppName("pathfinder").setMaster("local[*]")
    val sc = new SparkContext(conf)
    val ca: Graph[PartitionID, PartitionID] = readGraph(paPath,sc)

    val result = ShortestPaths.run(ca,Seq(0))
      .vertices
      .filter({case(vId,_) => vId == 1})
      .first()._2.get(0).mkString

//    val component = ca.connectedComponents().vertices

    val sub = ca.subgraph(vpred = (vid, v) => v <= 10)

    println(sub)
  }
}
