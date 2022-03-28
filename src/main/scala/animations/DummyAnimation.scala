package smv.animations

import smv.AudioSource
import smv.animations.engine.AnimationItem
import smv.animations.engine.Mesh

import org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

import engine.IAnimationLogic;
import engine.Window;

import com.meapsoft.FFT

class DummyAnimation(audioSource: AudioSource) extends IAnimationLogic {

    var direction = 0;

    var color = 0.0f;
    
    var renderer: Renderer = new Renderer()

    var mesh: Mesh = null
    var quad1: AnimationItem = null
    var quad2: AnimationItem = null
    var items: Array[AnimationItem] = null
    
    def createQuadMesh(x: Float, y: Float, z: Float, width: Float, height: Float): Mesh = {
        var positions: Array[Float] = Array(
          x,        y,        z,
          x,        y-height, z,
          x+width,  y-height, z,
          x+width,  y,        z,
        )
        var indices: Array[Int] = Array(
          0, 1, 3, 3, 1, 2,
        )
        var colors: Array[Float] = Array(
          0.5f, 0.0f, 0.0f,
          0.0f, 0.5f, 0.0f,
          0.0f, 0.0f, 0.5f,
          0.0f, 0.5f, 0.5f,
        )
        return new Mesh(positions, colors, indices)
    }

    @Override
    def init(window: Window) = {
        renderer.init(window);
        val gap: Float = 0.1f
        quad1 = new AnimationItem(createQuadMesh(-0.5f - gap/2, 0.25f, -1.0f, 0.5f, 0.5f ))
        quad2 = new AnimationItem(createQuadMesh(0f + gap/2, 0.25f, -1.0f, 0.5f, 0.5f ))
    }

    @Override
    def input(window: Window) = {
        if (window.isKeyPressed(GLFW_KEY_ESCAPE)) {
          cleanup()
          System.exit(0);
        }

        if (window.isKeyPressed(GLFW_KEY_UP)) {
            direction = 1;
        } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
            direction = -1;
        } else {
            direction = 0;
        }
    }

    @Override
    def update(interval: Float) = {
        color += direction * 0.01f;
        if (color > 1) {
            color = 1.0f;
        } else if (color < 0) {
            color = 0.0f;
        }

        val amplitudes = audioSource.read()

        
        //TODO: move this part to AudioSource or new Class AudioAnalyzer
        var fft = new FFT(amplitudes.length)
        var im = (for (x <- Range(0, amplitudes.length))
          yield 0.0).toArray
        fft.fft(amplitudes, im)

        for (a <- amplitudes) {
          println(a)
        }

        val width = 1.0f/amplitudes.length
        items = new Array[AnimationItem](amplitudes.length)
        for (i <- Range(0, amplitudes.length)) {
          items(i) = new AnimationItem(
            createQuadMesh(width * i, 0.25f, -1.0f, width, amplitudes(i).asInstanceOf[Float])
          )
        }

        // var scale:Float = quad1.getScale()
        // quad1.setScale(scale + direction*0.1f)
        // println("scale quad1: ", quad1.getScale())
        // var rotation = quad1.getRotation()
        // quad1.setRotation(rotation.x + direction, rotation.y, rotation.z)

        // def abs(x: Double): Double = {
        //   if (x<0) x*(-1) else x
        // }

        // def abs_mean(xs: Array[Double]) = {
        //   val ys = xs.map(x => abs(x))
        //   ys.sum / ys.length
        // }

        // color = abs_mean(amplitudes).asInstanceOf[Float]
        // for (a <- amplitudes) {
        //   println(a)
        // }
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
