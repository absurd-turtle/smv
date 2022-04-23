package smv

import javax.sound.sampled._
import java.io.ByteArrayOutputStream
import java.io.File
import smv.audio.SimpleAudioPlayer

class Player(fileName: String, bufferSize: Int) extends Runnable {
  var player: SimpleAudioPlayer = new SimpleAudioPlayer(bufferSize);

  override def run(): Unit = {
    player.play(fileName)
  }

  def getOutput(): Array[Byte] = synchronized {
    this.player.getOut()
  }
}


class AudioSource( source: String, fileName: String = "" ) extends Runnable {

  var line: TargetDataLine = null

  var player: Player = null

  var stopped: Boolean = false
  var sampleSize = 8

  var byteOut = new Array[Byte](256)
  var out: Array[Double] = new Array(128)

  def openMic(format: AudioFormat) = {
     // AudioSystem.getLine(Port.Info.MICROPHONE).asInstanceOf[TargetDataLine]
     AudioSystem.getTargetDataLine(format).asInstanceOf[TargetDataLine]
  }

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

  def init() = {
    var format: AudioFormat = new AudioFormat(44100, sampleSize, 1, false, true)

    // Obtain and open the line.
    try {
      source match {
        case "mic" => {
          if (!AudioSystem.isLineSupported(Port.Info.MICROPHONE)){
            System.err.println("[error] Error initiliazing AudioSource. System does not support a microphone.")
          }
          line = openMic(format)
          line.open(format);
          line.start()
        }
        case "line-in" => {
          var info: DataLine.Info = new DataLine.Info(classOf[TargetDataLine], format)
          if (!AudioSystem.isLineSupported(info)){
            System.err.println("[error] Error initiliazing AudioSource. System does not support line-in with " + info + ".")
          }
          line = AudioSystem.getLine(info).asInstanceOf[TargetDataLine];
          line.open(format);
        }
        case "file" => {
          player = new Player(fileName, 256)
          new Thread(player).start()
        }
      }
    } catch {
      case ex:LineUnavailableException => ???
      //TODO: Handle the error ... 
    }
  }

  def writeToOut(data: Array[Double]) = synchronized {
    out = data
  }

  def getAudioBuffer(): Array[Double] = synchronized {
    return out
  }

  override def run(): Unit = {
    while(!stopped) {
      // writing current input to the parameter out
      writeToOut(read())
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

  def getAmplitude(b1: Byte, b2: Byte): Double = {
    (b2 << 8 | b1 & 0xFF) / 32767.0;
  }

  def getAmplitude(b1: Byte): Double = {
    (b1 & 0xFF) / 256.0;
  }

  def read(): Array[Double] = {
    source match {
      case "file" => {

        var playerOut = player.getOutput()
        byteToDouble16Mono(
          playerOut
        )
      }
      case _ => readFromLine()
    }
  }

  def readFromLine(): Array[Double] = {
    // number of bytes that got read from line
    var numBytesRead: Int = 0

    // byte array of data that was read from the line
    var data: Array[Byte] = new Array(256);

    // Read the next chunk of data from the TargetDataLine.
    numBytesRead = line.read(data, 0, data.length);

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

  def byteToDouble16Mono(data: Array[Byte]): Array[Double] = {
      var amplitudes: Array[Double] = new Array(data.size / 2)
      for (i <- data.indices) {
        if (i % 2 == 1){
          amplitudes((i-1)/2) = getAmplitude(data(i-1), data(i))
        }
      }
      return amplitudes
  }

  def byteToDouble8Mono(data: Array[Byte]): Array[Double] = {
      var amplitudes: Array[Double] = new Array(data.size)
      amplitudes = data.map(b => getAmplitude(b))
      return amplitudes
  }
}

