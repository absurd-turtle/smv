package smv.animations.mutualAttraction

import smv.AudioSource
import smv.animations.engine.AnimationItem
import smv.animations.engine.Mesh
import org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN
import org.lwjgl.glfw.GLFW.GLFW_KEY_UP
import org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE
import smv.animations.engine.IAnimationLogic
import smv.animations.engine.Window
import smv.animations.geometry.Quad._
import com.meapsoft.FFT
import smv.animations.Renderer
import smv.animations.mutualAttraction.Mover
import smv.animations.geometry.VectorUtils._
import org.joml.Vector2d
import org.joml.Matrix2f
import org.joml.Matrix2d
import smv.audio.Analyzer.getBands
import smv.color.Color
import smv.color.ColorTheme

class MutualAttraction(audioSource: AudioSource, colorTheme: ColorTheme) extends IAnimationLogic {

    var direction = 0

    var color = 0.0f

    var counter = 0
    
    var renderer: Renderer = new Renderer()

    var mesh: Mesh = null
    var items: Array[AnimationItem] = null
    var movers: Array[Mover] = null

    var sun: Mover = null

    val colors: List[Color] = colorTheme.color.toList

    def createMovers(amount: Int, mass: Float = 10): Array[Mover] = {
      var movers = new Array[Mover](amount)
      for (i <- 0 until amount ) {
        var pos = new Vector2d(i * 0.1 - 0.5 , 0)
        var vel = new Vector2d(0.1f,0.1f)

        // rotate 90 degrees
        vel = vel.mulTranspose(new Matrix2d(0, -1, 1, 0))
        movers(i) = new Mover(pos.x, pos.y, vel.x, vel.y, mass )
      }
      return movers
    }
    
    @Override
    def init(window: Window) = {
        renderer.init(window)
        movers = createMovers(8, 0.01f)
        sun = new Mover(0, 0, 0, 0, 5f)
    }

    @Override
    def input(window: Window) = {
        if (window.isKeyPressed(GLFW_KEY_ESCAPE)) {
          cleanup()
          System.exit(0)
        }
    }

    @Override
    def update(interval: Float) = {
        var update = false
        val samples = audioSource.getAudioBuffer().clone()
        items = new Array[AnimationItem](movers.length)
        if(counter > 5 ){
          counter = 0
          update = true
        }
        counter = counter + 1

      val bands = getBands(samples)

      for (i <- 0 until movers.length) {
        movers(i).mass = 0.01f * constrain(bands(i), 0.1f, 10f)
      }

      // calculate attraction
      if(update) {
        for (mover <- movers) {
          sun.attract(mover)
          for (other <- movers){
            if (mover != other){
              mover.attract(other)
            }
          }
        }
      }

        // update and draw movers
        for (i <- 0 until movers.length) {
          var mover = movers(i)
          if(update) {
            mover.update()
          }
          //draw
          items(i) = new AnimationItem(
            createQuadMesh(mover.pos.x.asInstanceOf[Float], mover.pos.y.asInstanceOf[Float], -1f,
              0.002f * bands(i).asInstanceOf[Float], 0.002f * bands(i).asInstanceOf[Float],
              true, colors(i))
          )
        }
    }

    @Override
    def render(window: Window) = {
        window.setClearColor(color, color, color, 0.0f)
        renderer.render(window, items)
    }

    @Override
    def cleanup() = {
        renderer.cleanup()
    }

}
