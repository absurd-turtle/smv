package smv.animations.engine

import org.joml.Matrix4f
import org.joml.Vector3f

class Transformation {
  private val projectionMatrix: Matrix4f  = new Matrix4f()
  private val worldMatrix: Matrix4f  = new Matrix4f()

  final def getProjectionMatrix(fov: Float, width: Float, height: Float, zNear: Float, zFar: Float): Matrix4f = {
    val aspectRatio: Float = width / height
    projectionMatrix.identity()
    projectionMatrix.perspective(fov, aspectRatio, zNear, zFar)
    return projectionMatrix
  }

  def getWorldMatrix(offset: Vector3f, rotation: Vector3f, scale: Float): Matrix4f = {
    worldMatrix.identity().translate(offset)
      .rotateX(Math.toRadians(rotation.x).asInstanceOf[Float])
      .rotateY(Math.toRadians(rotation.y).asInstanceOf[Float])
      .rotateZ(Math.toRadians(rotation.z).asInstanceOf[Float])
      .scale(scale)

    return worldMatrix
  }

}
