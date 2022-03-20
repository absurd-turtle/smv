package smv.animations

import smv.AudioSource

import org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

import engine.IAnimationLogic;
import engine.Window;

class DummyAnimation(audioSource: AudioSource) extends IAnimationLogic {

    var direction = 0;

    var color = 0.0f;
    
    var renderer: Renderer = new Renderer()
    
    @Override
    def init() = {
        renderer.init();
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

        def abs(x: Double): Double = {
          if (x<0) x*(-1) else x
        }

        def abs_mean(xs: Array[Double]) = {
          val ys = xs.map(x => abs(x))
          ys.sum / ys.length
        }

        color = abs_mean(amplitudes).asInstanceOf[Float]
        // for (a <- amplitudes) {
        //   println(a)
        // }
    }

    @Override
    def render(window: Window) = {
        window.setClearColor(color, color, color, 0.0f);
        renderer.render(window);
    }

    @Override
    def cleanup() = {
        renderer.cleanup();
    }

}
