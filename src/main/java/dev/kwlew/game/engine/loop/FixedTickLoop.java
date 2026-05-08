package dev.kwlew.game.engine.loop;

import dev.kwlew.game.engine.client.InGameClient;
import dev.kwlew.game.engine.ui.MainMenuScreen;
import dev.kwlew.game.engine.ui.PauseMenuScreen;
import dev.kwlew.game.window.Window;
import org.lwjgl.glfw.GLFW;

public class FixedTickLoop {

    private static final double MAX_FRAME_DELTA_SECONDS = 0.25d;
    private static final int MENU_AND_PAUSE_FPS_CAP = 60;

    private final LoopSettings settings;
    private final Window window;
    private final LoopRuntime runtime = new LoopRuntime();
    private final LoopStateMachine stateMachine;
    private final LoopRenderer renderer;
    private final LoopReporter reporter;

    public FixedTickLoop(Window window, LoopSettings settings) {
        this.window = window;
        this.settings = settings;
        MainMenuScreen mainMenuScreen = new MainMenuScreen();
        PauseMenuScreen pauseMenuScreen = new PauseMenuScreen();
        InGameClient inGameClient = new InGameClient();
        this.stateMachine = new LoopStateMachine(
                window,
                settings,
                mainMenuScreen,
                pauseMenuScreen,
                () -> inGameClient.tick(window, (float) settings.tickStepSeconds())
        );
        this.renderer = new LoopRenderer(mainMenuScreen, pauseMenuScreen, inGameClient);
        this.reporter = new LoopReporter(settings, MENU_AND_PAUSE_FPS_CAP);
    }

    public void run() {
        runtime.running = true;
        try {
            stateMachine.init();
            renderer.init();

            double previousTime = GLFW.glfwGetTime();

            reporter.printStartupMessage();

            while (runtime.running && !window.shouldClose()) {
                double currentTime = GLFW.glfwGetTime();
                double frameDelta = clampFrameDelta(currentTime - previousTime);
                previousTime = currentTime;

                runtime.tickAccumulator += frameDelta;
                runtime.frameAccumulator += frameDelta;
                runtime.reportAccumulator += frameDelta;

                window.pollEvents();
                runtime.running = stateMachine.update(runtime);

                int activeFpsCap = reporter.getActiveFpsCap(runtime.loopState);
                boolean shouldRender = FramePacer.shouldRender(runtime, activeFpsCap);

                if (shouldRender) {
                    runtime.lastAlpha = runtime.tickAccumulator / settings.tickStepSeconds();
                    renderer.render(window, runtime.loopState, (float) runtime.lastAlpha);
                    window.swapBuffers();
                    runtime.framesThisSecond++;
                    if (activeFpsCap <= 0) {
                        runtime.frameAccumulator = 0.0d;
                    }
                }

                if (runtime.reportAccumulator >= 1.0d) {
                    reporter.printReport(runtime);
                    runtime.reportAccumulator -= 1.0d;
                    runtime.framesThisSecond = 0;
                    runtime.ticksThisSecond = 0;
                }
            }
        } finally {
            renderer.cleanup();
            stateMachine.cleanup();
        }
    }

    private static double clampFrameDelta(double frameDelta) {
        if (frameDelta < 0.0d) {
            return 0.0d;
        }
        if (frameDelta > MAX_FRAME_DELTA_SECONDS) {
            return MAX_FRAME_DELTA_SECONDS;
        }
        return frameDelta;
    }
}
