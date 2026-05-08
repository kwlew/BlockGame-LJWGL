package dev.kwlew.game.engine.loop;

final class FramePacer {

    private FramePacer() {}

    static boolean shouldRender(LoopRuntime runtime, int activeFpsCap) {
        if (activeFpsCap <= 0) {
            return true;
        }

        double frameStep = 1.0d / activeFpsCap;
        if (runtime.frameAccumulator < frameStep) {
            return false;
        }

        runtime.frameAccumulator -= frameStep;
        return true;
    }
}
