package smv

import smv.animations.Animation
import smv.AudioSource
import smv.utils.ColorThemeReader
import smv.utils.ColorTheme
import smv.utils.Color

object Visualizer {
  def main(args: Array[String]): Unit = {
    println("This is a music visualizer")

    var audioSource = new AudioSource(true)
    audioSource.init()
    audioSource.start()

    new Animation(audioSource, readColorTheme()).start()
  }

  def readColorTheme(): ColorTheme = {
    ColorThemeReader.read("monokai") match {
      case Right(x) => x
      case Left(x) => 
        println("Colortheme could not be read. Default colortheme will be used.")
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
}

