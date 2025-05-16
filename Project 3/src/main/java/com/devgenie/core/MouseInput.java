package com.devgenie.core;

import com.devgenie.test.Launcher;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class MouseInput {
    private final Vector2d previousPos, currentPos;
    private final Vector2f displVec;

    private boolean inWindow = false, leftButtonPress = false, rightButtonPress = false;

    public MouseInput() {
        previousPos = new Vector2d(-1,-1);
        currentPos = new Vector2d(0,0);
        displVec = new Vector2f();
    }
    public void init(){
        GLFW.glfwSetCursorPosCallback(Launcher.getWindow().getWindow(), (window, xpos, ypos) -> {
            currentPos.x = xpos;
            currentPos.y = ypos;
        });
        GLFW.glfwSetCursorEnterCallback(Launcher.getWindow().getWindow(),(window, entered) -> {
            inWindow = entered;
        });
        GLFW.glfwSetMouseButtonCallback(Launcher.getWindow().getWindow(),(window, button, action, mods) -> {
            leftButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS;
            rightButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_PRESS;

        });

        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;
    }

    public void input() {
        displVec.x = 0;
        displVec.y = 0;

        double x = currentPos.x - previousPos.x;
        double y = currentPos.y - previousPos.y;

        boolean rotateX = x != 0;
        boolean rotateY = y != 0;

        if (rotateX)
            displVec.y = (float) x;
        if (rotateY)
            displVec.x = (float) y;

        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;

    }


    public boolean isRightButtonPress() {
        return rightButtonPress;
    }

    public boolean isLeftButtonPress() {
        return leftButtonPress;
    }

    public Vector2f getDisplVec() {
        return displVec;
    }
}
