package com.chenray.tpoffline.fabric;

import com.chenray.tpoffline.common.TpOfflineCommon;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

/**
 * Fabric 平台入口
 */
public class TpOfflineFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        // 指令注册
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                TpOfflineCommon.registerCommands(dispatcher));

        // 玩家下线时记录位置
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                TpOfflineCommon.onPlayerQuit(handler.getPlayer()));

        // 服务器启动加载记录
        ServerLifecycleEvents.SERVER_STARTING.register(TpOfflineCommon::onServerStarting);

        // 服务器关闭前补录在线玩家
        ServerLifecycleEvents.SERVER_STOPPING.register(TpOfflineCommon::onServerStopping);

        TpOfflineCommon.LOGGER.info("[TpOffline] Fabric 平台已加载，/tpo <玩家名> 传送到离线玩家最后下线位置");
    }
}
