# what is afterLoader ?
afterLoader is Fast ice scorpion(跑得快的冰蝎)

# how run ?
## args
-XstartOnFirstThread

```
net.rebeyond.behinder.ui.Main

java -jar afterLoader.jar -cp net.rebeyond.behinder.utils.WebLogicPasswordDecryptor 'BNsVpgsCIH1SQeAoTRpAuAgmrQk18B6MhpXuF+D0Qx8Dxg9TpAMvGKhfQDBnygPIlPxAx0ZqqoQtZUNVdfvYyw==;' '{AES}XU+PAoMN3rp6Z6oGeAmB5XGuWNNNYM59DMipi5GbAPY=;{AES}J+1dgnx8m86JlmqVNfsLZoNq9DChLqTI4g2QaoHlwls='

```

# Demo
<img width="601" alt="image" src="https://user-images.githubusercontent.com/18223385/70731248-5be47500-1d41-11ea-9278-d9f172d7dc28.png">


# logs
- 2019-12-12 反编译，阅读源码、分析、确认其是否存在后门；添加了mac osX支持的jar
- 2019-12-12 优化了pass参数修改后报错、未编码问题
- 2019-12-12 优化了linux系统执行命令未考核支持管道的问题
- 2019-12-13 更新所有jar，确保其中不得暗藏后门；更新jar后图形类个别未向下兼容，手工添加AWTLayout BorderLayout
- 2019-12-13 修改默认密码pass为专用、高难度密码
- 2019-12-13 增加 net.rebeyond.behinder.utils.WebLogicPasswordDecryptor
- 2019-12-13 增加 bcpkix-jdk15on-164.jar
