package smv.animations

import smv.AudioSource
import smv.animations.engine.AnimationItem
import smv.animations.engine.Mesh

import org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

import engine.IAnimationLogic;
import engine.Window;

import smv.animations.geometry.Quad.createQuadMesh

import com.meapsoft.FFT
import smv.utils.ColorTheme
import smv.utils.ColorThemeReader

class SoundSpectrumVisualizer(audioSource: AudioSource, colorTheme: ColorTheme, movingColors: Boolean = true) extends IAnimationLogic {

    var color = colorTheme.color(0);
    var colorOffset = 0
    var colorOffsetCounter = 0

    var changeColorTheme = 0
    var themeId = 0

    var ct: ColorTheme = colorTheme
    
    var renderer: Renderer = new Renderer()

    var items: Array[AnimationItem] = null
    
    @Override
    def init(window: Window) = {
        renderer.init(window);
    }

    @Override
    def input(window: Window) = {
        if (window.isKeyPressed(GLFW_KEY_ESCAPE)) {
          cleanup()
          System.exit(0);
        }
        if (window.isKeyPressed(GLFW_KEY_UP)) {
            changeColorTheme = 1;
        } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
            changeColorTheme = -1;
        } else {
            changeColorTheme = 0;
        }
    }

    def getAmplitudes() = synchronized {
      audioSource.getAudioBuffer().clone()
    }

    def performFFT(amplitudes: Array[Double]) = {
        //TODO: move this part to AudioSource or new Class AudioAnalyzer
        var fft = new FFT(amplitudes.length)
        var im = (for (x <- Range(0, amplitudes.length))
          yield 0.0).toArray
        fft.fft(amplitudes, im)
    }

    @Override
    def update(interval: Float) = {
        if ( movingColors ){
          colorOffsetCounter = colorOffsetCounter + 1
          if(colorOffsetCounter == 10){
            colorOffsetCounter = 0
            colorOffset = colorOffset + 1
          }
        }

        if(changeColorTheme != 0){
          themeId = themeId + 1
          ct = ColorThemeReader.getKnownTheme(themeId % ColorThemeReader.getKnownThemeCount())
          color = ct.color(0)
        }

        val amplitudes = getAmplitudes()
        performFFT(amplitudes)


        //Draw quads
        val width = 2.0f/amplitudes.length
        items = new Array[AnimationItem](amplitudes.length)
        var colorCount = 0
        var colorIndex = 0
        for (i <- Range(0, amplitudes.length)) {
          items(i) = new AnimationItem(
            createQuadMesh(width * i - 1.0f, 0.0f, -1.0f, width, amplitudes(i).asInstanceOf[Float],
              true, ct.color((colorIndex + colorOffset) % (ct.color.length - 1) + 1 ))
          )

          colorCount = colorCount + 1
          if(colorCount > amplitudes.length/ct.color.length){
            colorCount = 0
            colorIndex = colorIndex + 1
          }
        }
    }

    @Override
    def render(window: Window) = {
        window.setClearColor(color.r, color.g, color.b, 1.0f);
        renderer.render(window, items);
    }

    @Override
    def cleanup() = {
        renderer.cleanup();
    }

}
