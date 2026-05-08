package dev.kwlew.game.engine.loop;

import dev.kwlew.game.engine.ui.MainMenuScreen;
import dev.kwlew.game.engine.ui.PauseMenuScreen;
import dev.kwlew.game.window.Window;
import org.lwjgl.glfw.GLFW;

final class LoopStateMachine {

    private final Window window;
    private final LoopSettings settings;
    private final MainMenuScreen mainMenuScreen;
    private final PauseMenuScreen pauseMenuScreen;
    private final Runnable inGameTick;
    private boolean escapePressedLastTick;

    LoopStateMachine(
            Window window,
            LoopSettings settings,
            MainMenuScreen mainMenuScreen,
            PauseMenuScreen pauseMenuScreen,
            Runnable inGameTick
    ) {
        this.window = window;
        this.settings = settings;
        this.mainMenuScreen = mainMenuScreen;
        this.pauseMenuScreen = pauseMenuScreen;
        this.inGameTick = inGameTick;
    }

    void init() {
        mainMenuScreen.init();
        pauseMenuScreen.init();
    }

    void cleanup() {
        mainMenuScreen.cleanup();
        pauseMenuScreen.cleanup();
    }

    boolean update(LoopRuntime runtime) {
        boolean escapeJustPressed = isEscapeJustPressed();

        if (runtime.loopState == LoopState.MAIN_MENU) {
            MainMenuScreen.Action action = mainMenuScreen.tick(window);
            if (action == MainMenuScreen.Action.START_GAME) {
                runtime.loopState = LoopState.IN_GAME;
                runtime.resetSimulationWindow();
                System.out.println("Entered in-game state (placeholder).");
                return true;
            }
            return action != MainMenuScreen.Action.QUIT_GAME && !escapeJustPressed;
        }

        if (runtime.loopState == LoopState.IN_GAME) {
            if (escapeJustPressed) {
                runtime.loopState = LoopState.PAUSED;
                runtime.resetSimulationWindow();
                System.out.println("Game paused.");
                return true;
            }

            while (runtime.tickAccumulator >= settings.tickStepSeconds()) {
                inGameTick.run();
                runtime.tickAccumulator -= settings.tickStepSeconds();
                runtime.ticksThisSecond++;
            }
            return true;
        }

        PauseMenuScreen.Action action = pauseMenuScreen.tick(window);
        if (action == PauseMenuScreen.Action.RESUME_GAME || escapeJustPressed) {
            runtime.loopState = LoopState.IN_GAME;
            runtime.resetSimulationWindow();
            System.out.println("Game resumed.");
            return true;
        }

        if (action == PauseMenuScreen.Action.GO_TO_MAIN_MENU) {
            runtime.loopState = LoopState.MAIN_MENU;
            runtime.resetSimulationWindow();
            System.out.println("Returned to main menu.");
        }
        return true;
    }

    private boolean isEscapeJustPressed() {
        boolean currentlyPressed = GLFW.glfwGetKey(window.getHandle(), GLFW.GLFW_KEY_ESCAPE) == GLFW.GLFW_PRESS;
        boolean justPressed = currentlyPressed && !escapePressedLastTick;
        escapePressedLastTick = currentlyPressed;
        return justPressed;
    }
}
