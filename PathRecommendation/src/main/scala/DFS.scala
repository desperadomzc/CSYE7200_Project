import org.apache.spark.graphx.VertexId

import scala.collection.mutable.ArrayBuffer

object DFS {

  def DFS(destination: Array[VertexId], start: Int, count: Int, temp: ArrayBuffer[VertexId], res:ArrayBuffer[ArrayBuffer[VertexId]]):Unit ={
    if(count == 0){
//      print(temp.mkString)
      res.append(temp)
      print(res)
      temp.clear
//      println(0)
    } else {
      for(i <- Range(start, destination.length)){
        temp.append(destination(i))
        DFS(destination, start+1, count-1, temp, res)
        temp-=destination(i)
      }
    }
  }
  def main(args: Array[String]): Unit ={

    val destination:Array[VertexId] = Array(0,1,2,3)
    val res = new ArrayBuffer[ArrayBuffer[VertexId]]()
    DFS(destination, 0, 4, new ArrayBuffer[VertexId], res)
//    println(res(0).mkString)


  }
}
