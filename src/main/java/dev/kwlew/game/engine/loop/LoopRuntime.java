package dev.kwlew.game.engine.loop;

final class LoopRuntime {

    boolean running;
    int framesThisSecond;
    int ticksThisSecond;
    double reportAccumulator;
    double lastAlpha;
    double tickAccumulator;
    double frameAccumulator;
    LoopState loopState = LoopState.MAIN_MENU;

    void resetSimulationWindow() {
        tickAccumulator = 0.0d;
        lastAlpha = 0.0d;
        ticksThisSecond = 0;
    }
}
