package net.ramixin.dynamo.neoforge.impl.contexts;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.ramixin.stator.events.contexts.BlockBrokenContext;

public record NeoForgeBlockBrokenContext(LevelAccessor level, Player player, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity) implements BlockBrokenContext {

    public NeoForgeBlockBrokenContext(BlockEvent.BreakEvent event) {
        this(event.getLevel(), event.getPlayer(), event.getPos(), event.getState(), event.getLevel().getBlockEntity(event.getPos()));
    }

}
