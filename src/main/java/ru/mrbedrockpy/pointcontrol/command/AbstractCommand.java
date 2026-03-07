package ru.mrbedrockpy.pointcontrol.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import lombok.AllArgsConstructor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.List;

@AllArgsConstructor
public abstract class AbstractCommand {

    protected static IntegerArgumentType INT_TYPE = IntegerArgumentType.integer();
    protected static DoubleArgumentType DOUBLE_TYPE = DoubleArgumentType.doubleArg();
    protected static StringArgumentType STR_TYPE = StringArgumentType.word();

    private final String name;
    private final List<LiteralArgumentBuilder<CommandSourceStack>> subs;

    public final void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(name);
        root.requires(this::require);
        subs.forEach(root ::then);
        dispatcher.register(root);
    }

    protected abstract boolean require(CommandSourceStack source);
}
