package smv.utils
import io.circe.{ Decoder, Encoder, HCursor, Json }
import scala.math
import scala.collection.mutable.HashMap

case class ColorTheme(name: String, author: String, color: Array[Color])

object ColorTheme {

  def rgbToHex(r: Float, g: Float, b: Float) = {
    "#" + math.round(r * 255).toHexString + math.round(g * 255).toHexString + math.round(b * 255).toHexString 
  }
  def hexToRgb(color: String) = {
    val r: Float = Integer.parseInt(color.substring(1,3), 16) / 255f
    val g: Float = Integer.parseInt(color.substring(3,5), 16) / 255f
    val b: Float = Integer.parseInt(color.substring(5,7), 16) / 255f
    new Color(r,g,b)
  }

  implicit val encodeColorTheme: Encoder[ColorTheme] = new Encoder[ColorTheme] {
    final def apply(theme: ColorTheme): Json = Json.obj(
      ("name", Json.fromString(theme.name)),
      ("author", Json.fromString(theme.author)),
      ("color", Json.fromValues(
        for {
          c <- theme.color
          s = rgbToHex(c.r,c.g,c.b)
        }
        yield Json.fromString(s)
      ))
    )
  }

  implicit val decodeColorTheme: Decoder[ColorTheme] = new Decoder[ColorTheme] {
    final def apply(c: HCursor): Decoder.Result[ColorTheme] =
      for {
        name <- c.downField("name").as[String]
        author <- c.downField("author").as[String]
        color <- c.downField("color").as[List[String]]
      } yield {
        new ColorTheme(name, author, color.map(hexToRgb).toArray)
      }
  }
}
