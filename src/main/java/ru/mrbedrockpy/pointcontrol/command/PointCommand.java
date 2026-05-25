package ru.mrbedrockpy.pointcontrol.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import ru.mrbedrockpy.pointcontrol.client.AssimilationAnimation;
import ru.mrbedrockpy.pointcontrol.point.Point;
import ru.mrbedrockpy.pointcontrol.point.PointManager;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PointCommand extends AbstractCommand {

    public PointCommand() {
        super("point", List.of(
                Commands.literal("create")
                        .requires(source -> source.getEntity() instanceof ServerPlayer)
                        .then(Commands.argument("id", STR_TYPE)
                                .then(Commands.argument("radius", DOUBLE_TYPE).executes(PointCommand::create))
                        ),
                Commands.literal("remove")
                        .then(Commands.argument("id", STR_TYPE).suggests(PointCommand::suggestPoints).executes(PointCommand::remove)),
                Commands.literal("list").executes(PointCommand::list),
                Commands.literal("radius")
                        .then(Commands.argument("id", STR_TYPE).suggests(PointCommand::suggestPoints)
                                .then(Commands.argument("radius", DOUBLE_TYPE).executes(PointCommand::radius))),
                Commands.literal("assimilation")
                        .then(Commands.literal("reset")
                                .then(Commands.argument("id", STR_TYPE).suggests(PointCommand::suggestPoints).executes(PointCommand::reset))
                                .then(Commands.literal("all").executes(PointCommand::resetAll))
                        )
                        .then(Commands.literal("duration")
                                .then(Commands.argument("id", STR_TYPE).suggests(PointCommand::suggestPoints)
                                        .then(Commands.argument("duration", INT_TYPE).executes(PointCommand::assimilationDuration))
                                )
                        )
                        .then(Commands.literal("animation")
                                .then(Commands.argument("id", STR_TYPE)
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(PointManager.getPoints().keySet(), builder))
                                        .then(Commands.argument("animation", STR_TYPE)
                                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                                        Arrays.stream(AssimilationAnimation.values()).map(Enum::name).map(String::toLowerCase).toList(), builder))
                                                .executes(PointCommand::assimilationAnimation)
                                        )
                                )
                        )
        ));
    }

    private static int create(CommandContext<CommandSourceStack> ctx) {
        String id = StringArgumentType.getString(ctx, "id");
        double radius = DoubleArgumentType.getDouble(ctx, "radius");
        CommandSourceStack source = ctx.getSource();
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            ctx.getSource().sendSystemMessage(
                    Component.literal("This command can only be executed by the player!")
                            .withStyle(ChatFormatting.RED));
            return 0;
        }
        Point point = new Point(id, player.level().dimension(), player.position().toVector3f(), radius);
        if (PointManager.addPoint(point)) {
            player.sendSystemMessage(Component.literal("Created point " + id + " with radius " + radius));
            return 1;
        }
        player.sendSystemMessage(Component.literal("Point with id already exists!").withStyle(ChatFormatting.RED));
        return 0;
    }

    private static int remove(CommandContext<CommandSourceStack> ctx) {
        PointManager.removePoint(StringArgumentType.getString(ctx, "id"));
        ctx.getSource().sendSystemMessage(Component.literal("Point been removed!"));
        return 1;
    }

    private static int list(CommandContext<CommandSourceStack> ctx) {
        ctx.getSource().sendSystemMessage(Component.literal("Points: ").append(String.join(", ", PointManager.getPoints().keySet())));
        return 1;
    }

    private static int reset(CommandContext<CommandSourceStack> ctx) {
        Point point = PointManager.getPoints().get(StringArgumentType.getString(ctx, "id"));
        if (point == null) {
            ctx.getSource().sendSystemMessage(Component.literal("Point with id not found!"));
            return 0;
        }
        reset0(point);
        return 1;
    }

    private static int resetAll(CommandContext<CommandSourceStack> ctx) {
        PointManager.getPoints().values().forEach(PointCommand::reset0);
        return 1;
    }

    private static void reset0(Point point) {
        point.resetDominator();
    }

    private static int radius(CommandContext<CommandSourceStack> ctx) {
        Point point = PointManager.getPoints().get(StringArgumentType.getString(ctx, "id"));
        if (point == null) {
            ctx.getSource().sendSystemMessage(Component.literal("Point with id not found!"));
            return 0;
        }
        point.setRadius(DoubleArgumentType.getDouble(ctx, "radius"));
        ctx.getSource().sendSystemMessage(Component.literal("Radius been set!"));
        return 1;
    }

    private static int assimilationDuration(CommandContext<CommandSourceStack> ctx) {
        Point point = PointManager.getPoints().get(StringArgumentType.getString(ctx, "id"));
        if (point == null) {
            ctx.getSource().sendSystemMessage(Component.literal("Point with id not found!"));
            return 0;
        }
        point.setAssimilationDuration(IntegerArgumentType.getInteger(ctx, "duration"));
        ctx.getSource().sendSystemMessage(Component.literal("Assimilation duration been set!"));
        return 1;
    }

    private static int assimilationAnimation(CommandContext<CommandSourceStack> ctx) {
        Point point = PointManager.getPoints().get(StringArgumentType.getString(ctx, "id"));
        if (point == null) {
            ctx.getSource().sendSystemMessage(Component.literal("Point with id not found!"));
            return 0;
        }
        String animationName = StringArgumentType.getString(ctx, "animation");
        AssimilationAnimation animation = AssimilationAnimation.getByName(animationName);
        if (animation == null) {
            ctx.getSource().sendSystemMessage(Component.literal("Unknown animation: " + animationName));
            return 0;
        }
        point.setAssimilationAnimation(animation);
        ctx.getSource().sendSystemMessage(Component.literal("Assimilation duration been set!"));
        return 1;
    }

    private static CompletableFuture<Suggestions> suggestPoints(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(PointManager.getPoints().keySet(), builder);
    }

    @Override
    protected boolean require(CommandSourceStack source) {
        return source.hasPermission(2);
    }
}
