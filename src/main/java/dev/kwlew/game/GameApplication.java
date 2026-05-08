package dev.kwlew.game;

import dev.kwlew.game.engine.loop.FixedTickLoop;
import dev.kwlew.game.engine.loop.LoopSettings;
import dev.kwlew.game.window.Window;

public class GameApplication {

    private final LoopSettings settings;
    private Window window;

    public GameApplication(LoopSettings settings) {
        this.settings = settings;
    }

    public void run() {
        init();
        new FixedTickLoop(window, settings).run();
        cleanup();
    }

    private void init() {
        window = new Window(1280, 720, "Block Game");
        window.init();
        window.setVsync(false);
    }

    private void cleanup() {
        if (window != null) {
            window.cleanup();
        }
    }
}
