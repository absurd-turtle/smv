package smv.animations.engine

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL11._;
import org.lwjgl.opengl.GL15._;
import org.lwjgl.opengl.GL20._;
import org.lwjgl.opengl.GL30._;
import org.lwjgl.system.MemoryUtil;


class Mesh(positions: Array[Float], indices: Array[Int]) {

    var vaoId: Int = 0

    var posVboId: Int = 0

    var idxVboId: Int = 0

    var vertexCount: Int = indices.length
    
    var verticesBuffer: FloatBuffer = null

    var indicesBuffer: IntBuffer = null

    try {
        verticesBuffer = MemoryUtil.memAllocFloat(positions.length);
        verticesBuffer.put(positions).flip();

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        posVboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, posVboId);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);            
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        idxVboId = glGenBuffers();
        indicesBuffer = MemoryUtil.memAllocInt(indices.length);
        indicesBuffer.put(indices).flip();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

        glBindVertexArray(0);         
    } finally {
        if (verticesBuffer != null) {
            MemoryUtil.memFree(verticesBuffer)
        }
        if (indicesBuffer != null) {
            MemoryUtil.memFree(indicesBuffer)
        }
    }

    def getVaoId(): Int = {
        return vaoId;
    }

    def getVertexCount(): Int = {
        return vertexCount;
    }

    def cleanUp() = {
        glDisableVertexAttribArray(0);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(posVboId);
        glDeleteBuffers(idxVboId);

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }
}
