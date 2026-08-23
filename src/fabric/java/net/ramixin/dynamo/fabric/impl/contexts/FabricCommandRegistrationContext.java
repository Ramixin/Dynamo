package net.ramixin.dynamo.fabric.impl.contexts;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.ramixin.stator.events.contexts.CommandRegistrationContext;

public record FabricCommandRegistrationContext(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) implements CommandRegistrationContext {
}
