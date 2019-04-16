import java.io.File

import org.apache.spark.SparkContext
import org.apache.spark.graphx._
import org.apache.spark.graphx.lib.ShortestPaths
import org.apache.spark.sql.SparkSession
import org.apache.spark.rdd.RDD
import org.jgrapht.io._
import org.jgrapht.Graph

object PathRecommendation extends App{

  val spark = SparkSession
    .builder()
    .appName("PathRecommendation")
    .master("local[*]")
    .getOrCreate()


  val s:SimpleGraphMLImporter[String,String] = new SimpleGraphMLImporter()
  val manh: File = new File("data\\manhatten.graphml")
  s.importGraph(,manh)
//  def readGraph(path:String)= {
//  }

  override def main(args: Array[String]): Unit = {
    println(s.getEdgeWeightAttributeName)
  }
}
