package dev.kwlew.game.engine.loop;

final class LoopReporter {

    private final LoopSettings settings;
    private final int menuAndPauseFpsCap;

    LoopReporter(LoopSettings settings, int menuAndPauseFpsCap) {
        this.settings = settings;
        this.menuAndPauseFpsCap = menuAndPauseFpsCap;
    }

    void printStartupMessage() {
        String inGameFpsCapLabel = settings.hasFrameCap() ? Integer.toString(settings.maxFps()) : "uncapped";
        System.out.printf(
                "Loop started | Menu/Pause FPS cap: %d | In-game server TPS: %d (%.2f ms) | In-game FPS cap: %s%n",
                menuAndPauseFpsCap,
                settings.targetTps(),
                settings.tickStepSeconds() * 1000.0d,
                inGameFpsCapLabel
        );
    }

    void printReport(LoopRuntime runtime) {
        String activeFpsCapLabel = getActiveFpsCap(runtime.loopState) > 0
                ? Integer.toString(getActiveFpsCap(runtime.loopState))
                : "uncapped";

        if (runtime.loopState == LoopState.MAIN_MENU || runtime.loopState == LoopState.PAUSED) {
            System.out.printf(
                    "FPS: %d | state: %s | cap: %s%n",
                    runtime.framesThisSecond,
                    runtime.loopState,
                    activeFpsCapLabel
            );
            return;
        }

        System.out.printf(
                "FPS: %d | TPS: %d | alpha: %.3f | state: %s | cap: %s%n",
                runtime.framesThisSecond,
                runtime.ticksThisSecond,
                runtime.lastAlpha,
                runtime.loopState,
                activeFpsCapLabel
        );
    }

    int getActiveFpsCap(LoopState loopState) {
        if (loopState == LoopState.MAIN_MENU || loopState == LoopState.PAUSED) {
            return menuAndPauseFpsCap;
        }
        return settings.maxFps();
    }
}
