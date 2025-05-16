package com.devgenie.core.lighting;

import org.joml.Vector3f;

public class SpotLight {
    private PointLight pointLight;

    private Vector3f coneDirection;
    private float cutOff;

    public SpotLight(PointLight pointLight, Vector3f coneDirection, float cutOff) {
        this.pointLight = pointLight;
        this.cutOff = cutOff;
        this.coneDirection = coneDirection;
    }

    public SpotLight(SpotLight spotLight) {
        this(new PointLight(spotLight.getPointLight()), new Vector3f(spotLight.getConeDirection()), 0);
        this.cutOff = spotLight.getCutOff();
    }

    public PointLight getPointLight() {
        return pointLight;
    }


    public void setPointLight(PointLight pointLight) {
        this.pointLight = pointLight;
    }

    public Vector3f getConeDirection() {
        return coneDirection;
    }

    public void setConeDirection(Vector3f coneDirection) {
        this.coneDirection = coneDirection;
    }

    public float getCutOff() {
        return cutOff;
    }

    public void setCutOff(float cutOff) {
        this.cutOff = cutOff;
    }
}
