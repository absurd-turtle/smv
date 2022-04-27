package smv.audio

import scala.math.abs
import com.meapsoft.FFT

object Analyzer {
  def applyFFT(samples: Array[Double]): Array[Double] = {
    //TODO: move this part to AudioSource or new Class AudioAnalyzer
    var out = samples.clone()
    var fft = new FFT(out.length)
    var im = (for (x <- 0 until out.length)
      yield 0.0).toArray
    fft.fft(out, im)
    return out.slice(0, out.length/2)
  }

  def getBands(samples: Array[Double]): Array[Double] = {
    var bins = applyFFT(samples)
    var bandValues = new Array[Double](8)
    for (i <- 1 until bins.length) {
      val index = i match {
        case x if x <= 3 => 0
        case x if x <= 6 => 1
        case x if x <= 12 => 2
        case x if x <= 26 => 3
        case x if x <= 55 => 4
        case x if x <= 118 => 5
        case x if x <= 251 => 6
        case _ => 7
      }
      bandValues(index) = bandValues(index) + abs(bins(i))
    }

    return bandValues
  }

  def section[T](arr: Array[T], sectionCount: Int): IndexedSeq[Array[T]] = {
    var sectionSize = arr.length / sectionCount
    var sectionSizes = for (i <- 0 until sectionCount) yield if(i < arr.length % sectionCount) sectionSize + 1 else sectionSize

    var index = 0
    var sections = sectionSizes.map(size => {
      index = index + size
      arr.slice(index-size, index)
    })
    return sections
  }
}
