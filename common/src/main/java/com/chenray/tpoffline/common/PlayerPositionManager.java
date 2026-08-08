package com.chenray.tpoffline.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.Vec3;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 玩家位置记录管理：保存 / 读取每个玩家的最后位置（含离线玩家）
 */
public class PlayerPositionManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Map<String, SavedPosition> positions = new ConcurrentHashMap<>();
    private Path filePath;

    /**
     * 从存档目录加载已保存的位置记录（存档独立，多存档互不干扰）
     */
    public void load(MinecraftServer server) {
        filePath = resolvePath(server);
        if (!Files.exists(filePath)) {
            return;
        }
        try (Reader reader = Files.newBufferedReader(filePath)) {
            JsonObject obj = GSON.fromJson(reader, JsonObject.class);
            if (obj == null) {
                return;
            }
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                SavedPosition pos = GSON.fromJson(entry.getValue(), SavedPosition.class);
                if (pos != null && pos.dimension != null) {
                    positions.put(entry.getKey(), pos);
                }
            }
            TpOfflineCommon.LOGGER.info("已加载 {} 条下线位置记录", positions.size());
        } catch (IOException e) {
            TpOfflineCommon.LOGGER.error("加载位置记录失败", e);
        }
    }

    /**
     * 把当前位置记录写入磁盘
     */
    public void save(MinecraftServer server) {
        if (filePath == null) {
            filePath = resolvePath(server);
        }
        try {
            Files.createDirectories(filePath.getParent());
            try (Writer writer = Files.newBufferedWriter(filePath)) {
                GSON.toJson(positions, writer);
            }
        } catch (IOException e) {
            TpOfflineCommon.LOGGER.error("保存位置记录失败", e);
        }
    }

    /**
     * 记录玩家当前所在位置（下线 / 服务器关闭时调用）
     */
    public void record(ServerPlayer player) {
        if (player == null || player.level == null) {
            return;
        }
        String name = player.getGameProfile().getName();
        Vec3 pos = player.position();
        Level level = player.level;
        positions.put(name, new SavedPosition(
                level.dimension().location().toString(),
                pos.x, pos.y, pos.z,
                player.yRot, player.xRot,
                System.currentTimeMillis()
        ));
    }

    /**
     * 查询某玩家的最后位置
     */
    public SavedPosition get(String playerName) {
        return positions.get(playerName);
    }

    /**
     * 删除某玩家的记录
     */
    public boolean remove(String playerName) {
        return positions.remove(playerName) != null;
    }

    private static Path resolvePath(MinecraftServer server) {
        // <世界根目录>/tpoffline/positions.json （存档独立，多存档互不干扰）
        return server.getWorldPath(LevelResource.ROOT)
                .resolve("tpoffline")
                .resolve("positions.json");
    }

    /**
     * 玩家保存的位置快照
     */
    public static class SavedPosition {
        public String dimension;
        public double x;
        public double y;
        public double z;
        public float yaw;
        public float pitch;
        public long lastSeen;

        public SavedPosition() {
        }

        public SavedPosition(String dimension, double x, double y, double z, float yaw, float pitch, long lastSeen) {
            this.dimension = dimension;
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
            this.lastSeen = lastSeen;
        }
    }
}
