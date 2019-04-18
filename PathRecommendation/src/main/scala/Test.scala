object Test {
  def main(args: Array[String]): Unit = {
    val l = List(1, 2, 3)
    val n = l.toBuffer
    n.remove(1)
    println(l)
    println(n)
  }

}
