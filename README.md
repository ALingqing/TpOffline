# TpOffline 模组（Minecraft 1.21.10）

传送到离线玩家的最后下线位置。本分支（`mc-1.21.10`）为 **Fabric** 平台。

## 功能

- `/tpo <玩家名>` —— 传送到指定玩家最后所在位置
  - 目标在线：直接传送到其当前位置
  - 目标离线：传送到其**最后下线位置**（跨维度自动切换）
- 玩家下线、服务器关闭时自动记录位置，数据存在每个存档目录下 `tpoffline/positions.json`

## 支持平台

| 平台 | Minecraft 版本 |
| --- | --- |
| Fabric | 1.21.10（Fabric Loader ≥ 0.16.0，Fabric API 任意版本） |

## 使用要求

| 项目 | 要求 |
| --- | --- |
| Minecraft | 1.21.10 |
| 权限 | OP（2 级权限），与 `/tp` 一致 |
| Java | 21 |

## 安装

1. 安装 [Fabric Loader](https://fabricmc.net/use/installer/) 1.21.10
2. 下载本模组 jar 和 [Fabric API](https://modrinth.com/mod/fabric-api)
3. 放入 `mods/` 文件夹，启动游戏

## 指令

```
/tpo <玩家名>
```

- 只支持**服务器 OP** 使用（2 级权限，和 `/tp` 一样）
- 提示"找不到下线位置记录"说明该玩家从未下线过（或数据被删除）

## 数据存储

每个世界存档独立存储：

```
<存档目录>/tpoffline/positions.json
```

删除该文件即可清空所有记录。

## 本地构建

```bash
./gradlew -p fabric build
```

产物在 `fabric/build/libs/` 目录下。

## 版本分支

本仓库按 Minecraft 版本维护分支（`mc-<版本>`），每个分支只包含该版本适用的平台。
3. 解压后得到模组 jar

也可以在 Actions 页面手动触发 **Run workflow** 重新构建。

## 开发

```
src/main/java/com/chenray/tpoffline/
├── TpOffline.java             # 模组主入口，事件注册
├── TpOfflineCommand.java      # /tpo 指令
└── PlayerPositionManager.java # 位置记录读写（JSON）
```
