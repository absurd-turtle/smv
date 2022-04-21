package smv.animations.engine

import org.lwjgl.opengl.GL20._
import scala.collection.mutable.HashMap
import org.joml.Matrix4f
import org.lwjgl.system.MemoryStack
import java.nio.FloatBuffer

class ShaderProgram {

    private var vertexShaderId: Int = 0

    private var fragmentShaderId: Int = 0

    private var programId = glCreateProgram()

    private var uniforms: HashMap[String, Integer] = new HashMap[String, Integer]()

    if (this.programId == 0) {
        throw new Exception("Could not create Shader")
    }

    def createVertexShader(shaderCode: String) = {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER)
    }

    def createFragmentShader(shaderCode: String) = {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER)
    }

    def createShader(shaderCode: String, shaderType: Int ): Int = {
        var shaderId: Int = glCreateShader(shaderType)
        if (shaderId == 0) {
            throw new Exception("Error creating shader. Type: " + shaderType)
        }

        glShaderSource(shaderId, shaderCode)
        glCompileShader(shaderId)

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024))
        }

        glAttachShader(programId, shaderId);

        return shaderId
    }

    def createUniform(uniformName: String) = {
        var uniformLocation: Int = glGetUniformLocation(programId,
            uniformName);
        if (uniformLocation < 0) {
            throw new Exception("Could not find uniform:" +
                uniformName);
        }
        uniforms.put(uniformName, uniformLocation);
    }

    def setUniform(uniformName: String, value: Matrix4f) = {
      var stack: MemoryStack = MemoryStack.stackPush()
      try {
        var fb: FloatBuffer = stack.mallocFloat(16)
        value.get(fb)
        var key = uniforms.get(uniformName).orNull
        if(key == null) {
          throw new Exception("Could not find uniform: " + 
            uniformName + " in HashMap")
        }
        glUniformMatrix4fv(key, false, fb)
      }
      finally {
        if(stack != null){
          stack.close()
        }
      }
    }

    def link() = {
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

        if (vertexShaderId != 0) {
            glDetachShader(programId, vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            glDetachShader(programId, fragmentShaderId);
        }

        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

    }

    def bind() = {
        glUseProgram(programId);
    }

    def unbind() = {
        glUseProgram(0);
    }

    def cleanup() = {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }
}
