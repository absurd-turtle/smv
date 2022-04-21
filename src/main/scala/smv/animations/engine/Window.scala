package smv.animations.engine

import org.lwjgl.glfw.GLFW._
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWVidMode
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.GL_FALSE
import org.lwjgl.opengl.GL11.GL_TRUE
import org.lwjgl.opengl.GL11.glClearColor
import org.lwjgl.system.MemoryUtil.NULL

class Window (var title: String, var width: Int, var height: Int, var vSync: Boolean ) {

    private var windowHandle: Long = 0
    
    private var resized: Boolean = false


    def init() = {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        // Create the window
        windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (windowHandle == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Setup resize callback
        glfwSetFramebufferSizeCallback(windowHandle, (window, width, height) => {
            this.width = width;
            this.height = height;
            this.setResized(true);
        })

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) => {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            }
        })

        // Get the resolution of the primary monitor
        var vidmode: GLFWVidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        glfwSetWindowPos(
                windowHandle,
                (vidmode.width() - width) / 2,
                (vidmode.height() - height) / 2
        );

        // Make the OpenGL context current
        glfwMakeContextCurrent(windowHandle);

        if (isvSync()) {
            // Enable v-sync
            glfwSwapInterval(1);
        }

        // Make the window visible
        glfwShowWindow(windowHandle);
        
        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }
    
    def setClearColor(r: Float, g: Float, b: Float, alpha: Float) = {
        glClearColor(r, g, b, alpha);
    }
    
    def isKeyPressed(keyCode: Int) : Boolean = {
        return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS;
    }
    
    def windowShouldClose() : Boolean = {
        return glfwWindowShouldClose(windowHandle);
    }
    
    def getTitle() : String = {
        return title;
    }

    def getWidth() : Int = {
        return width;
    }

    def getHeight() : Int = {
        return height;
    }
    
    def isResized() : Boolean = {
        return resized;
    }

    def setResized(resized: Boolean ) = {
        this.resized = resized;
    }

    def isvSync() : Boolean = {
        return vSync;
    }

    def setvSync(vSync: Boolean ) = {
        this.vSync = vSync;
    }

    def update() = {
        glfwSwapBuffers(windowHandle);
        glfwPollEvents();
    }
}
