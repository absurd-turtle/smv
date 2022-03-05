object Visualizer extends App {
  println("This is a music visualizer")
  var audioSource = new AudioSource(true)
  audioSource.init()
  audioSource.start()
  audioSource.read()
}
