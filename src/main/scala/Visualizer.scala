package smv

import smv.animations.Animation
import smv.AudioSource

object Visualizer {
  def main(args: Array[String]): Unit = {
    println("This is a music visualizer")
    var audioSource = new AudioSource(true)
    audioSource.init()
    audioSource.start()

    // var amplitudes = audioSource.read()

    new Animation(audioSource).start()
  }
}

