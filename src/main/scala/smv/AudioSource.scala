package smv

import javax.sound.sampled._
import java.io.ByteArrayOutputStream
import java.io.File
import smv.audio.SimpleAudioPlayer


// Wrapper class for the SimpleAudioPlayer
// It is used to make it run as a Thread
class Player(fileName: String, bufferSize: Int) extends Runnable {
  var player: SimpleAudioPlayer = new SimpleAudioPlayer(bufferSize)

  override def run(): Unit = {
    player.play(fileName)
  }

  def getOutput(): Array[Byte] = synchronized {
    this.player.getOut()
  }
}


/**
  * AudioSource is an adapter class for different audio sources like microphone, line-in and wav files
  *
  * @param source name of the source used ("mic", "line-in", "file")
  * @param fileName If "file" is chosen as source, the file path is required
  */
class AudioSource( source: String, fileName: String = "" ) extends Runnable {

  var line: TargetDataLine = null

  var player: Player = null

  var stopped: Boolean = false
  var sampleSize = 8

  var byteOut = new Array[Byte](256)
  var out: Array[Double] = new Array(128)

  /**
  * Method which opens the standard microphone of the OS
  *
  * @param format
  * @return TargetDataLine of the microphone
  */
  def openMic(format: AudioFormat) = {
     AudioSystem.getTargetDataLine(format).asInstanceOf[TargetDataLine]
  }

  // TODO: let the user choose from available mixers
  /**
  * prints all the available mixers 
  */
  def printMixers() = {
     var mixerInfos = AudioSystem.getMixerInfo()
     for (info <- mixerInfos){
        var m: Mixer = AudioSystem.getMixer(info)
        var lineInfos = m.getSourceLineInfo()
        for (lineInfo <- lineInfos){
          System.out.println (info.getName()+"---"+lineInfo)
          var line = m.getLine(lineInfo)
          System.out.println("\t-----"+line)
        }
        lineInfos = m.getTargetLineInfo()
        for (lineInfo <- lineInfos){
          System.out.println(m.toString() + "---"+lineInfo)
          var line = m.getLine(lineInfo)
          System.out.println("\t-----"+line)
        }
     }
  }

  /**
  * initialize AudioSource
  */
  def init() = {
    var format: AudioFormat = new AudioFormat(44100, sampleSize, 1, false, true)

    // Obtain and open the line.
    try {
      source match {
        case "mic" => {
          // doesn't work as expected
          // if (!AudioSystem.isLineSupported(Port.Info.MICROPHONE)){
          //   System.err.println("[error] Error initiliazing AudioSource. System does not support a microphone.")
          // }
          line = openMic(format)
          line.open(format)
          line.start()
        }
        case "line-in" => {
          var info: DataLine.Info = new DataLine.Info(classOf[TargetDataLine], format)
          if (!AudioSystem.isLineSupported(info)){
            System.err.println("[error] Error initiliazing AudioSource. System does not support line-in with " + info + ".")
          }
          line = AudioSystem.getLine(info).asInstanceOf[TargetDataLine]
          line.open(format)
        }
        case "file" => {
          player = new Player(fileName, 256)
          new Thread(player).start()
        }
      }
    } catch {
      case ex:LineUnavailableException =>
        System.err.println("The chosen Line/Source is not available.")
    }
  }

  /**
  * synchronized method to write the current data to the variable "out" which is used as "shared data" between different threads
  *
  * @param data
  */
  def writeToOut(data: Array[Double]) = synchronized {
    out = data
  }


  /**
  * synchronized method to get the current audio data 
  *
  * @param data
  */
  def getAudioBuffer(): Array[Double] = synchronized {
    return out
  }

  override def run(): Unit = {
    while(!stopped) {
      // writing current input to the parameter out
      writeToOut(read())
      if(source == "file"){
        //update buffer up to 100 times per second
        Thread.sleep(10)
      }
    }
    if (source == "file"){
      println("done reading file")
    }
    else {
      line.close()
    }
  }

  def stop() = {
    stopped = true
  }

  // translate two bytes of audio data to one double value
  def getAmplitude(b1: Byte, b2: Byte): Double = {
    (b2 << 8 | b1 & 0xFF) / 32767.0
  }

  // translate one byte of audio data to one double value
  def getAmplitude(b1: Byte): Double = {
    (b1 & 0xFF) / 256.0
  }

  /**
  * read data from the audio feed
  *
  * @return
  */
  def read(): Array[Double] = {
    source match {
      case "file" => {
        // get current output from the File Player
        var playerOut = player.getOutput()
        // currently the application assumes that when content from a file is read one sample is 16 bit in size and mono (1 channel)
        byteToDouble16Mono(
          playerOut
        )
      }
      case _ => readFromLine()
    }
  }

  /**
  * read samples from the TargetDataLine
  *
  * @return
  */
  def readFromLine(): Array[Double] = {
    // number of bytes that got read from line
    var numBytesRead: Int = 0

    // byte array of data that was read from the line
    var data: Array[Byte] = new Array(256)

    // Read the next chunk of data from the TargetDataLine.
    numBytesRead = line.read(data, 0, data.length)

    // when sample size is 16 each byte pair represents one sample point.
    // the byte pair will be converted to one double representing the amplitude of that sample
    if(sampleSize == 16){
      byteToDouble16Mono(data)
    } 
    // if the sample size is 8 each byte represents one sample point.
    else {
      byteToDouble8Mono(data)
    }
  }

  /**
  * Translate array of byte data (16 bit mono) to double array
  *
  * @param data
  * @return
  */
  def byteToDouble16Mono(data: Array[Byte]): Array[Double] = {
      var amplitudes: Array[Double] = new Array(data.size / 2)
      for (i <- data.indices) {
        if (i % 2 == 1){
          amplitudes((i-1)/2) = getAmplitude(data(i-1), data(i))
        }
      }
      return amplitudes
  }

  /**
  * Translate array of byte data (8 bit mono) to double array
  *
  * @param data
  * @return
  */
  def byteToDouble8Mono(data: Array[Byte]): Array[Double] = {
      var amplitudes: Array[Double] = new Array(data.size)
      amplitudes = data.map(b => getAmplitude(b))
      return amplitudes
  }
}

