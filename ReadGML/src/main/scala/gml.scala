import java.io.BufferedReader

import scala.io.Source

object gml {
  val edges = new Array[(Int, Int, Int)](200)
  val nodes = new Array[String](50)
  var isDirected = false

  val file = Source.fromFile("C:\\Users\\Administrator\\Desktop\\lol_counter.gml")

  val reader: BufferedReader = file.bufferedReader()
  var edgeCount = 0
  var flag = true

  while (reader.readLine() != null) {
    //    val line =
    val words = reader.readLine().trim.split(" ")
    words(0) match {
      //node id and label
      case "id" => val lb = reader.readLine().trim.split(" ")
        lb(0) match {
          case "label" => nodes.update(words(1).toInt, lb(1))
          case _ =>
        }
      //directed or not:
      case "directed" =>
        words(1).toInt match {
          case 0 => isDirected = false
          case 1 => isDirected = true
        }
      //add edges(src,tft,weight)
      case "source" => val tg = reader.readLine().trim.split(" ")
        tg(0) match {
          case "target" => val wt = reader.readLine().trim.split(" ")
            wt(0) match {
              case "weight" =>
                edges.update(edgeCount, (words(1).toInt, tg(1).toInt, wt(1).toInt))
                edgeCount += 1
              case _ =>
            }
          case _ =>
        }
      case _ =>
    }
  }

  def main(args: Array[String]): Unit = {
    println(edges.toList)
    println(nodes.toList)
    print(isDirected)
  }
}
