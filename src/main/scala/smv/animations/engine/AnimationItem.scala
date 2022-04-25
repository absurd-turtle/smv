package smv.animations.engine

import org.joml.Vector3f

class AnimationItem(private final val mesh: Mesh) {

  private var position: Vector3f = new Vector3f(0,0,0)
  private var scale: Float = 1
  private var rotation: Vector3f = new Vector3f(0,0,0)
  
  def getPosition(): Vector3f =  {
    return position
  }

  def setPosition(x: Float, y: Float, z: Float) = {
    position.x = x
    position.y = y
    position.z = z
  }

  def getScale(): Float =  {
    return scale
  }

  def setScale(scale: Float) = {
    this.scale = scale
  }

  def getRotation(): Vector3f =  {
    return rotation
  }

  def setRotation(x: Float, y: Float, z: Float) = {
    rotation.x = x
    rotation.y = y
    rotation.z = z
  }

  def getMesh(): Mesh = {
    return mesh
  }
}
