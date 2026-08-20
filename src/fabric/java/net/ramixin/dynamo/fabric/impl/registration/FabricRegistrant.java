package net.ramixin.dynamo.fabric.impl.registration;

import net.ramixin.stator.registration.Registrant;

public record FabricRegistrant<T>(T value) implements Registrant<T> {

    @Override
    public T get() {
        return value;
    }
}
