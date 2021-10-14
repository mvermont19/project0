package example

import scala.io._
import scala.collection._
import java.io._

import example.Helpers._
import org.mongodb.scala._ 


/**
  * This main method in the beginning reads in a csv file and creates a mongodb database and collection
  * to insert all the information from the csv file into the database
  */
object Main {
    def main(args: Array[String]){

      val client: MongoClient = MongoClient()
      val db: MongoDatabase = client.getDatabase("NHL")
      val col: MongoCollection[Document] = db.getCollection("Players")

        //data gotten from nhl.com for the 2019-2020 season
        val bufferedSource = Source.fromFile("C:/Users/matth/revature/project0/src/main/csv/nhl.csv")
        for(line <- bufferedSource.getLines()){
            //move lines into mongoDB
            //println(line)
            //println(line.split(","))
            //var brokenUpLine = line.split(",").map(_.trim())
            //println(line)
            //story ++ brokenUpLine
        }
        //println(story)
        bufferedSource.close()

        var answer: String = ""
        while(answer.toInt != 9){

            var validAnswer: Boolean = false

            while(!validAnswer){

              answer = StdIn.readLine()
              if(answer == "")  {
                println("Please give an answer")
              }
              else {
                var num: Char = answer.charAt(0)
                if(num.isDigit && (answer.toInt == 1 || answer.toInt == 2)){
                  validAnswer = true
                }else{
                  println("Not a valid answer. Try again")
                }
              }
            }


          }

    }
}