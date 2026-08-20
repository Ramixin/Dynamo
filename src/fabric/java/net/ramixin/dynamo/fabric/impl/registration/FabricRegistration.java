package net.ramixin.dynamo.fabric.impl.registration;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.ramixin.dynamo.fabric.impl.networking.FabricPayloadContext;
import net.ramixin.stator.networking.PayloadContext;
import net.ramixin.stator.registration.DeferredRegistration;
import net.ramixin.stator.registration.Registrant;
import net.ramixin.stator.registration.Registration;

import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FabricRegistration implements Registration {

    @Override
    public <T, V extends T> Registrant<V> entry(Registry<T> registry, Identifier id, Supplier<V> value) {
        V realValue = value.get();
        Registry.register(registry, id, realValue);
        return new FabricRegistrant<>(realValue);
    }

    @Override
    public <T extends CustomPacketPayload> void clientboundPayload(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        PayloadTypeRegistry.clientboundPlay().register(type, codec);
    }

    @Override
    public <T extends CustomPacketPayload> void serverboundPayload(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec, Consumer<PayloadContext<T>> handler) {
        PayloadTypeRegistry.serverboundPlay().register(type, codec);
        ServerPlayNetworking.registerGlobalReceiver(type, (payload, ctx) -> {
            FabricPayloadContext<T> context = new FabricPayloadContext<>(payload, ctx);
            handler.accept(context);
        });
    }

    @Override
    public void loadDeferred(DeferredRegistration reg) {
        Queue<Consumer<Registration>> deferrals = reg.deferrals();
        while (!deferrals.isEmpty()) {
            Consumer<Registration> deferral = deferrals.poll();
            deferral.accept(this);
        }
    }
}
