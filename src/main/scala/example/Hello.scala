package example

object Hello extends Greeting with App {
  println(greeting)
  println("changes for fun")
}

trait Greeting {
  lazy val greeting: String = "hello"
}
