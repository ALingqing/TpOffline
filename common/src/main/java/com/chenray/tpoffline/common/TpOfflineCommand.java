package com.chenray.tpoffline.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

/**
 * 指令注册：/tpo <玩家名>
 * 传送到指定玩家最后所在位置；玩家离线时使用其最后下线位置
 */
public final class TpOfflineCommand {

    private TpOfflineCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, PlayerPositionManager positionManager) {
        dispatcher.register(Commands.literal("tpo")
                .requires(source -> source.hasPermission(2)) // 需要 OP（2 级权限）
                .then(Commands.argument("player", StringArgumentType.word())
                        .executes(context -> execute(
                                context.getSource(),
                                StringArgumentType.getString(context, "player"),
                                positionManager))));
    }

    private static int execute(CommandSourceStack source, String targetName, PlayerPositionManager positionManager) {
        if (!(source.getEntity() instanceof ServerPlayer executor)) {
            source.sendFailure(Component.literal("§c该指令只能由玩家在游戏内执行"));
            return 0;
        }

        // 目标玩家在线：直接传送到其当前位置
        ServerPlayer online = source.getServer().getPlayerList().getPlayerByName(targetName);
        if (online != null) {
            executor.teleportTo((ServerLevel) online.level(), online.getX(), online.getY(), online.getZ(),
                    online.getYRot(), online.getXRot());
            source.sendSuccess(() -> Component.literal("§a已传送到在线玩家 §b" + targetName), true);
            return 1;
        }

        // 目标玩家离线：使用其最后下线位置
        PlayerPositionManager.SavedPosition saved = positionManager.get(targetName);
        if (saved == null) {
            source.sendFailure(Component.literal("§c找不到玩家 §b" + targetName + " §c的下线位置记录"));
            return 0;
        }

        ResourceKey<Level> dimensionKey = ResourceKey.create(Registries.DIMENSION, ResourceLocation.tryParse(saved.dimension));
        ServerLevel world = source.getServer().getLevel(dimensionKey);
        if (world == null) {
            world = source.getServer().overworld();
        }

        executor.teleportTo(world, saved.x, saved.y, saved.z, saved.yaw, saved.pitch);
        source.sendSuccess(() -> Component.literal("§a已传送到 §b" + targetName
                + " §a最后下线位置 §7["
                + saved.dimension.replace("minecraft:", "") + " "
                + (int) saved.x + ", " + (int) saved.y + ", " + (int) saved.z + "]"), true);
        return 1;
    }
}
