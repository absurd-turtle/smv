import javax.sound.sampled._
import java.io.ByteArrayOutputStream

class AudioSource( useMic: Boolean ) {
  var line: TargetDataLine = null
  var stopped: Boolean = false

  def init() = {
    var format: AudioFormat = new AudioFormat(44100, 8, 2, false, true)
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

  def read() = {

    // Assume that the TargetDataLine, line, has already
    // been obtained and opened.
    var out: ByteArrayOutputStream = new ByteArrayOutputStream();
    var numBytesRead: Int = 0
    var data: Array[Byte] = new Array(line.getBufferSize() / 5);

    //TODO: Here, stopped is a global boolean set by another thread.
    while (!stopped) {
       // Read the next chunk of data from the TargetDataLine.
       numBytesRead =  line.read(data, 0, data.length);
       // Save this chunk of data.
       out.write(data, 0, numBytesRead);
       for (b <- data) {
         print(b.toString +", ")
       }
       print("\n")
    }     
  }
}

