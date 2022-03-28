package smv

import javax.sound.sampled._
import java.io.ByteArrayOutputStream

class AudioSource( useMic: Boolean ) {
  var line: TargetDataLine = null
  var stopped: Boolean = false
  var sampleSize = 8

  def init() = {
    var format: AudioFormat = new AudioFormat(44100, sampleSize, 1, false, true)
    var info: DataLine.Info = new DataLine.Info(classOf[TargetDataLine], format)

    if (!AudioSystem.isLineSupported(info)) {
        // Handle the error ... 

    }
    // Obtain and open the line.
    try {
        if (useMic && AudioSystem.isLineSupported(Port.Info.MICROPHONE)) {
          line = AudioSystem.getLine(Port.Info.MICROPHONE).asInstanceOf[TargetDataLine]
        }
        else {
          line = AudioSystem.getLine(info).asInstanceOf[TargetDataLine];
        }
        line.open(format);
    } catch {
      case ex:LineUnavailableException => ???
      //TODO: Handle the error ... 
    }
  }

  def start(): TargetDataLine = {
    line.start()
    return line
  }

  def getAmplitude(b1: Byte, b2: Byte): Double = {
    (b2 << 8 | b1 & 0xFF) / 32767.0;
  }

  def getAmplitude(b1: Byte): Double = {
    (b1 & 0xFF) / 256.0;
  }

  def read(): Array[Double] = {

    // Assume that the TargetDataLine, line, has already
    // been obtained and opened.
    var out: ByteArrayOutputStream = new ByteArrayOutputStream();
    var numBytesRead: Int = 0
    // var data: Array[Byte] = new Array(line.getBufferSize() / 8 );
    var data: Array[Byte] = new Array(256);

     // Read the next chunk of data from the TargetDataLine.
     // for (i <- 1 to 10) {
       numBytesRead =  line.read(data, 0, data.length);

       
       if(sampleSize == 16){
         var amplitudes: Array[Double] = new Array(data.size / 2)
         for (i <- data.indices) {
            if (i % 2 == 1){
              amplitudes((i-1)/2) = getAmplitude(data(i-1), data(i))
            }
         }
         return amplitudes
       } else {
         var amplitudes: Array[Double] = new Array(data.size)
         amplitudes = data.map(b => getAmplitude(b))
         return amplitudes
       }
       // for (a <- amplitudes) {
       //   println(a)
       // }

     // }
     // Save this chunk of data.
     // out.write(data, 0, numBytesRead);
     // print("\n")
  }
}

