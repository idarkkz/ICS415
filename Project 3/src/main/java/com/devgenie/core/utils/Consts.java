package com.devgenie.core.utils;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class Consts {
    public static final String TITLE = "MINECRAFT-LIKE ENGINE";

    public static final float FOV = (float) Math.toRadians(60);
    public static final float Z_NEAR = 0.1f;
    public static final float Z_FAR = 1000.0f;

    public static final float MOUSE_SENSITIVITY = 0.2f;
    public static final float CAMERA_STEP = 0.05f;

    public static final float SPECULAR_POWER = 10f;

    public static final int MAX_SPOT_LIGHTS = 5;
    public static final int MAX_POINT_LIGHTS = 5;


    public static final Vector4f DEFAULT_COLOR = new Vector4f(1.0f,1.0f,1.0f,1.0f);
    public static final Vector3f AMBIENT_LIGHT = new Vector3f(0.3f, 0.3f, 0.3f);

    public static final float GRAVITY = -660f;
    public static final float JUMP_VELOCITY = 50f;
    public static final float PLAYER_HEIGHT = 1.8f;
    public static final float PLAYER_MOVE_SPEED = 50.0f;

}
