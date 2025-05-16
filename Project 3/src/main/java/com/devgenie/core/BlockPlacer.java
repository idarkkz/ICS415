package com.devgenie.core;

import com.devgenie.core.entity.Camera;
import com.devgenie.core.entity.Entity;
import com.devgenie.core.entity.Material;
import com.devgenie.core.entity.Model;
import com.devgenie.core.entity.SceneManager;
import com.devgenie.core.entity.Texture;
import org.joml.Vector3f;

public class BlockPlacer {

    private final SceneManager sceneManager;
    private final ObjectLoader loader;
    private final Model defaultModel;

    private boolean leftClickHandled = false;
    private boolean rightClickHandled = false;

    public BlockPlacer(SceneManager sceneManager, ObjectLoader loader) throws Exception {
        this.sceneManager = sceneManager;
        this.loader = loader;

        Texture texture = new Texture(loader.loadTexture("textures/grass_block.png"));
        defaultModel = loader.createCubeWithAtlas(texture);
        defaultModel.setMaterial(new Material(texture, 1.0f));
    }

    public void update(MouseInput mouseInput, Camera camera) {
        mouseInput.input(); // Always read mouse

        if (mouseInput.isLeftButtonPress()) {
            if (!leftClickHandled) {
                Vector3f blockPos = raycast(camera);
                if (blockPos != null) {
                    removeBlock(blockPos);
                }
                leftClickHandled = true;
            }
        } else {
            leftClickHandled = false;
        }

        if (mouseInput.isRightButtonPress()) {
            if (!rightClickHandled) {
                Vector3f blockPos = raycast(camera);
                if (blockPos != null) {
                    Vector3f placePos = new Vector3f(blockPos).add(0, 1, 0); // Place above
                    addBlock(placePos);
                }
                rightClickHandled = true;
            }
        } else {
            rightClickHandled = false;
        }
    }

    private Vector3f raycast(Camera camera) {
        Vector3f origin = new Vector3f(camera.getPosition());
        Vector3f direction = new Vector3f(
                (float) Math.sin(Math.toRadians(camera.getRotation().y)) * (float) Math.cos(Math.toRadians(camera.getRotation().x)),
                (float) -Math.sin(Math.toRadians(camera.getRotation().x)),
                (float) -(Math.cos(Math.toRadians(camera.getRotation().y)) * (float) Math.cos(Math.toRadians(camera.getRotation().x)))
        );
        direction.normalize();

        float maxDistance = 6.0f;
        float step = 0.1f;
        Vector3f currentPos = new Vector3f(origin);

        for (float i = 0; i < maxDistance; i += step) {
            currentPos.add(direction.x * step, direction.y * step, direction.z * step);

            Vector3f blockPos = new Vector3f(
                    (float) Math.floor(currentPos.x),
                    (float) Math.floor(currentPos.y),
                    (float) Math.floor(currentPos.z)
            );

            for (Entity entity : sceneManager.getEntities()) {
                if (Math.floor(entity.getPos().x) == blockPos.x &&
                        Math.floor(entity.getPos().y) == blockPos.y &&
                        Math.floor(entity.getPos().z) == blockPos.z) {
                    return blockPos;
                }
            }
        }
        return null;
    }

    private void removeBlock(Vector3f blockPos) {
        sceneManager.getEntities().removeIf(entity ->
                Math.floor(entity.getPos().x) == blockPos.x &&
                        Math.floor(entity.getPos().y) == blockPos.y &&
                        Math.floor(entity.getPos().z) == blockPos.z
        );
    }

    private void addBlock(Vector3f placePos) {
        Entity newBlock = new Entity(defaultModel, placePos, new Vector3f(0, 0, 0), 1.0f);
        sceneManager.addEntity(newBlock);
    }
}
