package example

import scala.io._
import scala.collection._
import java.io._
import scala.concurrent._

import example.Helpers._
import org.mongodb.scala._ 
import javax.print.Doc

import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Sorts._
import org.mongodb.scala.model.Projections._
import org.mongodb.scala.model.Aggregates._
import org.mongodb.scala.model.Updates._
import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Accumulators
import com.mongodb.client.model.Projections


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
      var exit: Boolean = false
      while(!exit){
        println("")
        Thread.sleep(1000)

        val intro: String = "Please choose an option to learn about the NHL: "
        val startingScreen: String = "1. Player Info \n2. Team Totals \n3. Changes \n4. Exit"
        val playerInfo: String = "What would you like to know about the Players: "
        val playerChoices: String = "1. Total Points \n2. Shooting % \n3. Goals per Game \n4. Lefties vs Righties \n5. Position-wise"
        val teamInfo : String = "What would you like to know about the Teams: "
        val teamChoices: String = "1. Total Goals \n2. Shooting % \n3. Goals per Game \n4. Show Players per Team"
        val changesInfo: String = "What changes would you like to make: "
        val changesChoices: String = "1. Create a player \n2. Change a player's stats \n3. Delete a player "


        answer = getUserInput(intro, startingScreen)
        answer match {
          case 1 => {
            answer = getUserInput(playerInfo, playerChoices)
            answer match {
              case 1 => {
                //Points
                // col.aggregate(
                //   Seq(
                //    // Aggregates.group(
                //     //  "$goals", Accumulators.sum("add", 1)
                //     //),

                //     Aggregates.project(
                //       Projections.fields(
                //         Projections.excludeId(),
                //         Projections.include("name"),
                //         Projections.include("goals"),
                //         Projections.include("assists"),
                //         Projections.computed("points", Accumulators.sum("goals", "add")
                //         )))
                //     //     Projections.include(Accumulators.sum("sum", 1))
                //         //: {Accumulators.sum("$goals", 1), Accumulators.sum("$assists", 1)}))
                //       )
                //    // )

                //   //  group(Document("_id" -> "$name"), Accumulators.sum("goals", 1), Accumulators.sum("assists", 1)),
                //   //   project(Document(
                //   //     "name" -> "$name",
                //   //     "goals" -> "$goals",
                //   //     "assists" -> "$assists",
                //   //     "points" -> "points"
                //   //   ))
                //   //)
                // ).printResults()
              }
              case 2 => {
                //Shooting %
              }
              case 3 => {
                //Goals per game
              }
              case 4 => {
                //L v R
                // col.aggregate(
                //   Seq(
                //     group(Document(
                //       {"_id" : "$hand",
                //       {"TotalGoals" : {"$sum" : "$goals"}}}
                //       )
                //     )
                //     // project(
                //     //   "_id" : 0,
                //     //   "hand" : _id,
                //     //   "Total Goals": 1
                //     // )
                //   )
                // ).printResults()
              }
              case 5 => {
                //Position
                // col.aggregate(
                //   Seq(
                //     group(Document(
                //       {"_id" : "$position",
                //       {"TotalGoals" : {"$sum" : "$goals"}}}
                //       )
                //     )
                //     // project(
                //     //   "_id" : 0,
                //     //   "hand" : _id,
                //     //   "Total Goals": 1
                //     // )
                //   )
                // ).printResults()
              }
            }
          }
          case 2 => {
            answer = getUserInput(teamInfo, teamChoices)
            answer match {
              case 1 => {
                //Goals
                // col.aggregate(
                //   Seq(
                //     group(Document(
                //       {"_id" : "$team",
                //       {"TotalGoals" : {"$sum" : "$goals"}}}
                //       )
                //     )
                //     // project(
                //     //   "_id" : 0,
                //     //   "hand" : _id,
                //     //   "Total Goals": 1
                //     // )
                //   )
                // ).printResults()
              }
              case 2 => {
                //Shooting %
              }
              case 3 => {
                //Goals per game
              }
              case 4 =>{
                //Player grouping
                //Aggregates.sort(orderBy(descending("team"))).printResults
                // col.aggregate(
                //   Seq(
                //     group(
                //       sort(orderBy(descending("team")))
                //     )
                //   )
                // ).printResults()
              }
              case _ => println("Not a valid answer. Try again")
            }
          }
          case 3 => {
            answer = getUserInput(changesInfo, changesChoices)
            answer match {
              case 1 => {
                //Create a player
                println("Please give the players name: ")
                var name: String = StdIn.readLine()
                println("Please give the players handedness (R or L): ")
                var hand: String = StdIn.readLine()
                println("Please give the players team: ")
                var team: String = StdIn.readLine()
                println("Please give the players position: ")
                var pos: String = StdIn.readLine()
                println("Please give the players games played: ")
                var gp: Int = StdIn.readInt()
                println("Please give the players goals: ")
                var goals: Int = StdIn.readInt()
                println("Please give the players assists: ")
                var assists: Int = StdIn.readInt()
                println("Please give the players shots: ")
                var shots: Int = StdIn.readInt()

                var doc: Document = new Document(
                  "_id" -> id1,
                  "name" -> name,
                  "team" -> team,
                  "hand" -> hand,
                  "position" -> pos,
                  "games played" -> gp,
                  "goals" -> goals,
                  "assists"-> assists,
                  "shots" -> shots
                )
                id1 += 1

                col.insertOne(doc)


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
            exit = true
          }
          case _ => println("Not a valid answer. Try again")
        }

      }
      col.deleteMany(gte("_id", 0)).printResults()
      col2.deleteMany(gte("_id", 0)).printResults()
      client.close()
    }

    /**
      * This helper method gets the user input while checking to make
      * sure it is a valid choice and returns their answer
      *
      * @return
      */
    def getUserInput(strings: String*): Int = {
      var validAnswer: Boolean = false
      var userInput: String = ""
      var answer = 0

      while(!validAnswer){
        println(strings(0))
        println(strings(1))
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