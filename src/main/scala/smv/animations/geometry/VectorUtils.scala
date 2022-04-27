package smv.animations.geometry

import org.joml.Vector2d

object VectorUtils {
  def magnitudeFactor(v: Vector2d, magnitude: Double): Double = {
    var currentMagnitude = v.distance(0, 0)
    if (currentMagnitude != 0) magnitude / currentMagnitude else 0
  }

  // def setMag( v: Vector2d, magnitude: Double) = {
  //   v = v.mul(magnitudeFactor(v, magnitude))
  // }
  def constrain(amt: Double, min: Double, max: Double): Double = {
    if (amt < min) min else if (amt > max) max else amt
  }
}
