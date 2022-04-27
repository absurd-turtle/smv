package smv.animations

import scala.io.Source
import smv.AudioSource
import smv.animations.engine.AnimationEngine
import smv.animations.DummyAnimation
import smv.animations.mutualAttraction.MutualAttraction
import engine.IAnimationLogic
import org.lwjgl.glfw._
import org.lwjgl.glfw.GLFW._
import org.lwjgl.opengl._
import org.lwjgl.system.MemoryUtil._
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._
import java.nio.FloatBuffer
import smv.color.ColorTheme


/**
  * Wrapper class for different animations that can be played
  *
  * @param audioSource AudioSource to read from (mic/linein/file)
  * @param colorTheme 
  * @param visualizationName Name of the visualization to play
  */
class Animation(audioSource: AudioSource, colorTheme: ColorTheme, visualizationName: String) {
    def start() = {
        try {
            var vSync: Boolean = true

            // fetch animation
            var animationLogic = matchAnimationLogic(visualizationName, audioSource, colorTheme)

            initAnimationEngine(vSync, animationLogic).run()

        } catch {
          case e: Exception => { 
            e.printStackTrace()
            System.exit(-1)
          }
        }
    }

    /**
    * matches the name of an animation and returns the initialized Animation
    *
    * @param visualizationName
    * @param audioSource
    * @param colorTheme
    * @return animation object
    */
    def matchAnimationLogic(visualizationName: String, audioSource: AudioSource, colorTheme: ColorTheme): IAnimationLogic = {
      visualizationName match {
        case x if (x == "SoundSpectrumVisualizer" || x == "0") => new SoundSpectrumVisualizer(audioSource, colorTheme, false)
        case x if (x == "MutualAttraction"        || x == "1") => new MutualAttraction(audioSource, colorTheme)
        case x if (x == "BandVisualizer"   || x == "2") => new BandVisualizer(audioSource, colorTheme)
        case _ => new BandVisualizer(audioSource, colorTheme)
      }
    }

    /**
    * initializes animation engine with given animation
    *
    * @param vSync
    * @param animationLogic
    * @return
    */
    def initAnimationEngine (vSync: Boolean, animationLogic: IAnimationLogic): AnimationEngine = {
      new AnimationEngine("anim", 600, 480, vSync, animationLogic)
    }
}


