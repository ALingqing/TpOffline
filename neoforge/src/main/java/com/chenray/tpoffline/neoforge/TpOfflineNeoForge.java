package com.chenray.tpoffline.neoforge;

import com.chenray.tpoffline.common.TpOfflineCommon;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

/**
 * NeoForge 平台入口
 */
@Mod(TpOfflineCommon.MOD_ID)
public class TpOfflineNeoForge {

    public TpOfflineNeoForge() {
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        TpOfflineCommon.registerCommands(event.getDispatcher());
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            TpOfflineCommon.onPlayerQuit(player);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        TpOfflineCommon.onServerStarting(event.getServer());
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        TpOfflineCommon.onServerStopping(event.getServer());
    }
}
