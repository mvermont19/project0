package example

import scala.io._
import scala.collection._
import java.io._
import _root_.scala.util.Success
import _root_.scala.util.Failure
import scala.util.Try

/**
  * The goal of the game will be to make it home safely after waking up in a dark 
  * room alone with no money, phone, or knowledge of where you are
  */
object Main {
    def main(args: Array[String]){
        val story: List[List[String]] = List(List(""))
        val bufferedSource = Source.fromFile("C:/Users/matth/revature/project0/src/main/csv/test.csv")
        for(lines <- bufferedSource.getLines()){
            //move lines into mongoDB
            val line = lines.split(",")
            //println(lines)
            story ++ line
        }
        bufferedSource.close()

        var textLine: Int = 0
        var deadLine: Int = 999 //will set deadLine when story is finished

        while(textLine != deadLine){
            //Print description 
            printText(textLine)

            var validAnswer: Boolean = false
            var answer: Int = 0

            while(!validAnswer){
                //Inform player of options
                printOptions(textLine)

                //Once answer is chosen, read the result and make sure it is valid
                var input = getPlayerInput() match {
                    case Success(a) => {
                        if(a == ""){
                            println("Please give an answer")
                        }
                        else{
                            var num: Char = a.charAt(0)
                            if(num.isDigit && (a.toInt == 1 || a.toInt == 2)){
                                validAnswer = true
                                answer = a.toInt
                            }else{
                                println("Not a valid answer. Try again")
                            }
                        }
                    }
                    case Failure(exception) => {
                        println("Not a valid answer. Try again")
                        throw exception
                    }
                }
            }

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

    /**
      * This prints out the options for the user to decide what to do
      *
      * @param textLine
      */
    def printOptions(textLine: Int) {
        var options = checkOptions(textLine)
        println("Would you like to: (Press 1 or 2)")
        println("1. " + options(0))
        println("2. " + options(1))
    }

    /**
      * This determines if the players answer if valid or not
      */
    def getPlayerInput(): Try[String] = {
        Try(StdIn.readLine())
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