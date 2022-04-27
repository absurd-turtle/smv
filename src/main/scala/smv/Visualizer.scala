package smv

import picocli.CommandLine
import picocli.CommandLine.{Command, Option, Parameters}

import smv.animations.Animation
import smv.AudioSource
import smv.color.ColorThemeReader
import smv.color.ColorTheme
import smv.color.Color
import java.util.concurrent.Callable

trait Options {
  val useMic: Boolean
  val useLineIn: Boolean
  val fileName: String
  val visualizationName: String
  val timelineFileName: String
  val colorthemeFileName: String
  val loopAnimations: Boolean
}


@Command(name = "smv", mixinStandardHelpOptions = true, version = Array("smv 0.1"),
  description = Array("plays audio reactive animations"))
class Visualizer extends Callable[Integer] {


  //OPTIONS
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
    private var colorthemeFileName: String = "monokai"

  @Option(names = Array("--loop"), description = Array("loop through animations"))
    private var loopAnimations: Boolean = false

  var audioThread: Thread = null

  @Override
  def call(): Integer = {
    printWelcomeMessage()
    printOptions()

    //initialize AudioSource
    val source = if (useMic) "mic" else if (useLineIn) "line-in" else if (fileName != "") "file" else "mic"
    var audioSource = new AudioSource(source, fileName)
    audioSource.init()
    audioThread = new Thread(audioSource)
    audioThread.start()

    // read user defined colortheme from file
    // if the file cannot be read use default colortheme 
    val colortheme = ColorThemeReader.readColorTheme(colorthemeFileName, false)

    //create Animation
    new Animation(audioSource, colortheme, visualizationName).start()
    //TODO: create Timeline

    return 0
  }
 

  def printOptions() = {
    println(" ")
    println(" |> OPTIONS                             ")
    println(" |--------------------------------------")
    println(" |- useMic: "              + useMic)
    println(" |- useLineIn: "           + useLineIn)
    println(" |- fileName: "            + fileName)
    println(" |- visualizationName: "   + visualizationName)
    println(" |- timelineFileName: "    + timelineFileName)
    println(" |- colorthemeFileName: "  + colorthemeFileName)
    println(" |- loopAnimations: "      + loopAnimations)
    println(" ")
  }

  def printWelcomeMessage() ={
    println("<|-------------------------------------|>")
    println(" | starting smv: some music visualizer | ")
    println("<|-------------------------------------|>")
  }
}

object Visualizer {
  def main(args: Array[String]): Unit = {
    System.exit(new CommandLine(new Visualizer()).execute(args: _*))
  }
}

