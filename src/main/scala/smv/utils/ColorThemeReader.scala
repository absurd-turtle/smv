package smv.utils

import io.circe.Json
import io.circe.syntax._
import io.circe.parser._
import smv.utils.ColorTheme
import smv.utils.ColorTheme._
import scala.io.Source
import scala.collection.mutable.HashMap

object ColorThemeReader {
  var themes = HashMap (
    0 -> "gruvbox",
    1 -> "ashes.dark",
    2 -> "ashes.light",
    3 -> "bespin.dark",
    4 -> "chalk.dark",
    5 -> "gruvbox.light",
    6 -> "monokai",
    7 -> "test"
  )

 def read(fileName: String) = {
    val jsonString: String = Source.fromFile(
        // new File(getClass.getClassLoader.getResource("/vertex.vs").getPath)
        "src/main/resources/colorthemes/" + fileName + ".json"
      ).mkString
    parseJson(jsonString)
  }

  def parseJson(jsonString: String) = {
    val doc: Json = parse(jsonString).getOrElse(Json.Null)
    decodeColorTheme(doc.hcursor)
  }

  def getKnownTheme(index: Int) = {
    readColorTheme(themes(index))
  }
  def getKnownThemeCount() = {
    themes.size
  }

  def readColorTheme(colorthemeFileName: String): ColorTheme = {
    ColorThemeReader.read(colorthemeFileName) match {
      case Right(x) => x
      case Left(x) => 
        println("Colortheme could not be read. Default colortheme will be used.")
        getDefaultColorTheme()
    }
  }

  def getDefaultColorTheme() = {
        new ColorTheme("smv default", "smv", List(
          new Color(1.0f, 1.0f, 1.0f),
          new Color(1.0f, 1.0f, 1.0f),
          new Color(1.0f, 1.0f, 1.0f),
          new Color(1.0f, 1.0f, 1.0f),
          new Color(1.0f, 1.0f, 1.0f),
          new Color(1.0f, 1.0f, 1.0f),
          new Color(1.0f, 1.0f, 1.0f),
          new Color(1.0f, 1.0f, 1.0f),
          new Color(1.0f, 1.0f, 1.0f),
          new Color(1.0f, 1.0f, 1.0f),
          new Color(1.0f, 1.0f, 1.0f),
          new Color(1.0f, 1.0f, 1.0f),
          new Color(1.0f, 1.0f, 1.0f),
          new Color(1.0f, 1.0f, 1.0f),
          new Color(1.0f, 1.0f, 1.0f),
          new Color(1.0f, 1.0f, 1.0f),
          ).toArray)
  }

}

