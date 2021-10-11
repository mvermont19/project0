package example

import scala.io._

/**
  * The goal of the game will be to make it home safely after waking up in a dark 
  * room alone with no money, phone, or knowledge of where you are
  */
object Main {
    def main(args: Array[String]){
        val bufferedSource = Source.fromFile("C:/Users/matth/revature/project0/src/main/csv/test.csv")
        for(lines <- bufferedSource.getLines()){
            //move lines into mongoDB
            //println(lines)
        }
        bufferedSource.close()

        var textLine: Int = 0
        printText(textLine)
        textLine += 1

        var answer = StdIn.readLine()
        println(answer)

    }

    /**
      * Printing all the instructions for the player
      * Need to get them from mongoDB
      * For now just printing
      *
      * @param textLine
      */
    def printText(textLine: Int){
        textLine match {
            case 0 => println("Intro to Game")
            case 1 => println("")
        }


    }
}