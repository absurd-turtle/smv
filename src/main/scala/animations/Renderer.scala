package smv.animations

import engine.Window;
import engine.ShaderProgram;
import engine.Mesh;
import scala.io.Source
import java.nio.FloatBuffer;
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

class Renderer(){
  private var vboId: Int = 0
  private var vaoId: Int = 0
  private var shaderProgram: ShaderProgram = null

  def init() = {
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
  }
  

  def clear() = {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  }

  def render(window: Window, mesh: Mesh) = {
      clear();

      if (window.isResized()) {
          glViewport(0, 0, window.getWidth(), window.getHeight());
          window.setResized(false);
      }

      shaderProgram.bind();

      // Draw the mesh
      glBindVertexArray(mesh.getVaoId());
      glEnableVertexAttribArray(0);
      glEnableVertexAttribArray(1);
      glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);

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
