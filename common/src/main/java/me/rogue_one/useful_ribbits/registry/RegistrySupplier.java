package me.rogue_one.useful_ribbits.registry;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * A single pending registry entry.
 * <p>
 * The value is built lazily, on first access, and cached. Building only CONSTRUCTS the object; it
 * does not register it. That split is what lets a spawn-egg factory ask for its EntityType while the
 * ITEM registry is being filled even if ENTITY_TYPE has not been filled yet (Forge/NeoForge fill
 * registries in vanilla order, which we do not control): the object is built once here and the
 * ENTITY_TYPE drain later registers that same cached instance.
 */
public final class RegistrySupplier<T> implements Supplier<T> {
    private final Identifier id;
    private final Supplier<? extends T> factory;
    private @Nullable T value;

    RegistrySupplier(Identifier id, Supplier<? extends T> factory) {
        this.id = Objects.requireNonNull(id, "id");
        this.factory = Objects.requireNonNull(factory, "factory");
    }

    public Identifier id() {
        return this.id;
    }

    /** Builds the value if it has not been built yet, caches it, and returns it. */
    T create() {
        if (this.value == null) {
            this.value = Objects.requireNonNull(this.factory.get(), () -> "Factory for " + this.id + " returned null");
        }
        return this.value;
    }

    @Override
    public T get() {
        return create();
    }

    public boolean isBound() {
        return this.value != null;
    }

    @Override
    public String toString() {
        return "RegistrySupplier[" + this.id + "]";
    }
}
