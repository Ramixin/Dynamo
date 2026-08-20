package net.ramixin.dynamo.fabric.contexts;

import net.minecraft.world.entity.player.Player;
import net.ramixin.stator.events.contexts.PlayerJoinedServerContext;

public record FabricPlayerJoinedServerContext(Player player) implements PlayerJoinedServerContext {
}
