package smv.animations.mutualAttraction

import smv.AudioSource
import smv.animations.engine.AnimationItem
import smv.animations.engine.Mesh

import org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

import smv.animations.engine.IAnimationLogic;
import smv.animations.engine.Window;

import smv.animations.geometry.Quad._

import com.meapsoft.FFT
import smv.animations.Renderer
import smv.animations.mutualAttraction.Mover

import smv.animations.geometry.VectorUtils._

import org.joml.Vector2d
import org.joml.Matrix2f
import org.joml.Matrix2d

import smv.utils.Color
import smv.utils.ColorTheme

class MutualAttraction(audioSource: AudioSource, colorTheme: ColorTheme) extends IAnimationLogic {

    var direction = 0;

    var color = 0.0f;
    
    var renderer: Renderer = new Renderer()

    var mesh: Mesh = null
    var quad1: AnimationItem = null
    var quad2: AnimationItem = null
    var items: Array[AnimationItem] = null
    var movers: Array[Mover] = null

    var sun: Mover = null

    val colors: List[Color] = colorTheme.color.toList
    
    @Override
    def init(window: Window) = {
        renderer.init(window);
        movers = new Array[Mover](10)

        
        //create movers
        for (i <- 0 until 10 ) {
          var pos = new Vector2d(i * 0.1 - 0.5 , 0)
          var vel = new Vector2d(0.01,0.01)
          // vel.setMag(random(10, 15))
          // vel = vel.mul(magnitudeFactor(vel, 0.01))
          
          // pos.setMag(100)
          // pos = pos.mul(magnitudeFactor(pos, 0.01))

          // vel.rotate(PI / 2);
          // rotate 90 degrees
          vel = vel.mulTranspose(new Matrix2d(0, -1, 1, 0))
          var m = 10;
          movers(i) = new Mover(pos.x, pos.y, vel.x, vel.y, m )
        }

        sun = new Mover(0, 0, 0, 0, 50)
    }

    @Override
    def input(window: Window) = {
        if (window.isKeyPressed(GLFW_KEY_ESCAPE)) {
          cleanup()
          System.exit(0);
        }
    }

    @Override
    def update(interval: Float) = {
        val amplitudes = audioSource.getAudioBuffer().clone()
        
        //TODO: move this part to AudioSource or new Class AudioAnalyzer
        var fft = new FFT(amplitudes.length)
        var im = (for (x <- Range(0, amplitudes.length))
          yield 0.0).toArray
        fft.fft(amplitudes, im)

        

        items = new Array[AnimationItem](movers.length)
        
        // calculate attraction
        for (mover <- movers) {
          sun.attract(mover)
          for (other <- movers){
            if (mover != other){
              mover.attract(other)
            }
          }
        }

        // //update and draw movers
        for (i <- 0 until movers.length) {
          var mover = movers(i)
          mover.update()       
          //draw
          items(i) = new AnimationItem(
            createQuadMesh(mover.pos.x.asInstanceOf[Float], mover.pos.y.asInstanceOf[Float], -2.0f, 0.04f, 0.04f, true, colors(i))
          )
        }

        // items(items.length-1) = new AnimationItem(
        //   createQuadMesh(sun.pos.x.asInstanceOf[Float], sun.pos.y.asInstanceOf[Float], -2.0f, 0.1f, 0.1f, true)
        // )
    }

    @Override
    def render(window: Window) = {
        window.setClearColor(color, color, color, 0.0f);
        renderer.render(window, items);
    }

    @Override
    def cleanup() = {
        renderer.cleanup();
    }

}
