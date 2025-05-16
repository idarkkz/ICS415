package com.devgenie.core;

import com.devgenie.core.utils.Consts;
import com.devgenie.test.Launcher;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

public class EngineManager {

    public static final long NANOSECONDS = 1000000000L;

    public static final float FRAMERATE = 1000;
    private static int fps;
    private static float frametime = 1.0f / FRAMERATE;
    public static float currentFrameTime = 0.0f;
    private boolean isRunning;

    private WindowManager window;
    private MouseInput mouseInput;
    private GLFWErrorCallback errorCallback;
    private ILogic gameLogic;

    private void init() throws Exception {
        GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        window = Launcher.getWindow();
        gameLogic = Launcher.getGame();
        mouseInput = new MouseInput();
        window.init();
        gameLogic.init();
        mouseInput.init();
    }

    public void start() throws Exception {
        init();
        if (isRunning)
            return;
        run();
    }

    public void run() {
        this.isRunning = true;
        int frames = 0;
        long frameCounter = 0;
        long lastTime = System.nanoTime();
        double unprocessedTime = 0;

        while (isRunning) {
            boolean render = false;
            long startTime = System.nanoTime();
            long passedTime = startTime - lastTime;
            lastTime = startTime;

            unprocessedTime += passedTime / (double) NANOSECONDS;
            frameCounter += passedTime;

            // ✅ Always call input()
            input();

            while (unprocessedTime > frametime) {
                unprocessedTime -= frametime;
                render = true;


                if (window.windowShouldClose()) {
                    stop();
                    break;
                }
            }

            if (render) {
                render();
                update(frametime);
                frames++;

                if (frameCounter >= NANOSECONDS) {
                    setFps(frames);
                    window.setTitle(Consts.TITLE + " FPS: " + getFps());
                    frames = 0;
                    frameCounter = 0;
                }
            }
        }

        //cleanup();
    }

    private void stop() {
        if (!isRunning)
            return;
        isRunning = false;
    }

    private void input() {
        gameLogic.input(mouseInput);
    }

    private void render() {
        gameLogic.render();
        window.update();
    }

    private void update(float interval) {
        gameLogic.update(interval, mouseInput);
    }

    private void cleanup() {
        window.cleanup();
        gameLogic.cleanup();
        errorCallback.free();
        GLFW.glfwTerminate();
    }

    private static int getFps() {
        return fps;
    }

    private static void setFps(int fps) {
        EngineManager.fps = fps;
    }
}
