package net.ramixin.dynamo.neoforge.impl.networking;

import net.minecraft.world.entity.player.Player;
import net.ramixin.stator.networking.PayloadContext;

public record NeoForgePayloadContext<T>(T payload, Player player) implements PayloadContext<T>  {
}
