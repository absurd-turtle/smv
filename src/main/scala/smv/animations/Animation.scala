package smv.animations

import scala.io.Source
import smv.AudioSource
import smv.animations.engine.AnimationEngine
import smv.animations.DummyAnimation
import smv.animations.mutualAttraction.MutualAttraction
import engine.AnimationEngine
import engine.IAnimationLogic
import org.lwjgl.glfw._
import org.lwjgl.glfw.GLFW._
import org.lwjgl.opengl._
import org.lwjgl.system.MemoryUtil._
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._
import java.nio.FloatBuffer
import smv.utils.ColorTheme

class Animation(audioSource: AudioSource, colorTheme: ColorTheme, visualizationName: String) {
    def start() = {
        try {
            var vSync: Boolean = true;
            // var animationLogic: IAnimationLogic  = new MutualAttraction(audioSource);
            // var animationLogic: IAnimationLogic  = new SoundSpectrumVisualizer(audioSource, colorTheme);
            // var animationLogic: IAnimationLogic  = new RotatingCubeAnimation(audioSource);


            var animationLogic = visualizationName match {
              case x if (x == "SoundSpectrumVisualizer" || x == "0") => new SoundSpectrumVisualizer(audioSource, colorTheme, true)
              case x if (x == "MutualAttraction"        || x == "1") => new MutualAttraction(audioSource, colorTheme)
              // case x if (x == "RotatingCubeAnimation"   || x == "2") => new RotatingCubeAnimation(audioSource, colorTheme)
              case _ => new SoundSpectrumVisualizer(audioSource, colorTheme)
            }
            var animationEngine: AnimationEngine  = new AnimationEngine("anim", 600, 480, vSync, animationLogic);
            animationEngine.run();
        } catch {
          case e: Exception => { 
            e.printStackTrace();
            System.exit(-1);
          }
        }
    }
}


