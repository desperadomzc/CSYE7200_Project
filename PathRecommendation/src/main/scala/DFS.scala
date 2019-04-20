import java.util

import org.apache.spark.graphx.VertexId

import scala.collection.mutable.ArrayBuffer
import java.util._

object DFS {
  def DFS(destination: Array[VertexId]) ={
    def recurse(destination: Array[VertexId], start: Int, count: Int, temp: ArrayList[VertexId], res:ArrayList[ArrayList[VertexId]]):Unit ={
      if(count == 0){
        print(temp)
        res.add(new util.ArrayList[VertexId](temp))
      }else{
        for(i <- Range(0,destination.length)){
          temp.add(destination(i))
          val copy = destination.toBuffer
          copy.remove(i)
          recurse(copy.toArray,start+1,count-1,temp,res)
          temp.remove(temp.size()-1)
        }
      }
    }
    val temp = new util.ArrayList[VertexId]()
    val res = new util.ArrayList[ArrayList[VertexId]]()
    recurse(destination,0,destination.length,temp,res)
    res
  }
  def main(args: Array[String]): Unit ={
//    println(DFS)

  }
}
