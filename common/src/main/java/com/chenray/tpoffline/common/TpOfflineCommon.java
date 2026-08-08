package com.chenray.tpoffline.common;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TpOffline 跨平台共享逻辑
 * <p>
 * 三个平台（Fabric / Forge / NeoForge）的入口类只负责把平台事件接到这里。
 * <p>
 * 功能：/tpo <玩家名> 传送到指定玩家最后所在位置（支持离线玩家）
 */
public final class TpOfflineCommon {

    public static final String MOD_ID = "tpoffline";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final PlayerPositionManager POSITIONS = new PlayerPositionManager();

    private TpOfflineCommon() {
    }

    /**
     * 平台入口调用：注册 /tpo 指令
     */
    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        TpOfflineCommand.register(dispatcher, POSITIONS);
    }

    /**
     * 服务器启动：加载位置记录
     */
    public static void onServerStarting(MinecraftServer server) {
        POSITIONS.load(server);
    }

    /**
     * 服务器停止：补录所有在线玩家并保存
     */
    public static void onServerStopping(MinecraftServer server) {
        server.getPlayerList().getPlayers().forEach(POSITIONS::record);
        POSITIONS.save(server);
    }

    /**
     * 玩家下线：记录位置并保存
     */
    public static void onPlayerQuit(ServerPlayer player) {
        if (player == null) {
            return;
        }
        POSITIONS.record(player);
        MinecraftServer server = player.getServer();
        if (server != null) {
            POSITIONS.save(server);
        }
    }
}
