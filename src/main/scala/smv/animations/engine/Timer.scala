package smv.animations.engine

class Timer {

    var lastLoopTime: Double = 0
    
    def init() = {
        lastLoopTime = getTime();
    }

    def getTime(): Double = {
        return System.nanoTime() / 1_000_000_000.0;
    }

    def getElapsedTime(): Float = {
        var time: Double = getTime()
        var elapsedTime: Float = (time - lastLoopTime).asInstanceOf[Float]
        lastLoopTime = time
        return elapsedTime
    }

    def getLastLoopTime(): Double = {
        return lastLoopTime;
    }
}
