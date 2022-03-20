package smv.animations

import scala.io.Source
import smv.AudioSource
import smv.animations.engine.AnimationEngine
import smv.animations.DummyAnimation
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


class Animation(audioSource: AudioSource) {
    def start() = {
        try {
            var vSync: Boolean = true;
            var animationLogic: IAnimationLogic  = new DummyAnimation(audioSource);
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

