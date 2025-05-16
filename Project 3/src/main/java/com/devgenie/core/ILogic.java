package com.devgenie.core;

public interface ILogic {
    void init() throws Exception;
    void update(float interval, MouseInput mouseInput);
    void render();
    void input(MouseInput mouseInput);
    void cleanup();
}
