# RinDeath
A minecraft plugin for 1.16.5.
## 中文介绍
### 插件功能
在这个插件中，你可以通过设置启用坟墓选项。在非观察者玩家死亡后，会生成一个坟墓(通过CustomModelData实现)。在玩家死亡后，死亡玩家会变为观察者模式并在坟墓附近游荡。当有玩家复活死亡玩家时，死亡玩家可以原地复活。否则当复活时间过后，玩家将在出生点复活。
### 插件特色
可以设定三种剩余复活时间提示模式

可以选择使用指令复活或是使用物品复活
### 权限设置
```
rindeath.cmd.respawn          -使用/rindeath respawn命令的权限
rindeath.cmd.revive           -使用/rindeath revive命令的权限
rindeath.cmd.reviveitem       -使用/rindeath reviveitem命令的权限
```
### 发现Bug或有改进意见请前往[Issues](https://github.com/YakumoReddo/RinDeath/issues)提出