package smv.utils

import io.circe.Json
import io.circe.syntax._
import io.circe.parser._
import smv.utils.ColorTheme
import smv.utils.ColorTheme._
import scala.io.Source
import scala.collection.mutable.HashMap
import java.io.FileNotFoundException
import java.io.IOException

object ColorThemeReader {

  //known colorthemes
  var themes = HashMap (
    0 -> "gruvbox",
    1 -> "ashes.dark",
    2 -> "ashes.light",
    3 -> "bespin.dark",
    4 -> "chalk.dark",
    5 -> "gruvbox.light",
    6 -> "monokai",
    7 -> "reggae"
  )

 /**
  * read a colortheme in json format from the ressources folder
  * throws error if it cannot find the file or the parsing failed
  *
  * @param fileName
  * @return
  */
 def read(fileName: String) = {
    val jsonString: String = Source.fromFile(
        "src/main/resources/colorthemes/" + fileName + ".json"
      ).mkString
    parseJson(jsonString)
  }

  /**
  * Parse a colortheme in json format
  *
  * @param jsonString
  * @return
  */
  def parseJson(jsonString: String) = {
    val doc: Json = parse(jsonString).getOrElse(Json.Null)
    decodeColorTheme(doc.hcursor)
  }


  /**
  * Fetch colorthemes known to the application
  *
  * @param index
  * @return
  */
  def getKnownTheme(index: Int) = {
    readColorTheme(themes(index))
  }

  /**
  * Get amount of colorthemes that are known to the system
  *
  * @return
  */
  def getKnownThemeCount() = {
    themes.size
  }


  /**
  * read a colortheme json document which is located in the ressources directory.
  * returns default colortheme if reading or parsing process fails
  *
  * @param colorthemeFileName
  * @return read or default colortheme
  */
  def readColorTheme(colorthemeFileName: String): ColorTheme = {
      try {
          return ColorThemeReader.read(colorthemeFileName) match {
            case Right(x) => x
            case Left(x) => 
              println("Colortheme could not be read. Default colortheme will be used.")
              getDefaultColorTheme()
          }
      } catch {
          case e: FileNotFoundException => System.err.println("[warning] Couldn't find the file: '" + colorthemeFileName + "'. The app will use the default colortheme.")
          case e: IOException => System.err.println("Had an IOException trying to read that file")
      }
      getDefaultColorTheme()
  }

  /**
  *
  *
  * @return default colortheme
  */
  def getDefaultColorTheme() = {
        new ColorTheme("smv default", "smv", List(
          new Color(1.0f, 1.0f, 1.0f),
          new Color(0.5f, 0.5f, 0.5f),
          new Color(0.5f, 0.5f, 0.5f),
          new Color(0.5f, 0.5f, 0.5f),
          new Color(0.5f, 0.5f, 0.5f),
          new Color(0.5f, 0.5f, 0.5f),
          new Color(0.5f, 0.5f, 0.5f),
          new Color(0.5f, 0.5f, 0.5f),
          new Color(0.5f, 0.5f, 0.5f),
          new Color(0.5f, 0.5f, 0.5f),
          new Color(0.5f, 0.5f, 0.5f),
          new Color(0.5f, 0.5f, 0.5f),
          new Color(0.5f, 0.5f, 0.5f),
          new Color(0.5f, 0.5f, 0.5f),
          new Color(0.5f, 0.5f, 0.5f),
          new Color(0.5f, 0.5f, 0.5f),
          ).toArray)
  }

}

