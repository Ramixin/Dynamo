package net.ramixin.dynamo.fabric.impl.networking;

import net.minecraft.world.entity.player.Player;
import net.ramixin.stator.networking.ClientPayloadContext;

public record FabricClientPayloadContext<T>(T payload, Player player) implements ClientPayloadContext<T> {
}
