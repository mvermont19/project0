package example

import scala.io._
import scala.collection._
import java.io._

import example.Helpers._
import org.mongodb.scala._ 
import javax.print.Doc
import org.mongodb.scala.model.Filters._

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

          //variables that need to be integers
          var gp = split(4).toInt
          var goals = split(5).toInt
          var assists = split(6).toInt
          var shots = split(17).toInt
          var pen = split(8).toInt
          var evg = split(9).toInt
          var evp = split(10).toInt
          var ppg = split(11).toInt
          var ppp = split(12).toInt
          var shg = split(13).toInt
          var shp = split(14).toInt
          var otg = split(15).toInt
          var gwg = split(15).toInt


          var doc: Document = Document(
            "_id" -> id1,
            "name" -> split(0),
            "team" -> split(1),
            "hand" -> split(2),
            "position" -> split(3),
            "games played" -> gp,
            "goals" -> goals,
            "assists"-> assists,
            "shots" -> shots
          )
          id1 += 1
          var doc2: Document = Document(
            "_id" -> id2,
            "playerID" -> id1,
            "plus/minus"-> split(7),
            "penalty mins"-> pen,
            "even strength goals" -> evg,
            "even strength points" -> evp,
            "power play goals" -> ppg,
            "power play points" -> ppp,
            "short handed goals" -> shg,
            "short handed points" -> shp,
            "overtime goals" -> otg,
            "game winning goals" -> gwg,
            "time on ice/game" -> split(18),
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
      

      var answer: Int = 0
      while(answer != 4){
        println("Please choose an option to learn about the NHL: ")
        println("1. Player Info \n2. Team Totals \n3. Changes \n4. Exit")
        answer = getUserInput()
        answer match {
          case 1 => {
            println("What would you like to know about the Players: ")
            println("1. Total Points \n2. Shooting % \n3. Goals per Game \n4. Lefties vs Righties \n5. Position-wise")
            answer = getUserInput()
            answer match {
              case 1 => {
                //Points
              }
              case 2 => {
                //Shooting %
              }
              case 3 => {
                //Goals per game
              }
              case 4 => {
                //L v R
              }
              case 5 => {
                //Position
              }
            }
          }
          case 2 => {
            println("What would you like to know about the Teams: ")
            println("1. Total Goals \n2. Shooting % \n3. Goals per Game \n4. Show Players per Team")
            answer = getUserInput()
            answer match {
              case 1 => {
                //Goals
              }
              case 2 => {
                //Shooting %
              }
              case 3 => {
                //Goals per game
              }
              case 4 =>{
                //Player grouping
              }
              case _ => println("Not a valid answer. Try again")
            }
          }
          case 3 => {
            println("What changes would you like to make: ")
            println("1. Create a player \n2. Change a player's stats \n3. Delete a player ")
            answer = getUserInput()
            answer match {
              case 1 => {
                //Create a player
              }
              case 2 => {
                //Change a player
              }
              case 3 => {
                //Delete a player
              }
              case _ => println("Not a valid answer. Try again")
            }
          }
          case 4 => {
            println("Exiting the program. Goodbye!")
          }
          case _ => println("Not a valid answer. Try again")
        }

      }
      col.drop()
      col2.drop()
      client.close()
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
        if(userInput.isEmpty())  {
          println("Please give an answer")
        }
        else {
          var num: Char = userInput.charAt(0)
          if(num.isDigit && (userInput.toInt == 1 || userInput.toInt == 2
                              || userInput.toInt == 3 || userInput.toInt == 4  || userInput.toInt == 5)){
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