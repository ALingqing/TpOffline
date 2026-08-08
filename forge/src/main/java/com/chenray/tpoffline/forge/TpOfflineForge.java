package com.chenray.tpoffline.forge;

import com.chenray.tpoffline.common.TpOfflineCommon;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

/**
 * Forge 平台入口
 */
@Mod(TpOfflineCommon.MOD_ID)
public class TpOfflineForge {

    public TpOfflineForge() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        TpOfflineCommon.registerCommands(event.getDispatcher());
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity) {
            TpOfflineCommon.onPlayerQuit((ServerPlayerEntity) event.getEntity());
        }
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        TpOfflineCommon.onServerStarting(event.getServer());
    }

    @SubscribeEvent
    public void onServerStopping(FMLServerStoppingEvent event) {
        TpOfflineCommon.onServerStopping(event.getServer());
    }
}
