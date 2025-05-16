package com.devgenie.test;
import com.devgenie.core.EngineManager;
import com.devgenie.core.WindowManager;
import com.devgenie.core.utils.Consts;

public class Launcher {

    private static WindowManager window;
    private static TestGame game;

    public static void main(String[] args) {
        window = new WindowManager(false, Consts.TITLE, 1600, 900);
        game = new TestGame();
        EngineManager engine = new EngineManager();
        try {
            engine.start();
        } catch (Exception e ){
            e.printStackTrace();
        }
    }

    public static WindowManager getWindow() {
        return window;
    }

    public static TestGame getGame() {
        return game;
    }
}
