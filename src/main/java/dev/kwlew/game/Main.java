package dev.kwlew.game;

import dev.kwlew.game.engine.loop.LoopSettings;

public class Main {
    public static void main(String[] args) {
        int maxFps = Integer.getInteger("game.maxFps", 0);

        LoopSettings settings = new LoopSettings(maxFps);
        new GameApplication(settings).run();
    }
}