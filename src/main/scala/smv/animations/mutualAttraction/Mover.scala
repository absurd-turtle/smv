package smv.animations.mutualAttraction

import org.joml.Vector2d
import scala.math
import smv.animations.engine.Mesh
import smv.animations.geometry.VectorUtils._

class Mover(x: Double, y: Double, vx: Double, vy: Double, m: Double) {
  var pos = new Vector2d(x, y)
  var vel = new Vector2d(vx, vy)
  var acc = new Vector2d(0, 0)
  var mass = m
  val G = 1

  var r = math.sqrt(mass) * 2

  def applyForce(force: Vector2d) = {
    var f = force.div(mass)
    acc.add(f)
  }

  def constrain(amt: Double, min: Double, max: Double): Double = {
    if (amt < min) min else if (amt > max) max else amt
  }

  def attract(mover: Mover) = {
    var force = pos.sub(mover.pos)
    var distanceSq = constrain(force.lengthSquared(), 100, 1000)
    var strength = (G * (mass * mover.mass)) / distanceSq
    force = force.mul(magnitudeFactor(force, strength))
    mover.applyForce(force)
  }

  def update() = {
    this.vel.add(this.acc)
    this.pos.add(this.vel)

    // TODO: limit velocity 
    // this.vel.limit(15)

    this.acc.set(0, 0)
  }
}
