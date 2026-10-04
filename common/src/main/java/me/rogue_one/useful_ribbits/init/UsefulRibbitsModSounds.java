package me.rogue_one.useful_ribbits.init;

import me.rogue_one.useful_ribbits.UsefulRibbits;
import me.rogue_one.useful_ribbits.registry.ModRegistries;
import me.rogue_one.useful_ribbits.registry.RegistrySupplier;
import net.minecraft.sounds.SoundEvent;

public final class UsefulRibbitsModSounds {
    private static final RegistrySupplier<SoundEvent> RIBBIT_AMBIANT_SUP =
            ModRegistries.SOUND_EVENTS.add("ribbit_ambiant", () -> SoundEvent.createVariableRangeEvent(UsefulRibbits.id("ribbit_ambiant")));
    private static final RegistrySupplier<SoundEvent> RIBBIT_DEATH_SUP =
            ModRegistries.SOUND_EVENTS.add("ribbit_death", () -> SoundEvent.createVariableRangeEvent(UsefulRibbits.id("ribbit_death")));
    private static final RegistrySupplier<SoundEvent> RIBBIT_HURT_SUP =
            ModRegistries.SOUND_EVENTS.add("ribbit_hurt", () -> SoundEvent.createVariableRangeEvent(UsefulRibbits.id("ribbit_hurt")));
    private static final RegistrySupplier<SoundEvent> RIBBIT_STEP_SUP =
            ModRegistries.SOUND_EVENTS.add("ribbit_step", () -> SoundEvent.createVariableRangeEvent(UsefulRibbits.id("ribbit_step")));

    // Raw fields for downstream runtime code (Chef/Miner/FarmerRibbitEntity sound getters). Bound after drain.
    public static SoundEvent RIBBIT_AMBIANT;
    public static SoundEvent RIBBIT_DEATH;
    public static SoundEvent RIBBIT_HURT;
    public static SoundEvent RIBBIT_STEP;

    private UsefulRibbitsModSounds() {}

    /** Triggers class-init so the entries above queue. Called by ModRegistries.bootstrap(). */
    public static void load() {}

    public static void bind() {
        RIBBIT_AMBIANT = RIBBIT_AMBIANT_SUP.get();
        RIBBIT_DEATH = RIBBIT_DEATH_SUP.get();
        RIBBIT_HURT = RIBBIT_HURT_SUP.get();
        RIBBIT_STEP = RIBBIT_STEP_SUP.get();
    }
}
