package net.ramixin.dynamo.neoforge.impl.registration;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.ramixin.stator.registration.Registrant;

public record NeoForgeRegistrant<T, V extends T>(DeferredHolder<T, V> holder) implements Registrant<V> {
    @Override
    public V get() {
        return holder.get();
    }
}
