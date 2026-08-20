package net.ramixin.dynamo.neoforge.impl.networking;

import net.minecraft.world.entity.player.Player;
import net.ramixin.stator.networking.ClientPayloadContext;

public record NeoForgeClientPayloadContext<T>(T payload, Player player) implements ClientPayloadContext<T> {

}
