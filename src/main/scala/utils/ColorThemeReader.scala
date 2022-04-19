package smv.utils

import io.circe.Json
import io.circe.syntax._
import io.circe.parser._
import smv.utils.ColorTheme
import smv.utils.ColorTheme._
import scala.io.Source

object ColorThemeReader {
 def read(fileName: String) = {
    val jsonString: String = Source.fromFile(
        // new File(getClass.getClassLoader.getResource("/vertex.vs").getPath)
        "/home/sam/Documents/uni/exchange_semester/courses/ps2/project/smv/src/main/resources/colorthemes/" + fileName + ".json"
      ).mkString
    parseJson(jsonString)
  }

  def parseJson(jsonString: String) = {
    val doc: Json = parse(jsonString).getOrElse(Json.Null)
    decodeColorTheme(doc.hcursor)
  }
}

