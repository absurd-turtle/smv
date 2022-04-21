package smv.animations.engine
import smv.animations.engine.Window
import smv.animations.engine.Timer

class AnimationEngine(var windowTitle: String, var width: Int, var height: Int, var vSync: Boolean, var animationLogic: IAnimationLogic) extends Runnable {

    val TARGET_FPS: Int = 75

    val TARGET_UPS: Int = 30

    private var window: Window = new Window(windowTitle, width, height, vSync)

    private var timer: Timer = new Timer()

    @Override
    def run() = {
        try {
            init();
            loop();
        } catch {
          case e: Exception => e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    protected def init() = {
        window.init();
        timer.init();
        animationLogic.init(window);
        println("animation engine initialized")
    }

    protected def loop() = {
        var elapsedTime: Float = 0
        var accumulator: Float = 0f
        var interval: Float = 1f / TARGET_UPS;

        var running: Boolean = true;
        while (running && !window.windowShouldClose()) {
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            input();

            while (accumulator >= interval) {
                update(interval);
                accumulator -= interval;
            }

            render();

            if ( !window.isvSync() ) {
                sync();
            }
        }
    }

    protected def cleanup() = {
        animationLogic.cleanup();                
    }
    
    private def sync() = {
        var loopSlot: Float = 1f / TARGET_FPS;
        var endTime: Double = timer.getLastLoopTime() + loopSlot;
        while (timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch {
              case e: InterruptedException => println(e.printStackTrace)
            }
        }
    }

    protected def input() = {
        animationLogic.input(window);
    }

    protected def update(interval: Float) = {
        animationLogic.update(interval);
    }

    protected def render() = {
        animationLogic.render(window);
        window.update();
    }
}
