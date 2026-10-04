package me.rogue_one.useful_ribbits;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Loader-agnostic mod constants. The per-loader entrypoints live in each platform module. */
public final class UsefulRibbits {
    public static final String MODID = "useful_ribbits";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    private UsefulRibbits() {}

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
