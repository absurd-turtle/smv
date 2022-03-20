package smv.animations

import engine.Window;
import engine.ShaderProgram;
import scala.io.Source
import java.nio.FloatBuffer;
import java.io.File

import org.lwjgl.system.MemoryUtil._
import org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import org.lwjgl.opengl.GL11.GL_FLOAT;
import org.lwjgl.opengl.GL11.GL_TRIANGLES;
import org.lwjgl.opengl.GL11.glClear;
import org.lwjgl.opengl.GL11.glDrawArrays;
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

    var triangleVertices: Array[Float] = Array(
         0.0f,  0.5f, 0.0f,
        -0.5f, -0.5f, 0.0f,
         0.5f, -0.5f, 0.0f
    )

    var verticesBuffer: FloatBuffer = memAllocFloat(triangleVertices.length);
    verticesBuffer.put(triangleVertices).flip();

    var vaoId = glGenVertexArrays();
    glBindVertexArray(vaoId);

    var vboId = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vboId);
    glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);

    // Enable location 0
    glEnableVertexAttribArray(0);
    // Define structure of the data
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

    // Unbind the VBO
    glBindBuffer(GL_ARRAY_BUFFER, 0);

    // Unbind the VAO
    glBindVertexArray(0);
    memFree(verticesBuffer);
  }
  

  def clear() = {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  }

  def render(window: Window) = {
      clear();

      if (window.isResized()) {
          glViewport(0, 0, window.getWidth(), window.getHeight());
          window.setResized(false);
      }

      shaderProgram.bind();

      // Bind to the VAO
      glBindVertexArray(vaoId);

      // Draw the vertices
      glDrawArrays(GL_TRIANGLES, 0, 3);

      // Restore state
      glBindVertexArray(0);

      shaderProgram.unbind();
  }

  def cleanup() = {
      if (shaderProgram != null) {
          shaderProgram.cleanup();
      }

      glDisableVertexAttribArray(0);

      // Delete the VBO
      glBindBuffer(GL_ARRAY_BUFFER, 0);
      glDeleteBuffers(vboId);

      // Delete the VAO
      glBindVertexArray(0);
      glDeleteVertexArrays(vaoId);
  }
}
