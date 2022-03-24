package smv.animations

import engine.Window
import engine.ShaderProgram
import engine.Mesh
import engine.Transformation
import engine.AnimationItem
import scala.io.Source
import java.nio.FloatBuffer
import java.io.File


import org.lwjgl.system.MemoryUtil._
import org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import org.lwjgl.opengl.GL11.GL_FLOAT;
import org.lwjgl.opengl.GL11.GL_TRIANGLES;
import org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import org.lwjgl.opengl.GL11.glClear;
import org.lwjgl.opengl.GL11.glDrawElements;
import org.lwjgl.opengl.GL11.glViewport;
import org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import org.lwjgl.opengl.GL15.glBindBuffer;
import org.lwjgl.opengl.GL15.glBufferData;
import org.lwjgl.opengl.GL15.glDeleteBuffers;
import org.lwjgl.opengl.GL15.glGenBuffers;
import org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import org.lwjgl.opengl.GL20.glVertexAttribPointer;
import org.lwjgl.opengl.GL30.glBindVertexArray;
import org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import org.lwjgl.opengl.GL30.glGenVertexArrays;
import org.lwjgl.system.MemoryUtil
import org.joml.Matrix4f
import scala.math

class Renderer(){
  private var vboId: Int = 0
  private var vaoId: Int = 0
  private var shaderProgram: ShaderProgram = null

  private final val FOV: Float = math.toRadians(60.0f).asInstanceOf[Float]
  private final val Z_NEAR: Float = 0.01f;
  private final val Z_FAR: Float = 1000;
  private var projectionMatrix: Matrix4f = null
  private var transformation: Transformation = new Transformation()

  def init(window: Window) = {
    var aspectRatio: Float = window.getWidth().toFloat / window.getHeight().toFloat
    projectionMatrix = new Matrix4f().perspective(FOV, aspectRatio, Z_NEAR, Z_FAR)

    shaderProgram = new ShaderProgram()

    shaderProgram.createVertexShader(Source.fromFile(
      // new File(getClass.getClassLoader.getResource("/vertex.vs").getPath)
      "/home/sam/Documents/uni/exchange_semester/courses/ps2/project/smv/src/main/resources/vertex.vs"
    ).mkString);
    shaderProgram.createFragmentShader(Source.fromFile(
      // new File(getClass.getClassLoader.getResource("/fragment.fs").getPath)
      "/home/sam/Documents/uni/exchange_semester/courses/ps2/project/smv/src/main/resources/fragment.fs"
    ).mkString);

    shaderProgram.link();

    shaderProgram.createUniform("projectionMatrix")
    shaderProgram.createUniform("worldMatrix")

    window.setClearColor(0.0f, 0.0f, 0.0f, 0.0f)
  }
  

  def clear() = {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  }

  def render(window: Window, items: Array[AnimationItem]) = {
      clear();

      if (window.isResized()) {
          glViewport(0, 0, window.getWidth(), window.getHeight());
          var aspectRatio: Float = window.getWidth().toFloat / window.getHeight().toFloat
          window.setResized(false);
      }

      shaderProgram.bind();

      // Update projection Matrix
      var projectionMatrix: Matrix4f = transformation.getProjectionMatrix(
        FOV, 
        window.getWidth().asInstanceOf[Float],
        window.getHeight().asInstanceOf[Float],
        Z_NEAR,
        Z_FAR
      )
      shaderProgram.setUniform("projectionMatrix", projectionMatrix)

      // Draw each animation item 
      for (item <- items) {
        var worldMatrix: Matrix4f = transformation.getWorldMatrix(
            item.getPosition(), item.getRotation(), item.getScale()
          )
        shaderProgram.setUniform("worldMatrix", worldMatrix)
        item.getMesh.render()
      }


      // Restore state
      glDisableVertexAttribArray(0)
      glBindVertexArray(0);

      shaderProgram.unbind();
  }

  def cleanup() = {
      if (shaderProgram != null) {
          shaderProgram.cleanup();
      }
  }
}
