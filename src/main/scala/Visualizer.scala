package smv

import picocli.CommandLine
import picocli.CommandLine.{Command, Option, Parameters}

import smv.animations.Animation
import smv.AudioSource
import smv.utils.ColorThemeReader
import smv.utils.ColorTheme
import smv.utils.Color
import java.util.concurrent.Callable

@Command(name = "smv", mixinStandardHelpOptions = true, version = Array("smv 0.1"),
  description = Array("plays audio reactive animations"))
class Visualizer extends Callable[Integer] {

  @Option(names = Array("-m", "--microphone"), description = Array("use microphone input"))
    private var useMic: Boolean = false

  @Option(names = Array("-l", "--line-in"), description = Array("use line-in input instead of microphone"))
    private var useLineIn: Boolean = false

  @Option(names = Array("-f", "--file"), description = Array("use file input instead of microphone (not implemented)"))
    private var fileName: String = ""

  @Option(names = Array("-v", "--visualization"), description = Array("play specific visualization by name or index"))
    private var visualizationName: String = "SoundSpectrumVisualizer"

  @Option(names = Array("-t", "--timeline"), description = Array("start the application with a timeline"))
    private var timelineFileName: String = ""

  @Option(names = Array("-c", "--colortheme"), description = Array("start the application with a colortheme"))
    private var colorthemeFileName: String = ""

  @Option(names = Array("--loop"), description = Array("loop through animations"))
    private var loopAnimations: Boolean = false


  @Override
  def call(): Integer = {
    println("This is a music visualizer")

    println("options")
    println("useMic", useMic)
    println("useLineIn", useLineIn)
    println("fileName", fileName)
    println("visualizationName", visualizationName)
    println("timelineFileName", timelineFileName)
    println("colorthemeFileName", colorthemeFileName)
    println("loopAnimations", loopAnimations)

    var audioSource = new AudioSource(useMic || !useLineIn)
    audioSource.init()
    audioSource.start()

    new Animation(audioSource, readColorTheme()).start()

    return 0
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

object Visualizer {
  def main(args: Array[String]): Unit = {
    System.exit(new CommandLine(new Visualizer()).execute(args: _*))
  }
}

