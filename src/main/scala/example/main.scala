package example

/**
  * The goal of the game will be to make it home safely after waking up in a dark 
  * room alone with no money, phone, or knowledge of where you are
  */
object Main {
    def main(args: Array[String]){
        val bufferedSource = io.Source.fromFile("/main/csv/test.csv")
        for(lines <- bufferedSource.getLines()){
            //move lines into mongoDB
        }
        bufferedSource.close()

        var textLine: Int = 0
        printText(textLine)
        textLine += 1

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