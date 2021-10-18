package example

import scala.io._
import scala.collection._
import java.io._
import scala.concurrent._
import javax.print.Doc

import example.Helpers._

import org.mongodb.scala._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Sorts._
import org.mongodb.scala.model.Projections._
import org.mongodb.scala.model.Aggregates._
import org.mongodb.scala.model.Updates._
import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Accumulators
import com.mongodb.client.model.Projections

import net.liftweb.json._



/**
  * This main method in the beginning reads in a csv file and creates a mongodb database and collection
  * to insert all the information from the csv file into the database
  * Then it goes on to ask the user what they would like to do
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

          //Data that will be used a lot
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

          //Data that will not be used
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

          //To ensure that the data was inserted into the db
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
      
      //Now comes where the user inputs what they want to see
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
            //Player info
            answer = getUserInput(playerInfo, playerChoices)
            answer match {
              case 1 => {
                //Points
                val players = col
                  .aggregate(
                    Seq(
                      Aggregates.group(
                        "$name",
                        Accumulators.sum("goals", "$goals"),
                        Accumulators.sum("assists", "$assists")
                      ),
                      Aggregates.sort(orderBy(ascending("goals"), ascending("assists")))
                    )
                  ).results()

                implicit val formats = DefaultFormats

                case class Player(_id: String, goals: Int, assists: Int)

                for (player <- players) {
	                // Convert the doc into a proper JSON string.
                  val jsonString = player.toJson()
                  //println(jsonString)
	                // Convert the JSON string into a JSON object.
                  val jValue = parse(jsonString)
                  // create a Player object from the string
                  val playersDoc = jValue.extract[Player]
	                // Calculate the total points and print.
                  val total = playersDoc.goals + playersDoc.assists
                  println(s"Player: ${playersDoc._id}, goals: ${playersDoc.goals}, assists: ${playersDoc.assists}, Total Points: $total")
                }
              }
              case 2 => {
                //Shooting %
                val players = col
                  .aggregate(
                    Seq(
                      Aggregates.group(
                        "$name",
                        Accumulators.sum("goals", "$goals"),
                        Accumulators.sum("shots", "$shots")
                      ),
                      Aggregates.sort(orderBy(ascending("goals")))
                    )
                  ).results()

                implicit val formats = DefaultFormats

                case class Player(_id: String, goals: Int, shots: Int)

                for (player <- players) {
	                // Convert the doc into a proper JSON string.
                  val jsonString = player.toJson()
                  //println(jsonString)
	                // Convert the JSON string into a JSON object.
                  val jValue = parse(jsonString)
                  // create a Player object from the string
                  val playersDoc = jValue.extract[Player]
	                // Calculate the shooting % and print.
                  var percent : Double = 0.0
                  if(playersDoc.shots != 0){
                    percent = (playersDoc.goals.toDouble / playersDoc.shots.toDouble) * 100
                  } 
                  println(f"Player: ${playersDoc._id}, goals: ${playersDoc.goals}, shots: ${playersDoc.shots}, Shooting Per: $percent%.2f")
                }
              }
              case 3 => {
                //Goals per game
                val players = col
                  .aggregate(
                    Seq(
                      Aggregates.group(
                        "$name",
                        Accumulators.sum("goals", "$goals"),
                        Accumulators.sum("games", "$games played")
                      ),
                      Aggregates.sort(orderBy(ascending("goals")))
                    )
                  ).results()

                implicit val formats = DefaultFormats

                case class Player(_id: String, goals: Int, games: Int)

                for (player <- players) {
	                // Convert the doc into a proper JSON string.
                  val jsonString = player.toJson()
                  //println(jsonString)
	                // Convert the JSON string into a JSON object.
                  val jValue = parse(jsonString)
                  // create a Player object from the string
                  val playersDoc = jValue.extract[Player]
	                // Calculate the goals per game and print.
                  val gpg = playersDoc.goals.toDouble / playersDoc.games.toDouble
                  println(f"Player: ${playersDoc._id}, goals: ${playersDoc.goals}, games: ${playersDoc.games}, Goals per Game: $gpg%.2f")
                }
              }
              case 4 => {
                //L v R
                col.aggregate(
                  Seq(
                    group("$hand", Accumulators.sum("goals", "$goals")),
                    project(
                      include("hand", "goals")
                    )
                  )
                ).printResults()
              }
              case 5 => {
                //Position
                col.aggregate(
                  Seq(
                    group("$position", Accumulators.sum("goals", "$goals")),
                    project(
                      include("position", "goals")
                    )
                  )
                ).printResults()
              }
            }
          }
          case 2 => {
            //Team info
            answer = getUserInput(teamInfo, teamChoices)
            answer match {
              case 1 => {
                //Goals
                col.aggregate(
                  Seq(
                    group("$team", Accumulators.sum("goals", "$goals")),
                    project(
                      include("team", "goals"),
                    )
                  )
                ).printResults()
              }
              case 2 => {
                //Shooting %
                val teams = col
                  .aggregate(
                    Seq(
                      Aggregates.group(
                        "$team",
                        Accumulators.sum("goals", "$goals"),
                        Accumulators.sum("shots", "$shots")
                      )
                    )
                  ).results()

                implicit val formats = DefaultFormats

                case class Team(_id: String, goals: Int, shots: Int)

                for (team <- teams) {
	                // Convert the doc into a proper JSON string.
                  val jsonString = team.toJson()
                  //println(jsonString)
	                // Convert the JSON string into a JSON object.
                  val jValue = parse(jsonString)
                  // create a Team object from the string
                  val teamDoc = jValue.extract[Team]
	                // Calculate the shooting % and print.
                  var percent = (teamDoc.goals.toDouble / teamDoc.shots.toDouble) * 100
                  println(f"Player: ${teamDoc._id}, goals: ${teamDoc.goals}, shots: ${teamDoc.shots}, Shooting Per: $percent%.2f")
                }
              }
              case 3 => {
                //Goals per game
                val teams = col
                  .aggregate(
                    Seq(
                      Aggregates.group(
                        "$team",
                        Accumulators.sum("goals", "$goals")
                      )
                    )
                  ).results()

                implicit val formats = DefaultFormats

                case class Team(_id: String, goals: Int)

                for (team <- teams) {
	                // Convert the doc into a proper JSON string.
                  val jsonString = team.toJson()
                  //println(jsonString)
	                // Convert the JSON string into a JSON object.
                  val jValue = parse(jsonString)
                  // create a Team object from the string
                  val teamDoc = jValue.extract[Team]
	                // Calculate the shooting % and print.
                  //Because of the shortend season, each team only played 71 games
                  var gpg = teamDoc.goals.toDouble / 71
                  println(f"Player: ${teamDoc._id}, goals: ${teamDoc.goals}, Goals per Game: $gpg%.2f")
                }
              }
              case 4 =>{
                //Player grouping
                col.aggregate(
                  Seq(
                      sort(orderBy(ascending("team")))
                  )
                ).printResults()
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

                var doc: Document = Document(
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

                val observable: Observable[Completed] = col.insertOne(doc)
                observable.subscribe(new Observer[Completed] {
                  override def onNext(result: Completed): Unit = println("Inserted")
                  override def onError(e: Throwable): Unit = println("Failed")
                  override def onComplete(): Unit = println("Completed")
                })
                col.find(equal("name", name)).first().printHeadResult()

              }
              case 2 => {
                //Change a player
                println("Please give the players name: ")
                var name: String = StdIn.readLine()
                col.find(equal("name", name)).first().printHeadResult()
                println("What would you like to update about the player: ")
                println("1. Goals \n2. Assists")
                var choice: Int = StdIn.readInt() 
                choice match {
                  case 1 => {
                    println("Please enter the new number of goals: ")
                    var g = StdIn.readInt()
                    col.updateOne(equal("name", name), set("goals", g)).printHeadResult("Update Result: ")
                  }
                  case 2 => {
                    println("Please enter the new number of assists: ")
                    var a = StdIn.readInt()
                    col.updateOne(equal("name", name), set("assists", a)).printHeadResult("Update Result: ")
                  }
                }
                col.find(equal("name", name)).first().printHeadResult()

              }
              case 3 => {
                //Delete a player
                println("Please give the players name: ")
                var name: String = StdIn.readLine()
                col.find(equal("name", name)).first().printHeadResult()
                col.deleteOne(equal("$name", name)).printHeadResult("Delete Result: ")
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