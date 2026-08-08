package com.chenray.tpoffline.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

/**
 * 指令注册：/tpo <玩家名>
 * 传送到指定玩家最后所在位置；玩家离线时使用其最后下线位置
 */
public final class TpOfflineCommand {

    private TpOfflineCommand() {
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher, PlayerPositionManager positionManager) {
        dispatcher.register(Commands.literal("tpo")
                .requires(source -> source.hasPermissionLevel(2)) // 需要 OP（2 级权限）
                .then(Commands.argument("player", StringArgumentType.word())
                        .executes(context -> execute(
                                context.getSource(),
                                StringArgumentType.getString(context, "player"),
                                positionManager))));
    }

    private static int execute(CommandSource source, String targetName, PlayerPositionManager positionManager) {
        if (!(source.getEntity() instanceof ServerPlayerEntity)) {
            source.sendErrorMessage(new StringTextComponent("§c该指令只能由玩家在游戏内执行"));
            return 0;
        }
        ServerPlayerEntity executor = (ServerPlayerEntity) source.getEntity();

        // 目标玩家在线：直接传送到其当前位置
        ServerPlayerEntity online = source.getServer().getPlayerList().getPlayerByUsername(targetName);
        if (online != null) {
            executor.teleport((ServerWorld) online.world, online.getPosX(), online.getPosY(), online.getPosZ(),
                    online.rotationYaw, online.rotationPitch);
            source.sendFeedback(new StringTextComponent("§a已传送到在线玩家 §b" + targetName), true);
            return 1;
        }

        // 目标玩家离线：使用其最后下线位置
        PlayerPositionManager.SavedPosition saved = positionManager.get(targetName);
        if (saved == null) {
            source.sendErrorMessage(new StringTextComponent("§c找不到玩家 §b" + targetName + " §c的下线位置记录"));
            return 0;
        }

        RegistryKey<World> dimensionKey = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, ResourceLocation.tryCreate(saved.dimension));
        ServerWorld world = source.getServer().getWorld(dimensionKey);
        if (world == null) {
            world = source.getServer().getWorld(World.OVERWORLD);
        }

        executor.teleport(world, saved.x, saved.y, saved.z, saved.yaw, saved.pitch);
        source.sendFeedback(new StringTextComponent("§a已传送到 §b" + targetName
                + " §a最后下线位置 §7["
                + saved.dimension.replace("minecraft:", "") + " "
                + (int) saved.x + ", " + (int) saved.y + ", " + (int) saved.z + "]"), true);
        return 1;
    }
}
