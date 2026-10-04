package me.rogue_one.useful_ribbits.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import me.rogue_one.useful_ribbits.UsefulRibbits;
import me.rogue_one.useful_ribbits.platform.PlatformHelper;
import me.rogue_one.useful_ribbits.util.JsonIO;

/**
 * Mod settings, stored in {@code config/useful_ribbits.json}. Loaded once at mod init; the values
 * are read live by the entities, so editing the file and rejoining a world applies changes.
 */
public final class UsefulRibbitsConfig {
    /** Master toggle for the ambient "croak" sound. Set false for silent ribbits. */
    public boolean ambientCroaks = true;

    /**
     * Minimum ticks between a ribbit's croaks (higher = quieter). Vanilla ambient mobs use ~80;
     * the default here is deliberately calmer so a crowd of ribbits is not constant noise.
     */
    public int ambientCroakInterval = 240;

    private static UsefulRibbitsConfig instance = new UsefulRibbitsConfig();

    private UsefulRibbitsConfig() {}

    public static UsefulRibbitsConfig get() {
        return instance;
    }

    private static Path path() {
        return PlatformHelper.getConfigFolder().resolve("useful_ribbits.json");
    }

    /** Loads the config, creating it with defaults when missing. Safe to call once at mod init. */
    public static void load() {
        try {
            Path path = path();
            if (Files.exists(path)) {
                UsefulRibbitsConfig loaded = JsonIO.readObjectFromFile(path, UsefulRibbitsConfig.class);
                if (loaded != null) {
                    instance = loaded;
                }
            }
            save();
        } catch (Exception e) {
            UsefulRibbits.LOGGER.warn("Could not load useful_ribbits config; using defaults", e);
        }
    }

    public static void save() {
        try {
            JsonIO.writeObjectToFile(path(), instance);
        } catch (IOException e) {
            UsefulRibbits.LOGGER.warn("Could not save useful_ribbits config", e);
        }
    }
}
