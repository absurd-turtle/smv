package smv.animations

import org.lwjgl.glfw.GLFW.{GLFW_KEY_DOWN, GLFW_KEY_ESCAPE, GLFW_KEY_M, GLFW_KEY_UP}
import smv.AudioSource
import smv.animations.engine.{AnimationItem, IAnimationLogic, Window}
import smv.animations.geometry.Quad.createQuadMesh
import smv.color.{ColorTheme, ColorThemeReader}
import com.meapsoft.FFT

import scala.math.abs

class BandVisualizer(audioSource: AudioSource, colorTheme: ColorTheme, moveColors: Boolean = true) extends IAnimationLogic {

    var color = colorTheme.color(0)
    var colorOffset = 0
    var colorOffsetCounter = 0

    var changeColorTheme = 0
    var themeId = 0

    var ct: ColorTheme = colorTheme
    
    var renderer: Renderer = new Renderer()

    var items: Array[AnimationItem] = null

    var movingColors: Boolean = moveColors
    
    @Override
    def init(window: Window) = {
        renderer.init(window)
    }

    @Override
    def input(window: Window) = {
        if (window.isKeyPressed(GLFW_KEY_ESCAPE)) {
          cleanup()
          System.exit(0)
        }
        else if (window.isKeyPressed(GLFW_KEY_UP)) {
            changeColorTheme = 1
        } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
            changeColorTheme = -1
        } else {
            changeColorTheme = 0
        }
        if (window.isKeyPressed(GLFW_KEY_M)){
            this.movingColors = !movingColors
        }

    }

    def getAmplitudes() = synchronized {
      audioSource.getAudioBuffer().clone()
    }

    def applyFFT(samples: Array[Double]): Array[Double] = {
      //TODO: move this part to AudioSource or new Class AudioAnalyzer
      var out = samples.clone()
      var fft = new FFT(out.length)
      var im = (for (x <- Range(0, out.length))
        yield 0.0).toArray
      fft.fft(out, im)
      return out.slice(0, out.length/2)
    }

    def getBands(samples: Array[Double]): Array[Double] = {
      var bins = applyFFT(samples)
      var bandValues = new Array[Double](8)
      for (i <- 1 until bins.length) {
        val index = i match {
          case x if x <= 3 => 0
          case x if x <= 6 => 1
          case x if x <= 12 => 2
          case x if x <= 26 => 3
          case x if x <= 55 => 4
          case x if x <= 118 => 5
          case x if x <= 251 => 6
          case _ => 7
        }
        bandValues(index) = bandValues(index) + abs(bins(i))
      }

      return bandValues
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

        val amplitudes = getBands(getAmplitudes())

        //Draw quads
        val width = 2.0f/amplitudes.length
        

        //TODO: increase effiency by reusing the items instead of creating new ones
        if(items != null && items.length > 0){
          for (item <- items) {
            item.getMesh().cleanUp()
          }
        }

        items = new Array[AnimationItem](amplitudes.length)
        var colorCount = 0
        var colorIndex = 0

        for (i <- 0 until amplitudes.length) {
          items(i) = new AnimationItem(
            createQuadMesh(width * i - 1.0f, -1.0f, -2.0f, width, -0.1f * amplitudes(i).asInstanceOf[Float],
              false, ct.color((colorIndex + colorOffset) % (ct.color.length - 1) + 1 ))
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
        window.setClearColor(color.r, color.g, color.b, 1.0f)
        renderer.render(window, items)
    }

    @Override
    def cleanup() = {
        renderer.cleanup()
    }

}
