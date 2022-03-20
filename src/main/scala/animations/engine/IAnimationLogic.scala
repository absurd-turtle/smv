package smv.animations.engine

trait IAnimationLogic {

    def init(): Unit
    
    def input(window: Window): Unit

    def update(interval: Float): Unit

    def render(window: Window): Unit

    def cleanup(): Unit

}
