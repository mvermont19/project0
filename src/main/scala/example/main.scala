package example

import scala.io._
import scala.collection._

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
        var deadLine: Int = 999 //will set deadLine when story is finished

        while(textLine != deadLine){
            //Print description 
            printText(textLine)

            //Inform player of options
            printOptions(textLine)

            //Once answer is chosen, read the result
            //return new textLine and set it
            var answer = StdIn.readLine()
            //println(answer)
            var newLine: Int = printResults(textLine, answer.toInt)
            textLine = newLine
        }

        println("You are now dead")

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

    /**
      * After the choice is made by the user, check to see what happens
      * as a result of those choices
      *
      * @param textLine
      * @return
      */
    def checkOptions(textLine: Int): List[String] = {
        //read tab from mongoDB
        //split by , returning a list (or tuple) of results
        List("a", "b")
    }

    def printOptions(textLine: Int) {
        var options = checkOptions(textLine)
        println("Would you like to: (Press 1 or 2)")
        println(options(0))
        println(options(1))
    }

    /**
      * This will pull the result from mongoDB using what line we are one
      * and the answer that the user gave
      *
      * @param textLine
      * @param answer
      * @return
      */
    def getResults(textLine: Int, answer: Int): String = {
        "This is a test result,999"
    }

    /**
      * This will print out the result of what happened in the story and
      * return the new textLine that corresponds to that event
      *
      * @param textLine
      * @param answer
      * @return
      */
    def printResults(textLine: Int, answer: Int): Int = {
        var results: String = getResults(textLine, answer)
        val res = results.split(",")
        println(res(0))
        res(1).toInt
    }
}