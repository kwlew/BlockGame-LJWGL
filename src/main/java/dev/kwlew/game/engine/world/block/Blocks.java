package dev.kwlew.game.engine.world.block;

public final class Blocks {
    private static final BlockType[] REGISTRY = new BlockType[256];

    public static final BlockType AIR   = register(new BlockType(BlockId.AIR.id(),   false, false, 0.0f));
    public static final BlockType GRASS = register(new BlockType(BlockId.GRASS.id(), true,  true,  0.6f));
    public static final BlockType DIRT  = register(new BlockType(BlockId.DIRT.id(),  true,  true,  0.5f));
    public static final BlockType STONE = register(new BlockType(BlockId.STONE.id(), true,  true,  1.5f));

    private Blocks() {}

    private static BlockType register(BlockType type) {
        REGISTRY[type.id()] = type;
        return type;
    }

    public static BlockType byId(short id) {
        BlockType type = (id >= 0 && id < REGISTRY.length) ? REGISTRY[id] : null;
        return type != null ? type : AIR;
    }
}