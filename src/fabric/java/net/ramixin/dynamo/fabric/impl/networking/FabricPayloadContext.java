package net.ramixin.dynamo.fabric.impl.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.entity.player.Player;
import net.ramixin.stator.networking.PayloadContext;

public record FabricPayloadContext<T>(T payload, Player player) implements PayloadContext<T> {

    public FabricPayloadContext(T payload, ServerPlayNetworking.Context context) {
        this(payload, context.player());
    }

}
