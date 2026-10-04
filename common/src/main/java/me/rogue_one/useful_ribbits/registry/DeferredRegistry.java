package me.rogue_one.useful_ribbits.registry;

import me.rogue_one.useful_ribbits.UsefulRibbits;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Collects entries for one vanilla registry so they can be handed to whichever registration
 * mechanism the current loader uses. Fabric writes straight into the registry; Forge and NeoForge
 * replay the entries during their own RegisterEvent.
 */
public final class DeferredRegistry<T> {
    private final Registry<T> registry;
    private final ResourceKey<? extends Registry<T>> registryKey;
    private final List<RegistrySupplier<? extends T>> entries = new ArrayList<>();

    private DeferredRegistry(Registry<T> registry, ResourceKey<? extends Registry<T>> registryKey) {
        this.registry = registry;
        this.registryKey = registryKey;
    }

    public static <T> DeferredRegistry<T> of(Registry<T> registry, ResourceKey<? extends Registry<T>> registryKey) {
        return new DeferredRegistry<>(registry, registryKey);
    }

    public <R extends T> RegistrySupplier<R> add(String name, Supplier<R> factory) {
        RegistrySupplier<R> entry = new RegistrySupplier<>(UsefulRibbits.id(name), factory);
        this.entries.add(entry);
        return entry;
    }

    public ResourceKey<? extends Registry<T>> registryKey() {
        return this.registryKey;
    }

    public List<RegistrySupplier<? extends T>> entries() {
        return Collections.unmodifiableList(this.entries);
    }

    /** Fabric: write every pending entry straight into the vanilla registry. */
    public void registerAll() {
        for (RegistrySupplier<? extends T> entry : this.entries) {
            Registry.register(this.registry, entry.id(), entry.create());
        }
    }

    /** Forge/NeoForge: hand every pending entry to {@code consumer}. */
    public void forEach(EntryConsumer<T> consumer) {
        for (RegistrySupplier<? extends T> entry : this.entries) {
            consumer.accept(entry.id(), entry::create);
        }
    }

    @FunctionalInterface
    public interface EntryConsumer<T> {
        void accept(Identifier id, Supplier<? extends T> value);
    }
}
