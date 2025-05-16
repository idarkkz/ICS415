package com.devgenie.core.rendering;

import com.devgenie.core.entity.Camera;
import com.devgenie.core.entity.Model;
import com.devgenie.core.lighting.DirectionalLight;
import com.devgenie.core.lighting.PointLight;
import com.devgenie.core.lighting.SpotLight;

public interface IRenderer<T> {
    public void init() throws Exception;

    public void render(Camera camera, PointLight[] pointLights, SpotLight[] spotLights, DirectionalLight directionalLight);

    abstract void bind(Model model);

    public void unbind();

    public void prepare(T t, Camera camera);

    public void cleanup();

}
