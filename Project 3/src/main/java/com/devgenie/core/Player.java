package com.devgenie.core;

import com.devgenie.core.entity.Camera;
import com.devgenie.core.entity.Entity;
import com.devgenie.core.entity.SceneManager;
import com.devgenie.core.utils.Consts;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class Player {

    private final Camera camera;
    private final SceneManager sceneManager;
    private final WindowManager window;

    private final Vector3f playerVelocity = new Vector3f(0, 0, 0);
    private final Vector3f movement = new Vector3f(0, 0, 0);
    private boolean isJumping = false;

    public Player(Camera camera, SceneManager sceneManager, WindowManager window) {
        this.camera = camera;
        this.sceneManager = sceneManager;
        this.window = window;
    }

    public void update(float interval) {
        handleInput();
        applyGravity(interval);
        movePlayer(interval);
    }

    private void handleInput() {
        movement.zero();

        if (window.isKeyPressed(GLFW.GLFW_KEY_W))
            movement.z = -1;
        if (window.isKeyPressed(GLFW.GLFW_KEY_S))
            movement.z = 1;
        if (window.isKeyPressed(GLFW.GLFW_KEY_A))
            movement.x = -1;
        if (window.isKeyPressed(GLFW.GLFW_KEY_D))
            movement.x = 1;

        if (movement.x != 0 && movement.z != 0)
            movement.normalize();

        if (window.isKeyPressed(GLFW.GLFW_KEY_SPACE))
            jump();
    }

    private void applyGravity(float interval) {
        playerVelocity.y += Consts.GRAVITY * interval;
    }

    private void movePlayer(float interval) {
        Vector3f oldPos = new Vector3f(camera.getPosition());

        // Move horizontally
        camera.movePosition(
                movement.x * Consts.PLAYER_MOVE_SPEED * interval,
                0,
                movement.z * Consts.PLAYER_MOVE_SPEED * interval
        );
        if (checkCollision(camera.getPosition())) {
            camera.setPosition(oldPos.x, camera.getPosition().y, oldPos.z);
        }

        // Move vertically
        camera.getPosition().y += playerVelocity.y * interval;
        if (checkCollision(camera.getPosition())) {
            camera.getPosition().y = oldPos.y;
            playerVelocity.y = 0;
            isJumping = false;
        }
    }

    private boolean checkCollision(Vector3f newPos) {
        float playerFeet = newPos.y - Consts.PLAYER_HEIGHT;

        for (Entity block : sceneManager.getEntities()) {
            Vector3f blockPos = block.getPos();

            boolean collideX = Math.abs(blockPos.x - newPos.x) < 0.5f;
            boolean collideY = Math.abs(blockPos.y - playerFeet) < 1.0f; // Make this bigger so it detects sideways collision
            boolean collideZ = Math.abs(blockPos.z - newPos.z) < 0.5f;

            if (collideX && collideZ && collideY) {
                return true;
            }
        }
        return false;
    }


    private void jump() {
        if (!isJumping) {
            playerVelocity.y = Consts.JUMP_VELOCITY;
            isJumping = true;
        }
    }
}
