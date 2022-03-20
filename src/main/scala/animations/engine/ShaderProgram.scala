package smv.animations.engine

import org.lwjgl.opengl.GL20._

class ShaderProgram {

    var vertexShaderId: Int = 0

    var fragmentShaderId: Int = 0

    var programId = glCreateProgram()

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
