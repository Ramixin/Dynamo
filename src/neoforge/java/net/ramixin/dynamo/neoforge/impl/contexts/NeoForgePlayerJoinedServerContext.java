package net.ramixin.dynamo.neoforge.impl.contexts;

import net.minecraft.world.entity.player.Player;
import net.ramixin.stator.events.contexts.PlayerJoinedServerContext;

public record NeoForgePlayerJoinedServerContext(Player player) implements PlayerJoinedServerContext {
}
