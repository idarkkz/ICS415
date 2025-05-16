package com.devgenie.test;

import com.devgenie.core.*;
import com.devgenie.core.entity.*;
import com.devgenie.core.entity.terrain.Terrain;
import com.devgenie.core.lighting.DirectionalLight;
import com.devgenie.core.lighting.PointLight;
import com.devgenie.core.lighting.SpotLight;
import com.devgenie.core.rendering.RenderManager;
import com.devgenie.core.utils.Consts;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class TestGame implements ILogic {

    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final WindowManager window;
    private SceneManager sceneManager;
    private Camera camera;
    Vector3f cameraInc;
    private BlockPlacer blockPlacer;
    private Player player;

    private boolean leftClickHandled = false;
    private boolean rightClickHandled = false;

    public TestGame() {
        renderer = new RenderManager();
        window = Launcher.getWindow();
        loader = new ObjectLoader();
        camera = new Camera();
        cameraInc = new Vector3f(0,0,0);
        sceneManager = new SceneManager(-90);
    }

    @Override
    public void init() throws Exception {
        renderer.init();
        blockPlacer = new BlockPlacer(sceneManager, loader);
        camera.setPosition(16f, 5f, 16f); // center of grid, 5 blocks above ground
        camera.setRotation(0f, 0f, 0f);   // looking straight forward
        player = new Player(camera, sceneManager, window);

        float reflectance = 1.0f;

        Texture texture = new Texture(loader.loadTexture("textures/grass_block.png"));
        Model mesh = loader.createCubeWithAtlas(texture);
        Material material = new Material(texture, reflectance);
        mesh.setMaterial(material);



        // Grid creation
        for (int x = 0; x < 32; x++) {
            for (int z = 0; z < 32; z++) {
                Vector3f position = new Vector3f(x, 0, z); // y=0 for flat world
                Entity block = new Entity(mesh, position, new Vector3f(0, 0, 0), 1.0f);
                sceneManager.addEntity(block);
            }
        }



        // Ambient Light
        sceneManager.setAmbientLight(new Vector3f(0.3f,0.3f,0.3f));

        // Point Light

        Vector3f lightPosition = new Vector3f(0,0,0);
        float lightIntensity = 1.0f;
        PointLight pointLight = new PointLight(new Vector3f(1,1,1),lightPosition,lightIntensity);
        PointLight.Attenuation attenuation = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
        pointLight.setAttenuation(attenuation);
        sceneManager.setPointLights(new PointLight[]{pointLight});

        // Spot Light
        lightPosition = new Vector3f(16,5,16f);
        pointLight = new PointLight(new Vector3f(1,1,1),lightPosition,lightIntensity);
        attenuation = new PointLight.Attenuation(0.0f, 0.0f, 0.02f);
        pointLight.setAttenuation(attenuation);
        Vector3f coneDir = new Vector3f(0,0,-1);
        float cutoff = (float) Math.cos(Math.toRadians(140));
        SpotLight spotLight = new SpotLight(pointLight, coneDir, cutoff);
        sceneManager.setSpotLights(new SpotLight[]{spotLight, new SpotLight(spotLight)});


        // Directional Light
        lightPosition = new Vector3f(-1,0,0f);
        sceneManager.setDirectionalLight(new DirectionalLight(new Vector3f(1,1,1), lightPosition, lightIntensity));
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        camera.movePosition(cameraInc.x * Consts.CAMERA_STEP, cameraInc.y * Consts.CAMERA_STEP,
                cameraInc.z * Consts.CAMERA_STEP);

        //entity.incRotation(0.0f,0.25f,0.0f);

        sceneManager.incSpotAngle(sceneManager.getSpotAngle() * 0.05f);
        if(sceneManager.getSpotAngle() > 2)
            sceneManager.setSpotInc(-1);
        else if(sceneManager.getSpotAngle() <= -2)
            sceneManager.setSpotInc(1);

        double spotAngleRad = Math.toRadians(sceneManager.getSpotAngle());
        Vector3f coneDir = sceneManager.getSpotLights()[0].getConeDirection();
        coneDir.y = (float) Math.sin(spotAngleRad);

        float factor = 1 - (Math.abs(sceneManager.getLightAngle()) - 80) / 10.0f;
        sceneManager.getDirectionalLight().setIntensity(factor);
        sceneManager.getDirectionalLight().getColor().y = Math.max(factor, 0.9f);
        sceneManager.getDirectionalLight().getColor().z = Math.max(factor, 0.5f);

        for(Entity entity : sceneManager.getEntities()) {
            renderer.processEntity(entity);
        }

        for(Terrain terrain : sceneManager.getTerrains()) {
            renderer.processTerrain(terrain);
        }
        player.update(interval);

    }


    @Override
    public void render() {
        renderer.render(camera, sceneManager);
    }


    @Override
    public void input(MouseInput mouseInput) {
        mouseInput.input();
        // Free look
        Vector2f rotVec = mouseInput.getDisplVec();
        camera.moveRotation(rotVec.x * Consts.MOUSE_SENSITIVITY, rotVec.y * Consts.MOUSE_SENSITIVITY, 0);

        // Clamp pitch (X rotation)
        Vector3f rotation = camera.getRotation();
        rotation.x = Math.max(-90.0f, Math.min(90.0f, rotation.x));


        // Build/destroy blocks
        blockPlacer.update(mouseInput, camera);

//        if(window.isKeyPressed(GLFW.GLFW_KEY_O))
//            pointLight.getPosition().x += 0.1f;
//
//        if(window.isKeyPressed(GLFW.GLFW_KEY_P))
//            pointLight.getPosition().x -= 0.1f;

        float lightPos = sceneManager.getSpotLights()[0].getPointLight().getPosition().z;
        if(window.isKeyPressed(GLFW.GLFW_KEY_N))
            sceneManager.getSpotLights()[0].getPointLight().getPosition().z = lightPos + 0.1f;

        if(window.isKeyPressed(GLFW.GLFW_KEY_M))
            sceneManager.getSpotLights()[0].getPointLight().getPosition().z = lightPos - 0.1f;
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
    }
}
