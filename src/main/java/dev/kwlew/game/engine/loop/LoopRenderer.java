package dev.kwlew.game.engine.loop;

import dev.kwlew.game.engine.client.InGameClient;
import dev.kwlew.game.engine.ui.MainMenuScreen;
import dev.kwlew.game.engine.ui.PauseMenuScreen;
import dev.kwlew.game.window.Window;

final class LoopRenderer {

    private final MainMenuScreen mainMenuScreen;
    private final PauseMenuScreen pauseMenuScreen;
    private final InGameClient inGameClient;

    LoopRenderer(MainMenuScreen mainMenuScreen, PauseMenuScreen pauseMenuScreen, InGameClient inGameClient) {
        this.mainMenuScreen = mainMenuScreen;
        this.pauseMenuScreen = pauseMenuScreen;
        this.inGameClient = inGameClient;
    }

    void init() {
        inGameClient.init();
    }

    void cleanup() {
        inGameClient.cleanup();
    }

    void render(Window window, LoopState loopState, float alpha) {
        inGameClient.setActive(window, loopState == LoopState.IN_GAME);

        if (loopState == LoopState.MAIN_MENU) {
            mainMenuScreen.render(window);
            return;
        }

        if (loopState == LoopState.PAUSED) {
            pauseMenuScreen.render(window);
            return;
        }

        inGameClient.render(window, alpha);
    }
}
