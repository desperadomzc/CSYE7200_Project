import org.apache.spark.SparkContext
import org.apache.spark.graphx._
import org.apache.spark.graphx.lib.ShortestPaths
import org.apache.spark.sql.SparkSession
import org.apache.spark.rdd.RDD
object PathRecommendation extends App{

  val sc = SparkContext
    .getOrCreate()


  val ca = GraphLoader.edgeListFile(sc,"data\\roadNet-CA.txt")

  val vertexCount = ca.numVertices
  val edges = ca.edges
//  def readGraph(path:String)= {
//  }

  override def main(args: Array[String]): Unit = {
    println(edges.count())
  }
}
