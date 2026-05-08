package dev.kwlew.game.engine.world.block;

public enum BlockId {
    AIR(0),
    GRASS(1),
    DIRT(2),
    STONE(3);

    private final short id;

    BlockId(int id) {
        this.id = (short) id;
    }

    public short id() {
        return id;
    }
}