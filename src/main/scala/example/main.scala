package example

import scala.io._
import scala.collection._
import java.io._

import example.Helpers._
import org.mongodb.scala._ 
import javax.print.Doc


/**
  * This main method in the beginning reads in a csv file and creates a mongodb database and collection
  * to insert all the information from the csv file into the database
  */
object Main {
    def main(args: Array[String]){

      val client: MongoClient = MongoClient()
      val db: MongoDatabase = client.getDatabase("nhl")
      //splitting the data into 2 collections, 1 for frequently used data
      //other for less used data
      val col: MongoCollection[Document] = db.getCollection("players")
      val col2: MongoCollection[Document] = db.getCollection("player info")

      var id1: Int = 1
      var id2: Int = 1001
      //data gotten from nhl.com for the 2019-2020 season

      try{
        val bufferedSource = Source.fromFile("C:/Users/matth/revature/project0/src/main/csv/nhl.csv")
        for(line <- bufferedSource.getLines().drop(1)){
          //move lines into mongoDB
          var split: Array[String] = line.split(",")

          var doc: Document = Document(
            "_id" -> id1,
            "name" -> split(0),
            "team" -> split(1),
            "hand" -> split(2),
            "position" -> split(3),
            "games played" -> split(4).toInt,
            "goals" -> split(5).toInt,
            "assists"-> split(6).toInt,
            "shots" -> split(17).toInt
          )
          id1 += 1
          var doc2: Document = Document(
            "_id" -> id2,
            "playerID" -> id1,
            "plus/minus"-> split(7),
            "penalty mins"-> split(8).toInt,
            "even strength goals" -> split(9).toInt,
            "even strength points" -> split(10).toInt,
            "power play goals" -> split(11).toInt,
            "power play points" -> split(12).toInt,
            "short handed goals" -> split(13).toInt,
            "short handed points" -> split(14).toInt,
            "overtime goals" -> split(15).toInt,
            "game winning goals" -> split(16).toInt,
            "time on ice/game" -> split(18).toInt,
            "face-off win %" -> split(19)
          )
          id2 += 1
          val observable: Observable[Completed] = col.insertOne(doc)
          observable.subscribe(new Observer[Completed] {
            override def onNext(result: Completed): Unit = println("Inserted")
            override def onError(e: Throwable): Unit = println("Failed")
            override def onComplete(): Unit = println("Completed")
          })
          val observable2: Observable[Completed] = col2.insertOne(doc2)
          observable2.subscribe(new Observer[Completed] {
            override def onNext(result: Completed): Unit = println("Inserted")
            override def onError(e: Throwable): Unit = println("Failed")
            override def onComplete(): Unit = println("Completed")
          })
        }
        bufferedSource.close()
      }
      catch {
        case e: FileNotFoundException => {
          println("Could not find file")
        }
        case _ : Throwable => {
          println("Some other error occured")
        }
      }
      

      var answer: Int = 9
      while(answer != 9){

        // answer = getUserInput() match {
        //   case 1 => println(answer + "")
        //   case _ => println("nothing")
        // }

      }

    }

    /**
      * This helper method gets the user input while checking to make
      * sure it is a valid choice and returns their answer
      *
      * @return
      */
    def getUserInput(): Int = {
      var validAnswer: Boolean = false
      var userInput: String = ""
      var answer = 0

      while(!validAnswer){
        userInput = StdIn.readLine()
        userInput.trim()
        if(userInput.isEmpty())  {
          println("Please give an answer")
        }
        else {
          var num: Char = userInput.charAt(0)
          if(num.isDigit && (userInput.toInt == 1 || userInput.toInt == 9)){
            validAnswer = true
            answer = userInput.toInt
          }
          else{
          println("Not a valid answer. Try again")
          }
        }
      }
      answer
    }
}