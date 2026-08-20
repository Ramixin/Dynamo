package net.ramixin.dynamo.neoforge.impl.registration;

import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ramixin.dynamo.DynamoCommon;
import net.ramixin.dynamo.neoforge.impl.networking.NeoForgePayloadContext;
import net.ramixin.stator.networking.PayloadContext;
import net.ramixin.stator.registration.DeferredRegistration;
import net.ramixin.stator.registration.Registrant;
import net.ramixin.stator.registration.Registration;

import java.util.HashMap;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NeoForgeRegistration implements Registration {

    private final IEventBus bus;
    private final HashMap<Key, DeferredRegister<?>> deferredRegisters = new HashMap<>();

    public NeoForgeRegistration(IEventBus bus) {
        this.bus = bus;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, V extends T> Registrant<V> entry(Registry<T> registry, Identifier identifier, Supplier<V> supplier) {
        DynamoCommon.LOGGER.info("Registering entry {} to registry {}", identifier, registry);
        DeferredRegister<T> deferredRegister = (DeferredRegister<T>) deferredRegisters.computeIfAbsent(new Key(registry, identifier.getNamespace()), key -> {
            DeferredRegister<?> reg = DeferredRegister.create(key.registry, key.modId);
            reg.register(bus);
            return reg;
        });
        DeferredHolder<T, V> holder = deferredRegister.register(identifier.getPath(), supplier);
        DynamoCommon.LOGGER.info("registered {}", holder);
        return new NeoForgeRegistrant<>(holder);
    }

    @Override
    public <T extends CustomPacketPayload> void clientboundPayload(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        bus.addListener((Consumer<RegisterPayloadHandlersEvent>) event -> {
            PayloadRegistrar registrar = event.registrar("1");
            registrar.playToClient(type, streamCodec);
        });
    }

    @Override
    public <T extends CustomPacketPayload> void serverboundPayload(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, Consumer<PayloadContext<T>> handler) {
        bus.addListener((Consumer<RegisterPayloadHandlersEvent>) event -> {
            PayloadRegistrar registrar = event.registrar("1");
            registrar.playToServer(type, streamCodec, (payload, ctx) -> handler.accept(new NeoForgePayloadContext<>(payload, ctx.player())));
        });
    }

    @Override
    public void loadDeferred(DeferredRegistration deferredRegistration) {
        Queue<Consumer<Registration>> deferrals = deferredRegistration.deferrals();
        while (!deferrals.isEmpty()) {
            Consumer<Registration> registrant = deferrals.remove();
            registrant.accept(this);
        }
    }

    private record Key(Registry<?> registry, String modId) {

    }
}
