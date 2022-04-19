package smv.animations.geometry

import smv.animations.engine.Mesh
import smv.utils.Color

object Quad {
  def createQuadMesh(x: Float, y: Float, z: Float, width: Float, height: Float, center: Boolean = true, color: Color = null): Mesh = {
      var positions: Array[Float] = if (center) 
      Array(
        x-width/2, y+height/2, z,
        x-width/2, y-height/2, z,
        x+width/2, y-height/2, z,
        x+width/2, y+height/2, z,
      )
      else 
      Array(
        x,        y,        z,
        x,        y-height, z,
        x+width,  y-height, z,
        x+width,  y,        z,
      )
      var indices: Array[Int] = Array(
        0, 1, 3, 3, 1, 2,
      )
      var colors: Array[Float] = 
      if ( color != null)
      Array(
        color.r, color.g, color.b,
        color.r, color.g, color.b,
        color.r, color.g, color.b,
        color.r, color.g, color.b
      )
      else
      Array(
        0.5f, 0.0f, 0.0f,
        0.0f, 0.5f, 0.0f,
        0.0f, 0.0f, 0.5f,
        0.0f, 0.5f, 0.5f,
      )
      return new Mesh(positions, colors, indices)
  }
}
