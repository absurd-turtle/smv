import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import smv.utils.ColorThemeReader
import smv.utils.ColorTheme
import smv.utils.Color

class ColorThemeReaderTest extends AnyFunSpec with Matchers {
  describe("ColorTheme should consist of name, author and color (List of colors with floating point r, g, b values between 0 and 1)") {
    describe("read") {
      it("should read a user defined colortheme in json format and covert it to an object of ColorTheme") {
        val result = ColorThemeReader.read("test")
        val expectedCt = new ColorTheme("Test", "AT", List(
            Color(1f,1f,1f),
            Color(1f,1f,1f),
            Color(1f,1f,1f),
            Color(1f,1f,1f),
            Color(1f,1f,1f),
            Color(1f,1f,1f),
            Color(1f,1f,1f),
            Color(0f,0f,0f),
            Color(0f,0f,0f),
            Color(0f,0f,0f),
            Color(0f,0f,0f),
            Color(0f,0f,0f),
            Color(0f,0f,0f),
            Color(1f,0f,0f),
            Color(0f,1f,0f),
            Color(0f,0f,1f),
          ).toArray)

        result match {
          case Right(x) => 
            assert(x.author == expectedCt.author)
            assert(x.name == expectedCt.name)
            assert(x.color.toList == expectedCt.color.toList)
          case Left(x) => 
            println("result not an instance of ColorTheme")
            assert(false)
        }
      }
    }
  }
}

