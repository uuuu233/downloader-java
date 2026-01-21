# 悠悠下载器

## 准备工作
- 搭建 `alist`
- 提供获取 `文件元信息` http接口

## 文件元信息
`文件元信息` 是描述文件的基本信息, 属性如下

```properties
# 文件的下载地址
url = http://baidu.com?file=11.zip
# 下载的文件名, 建议尾部加个版本号
filename = Game_v122.zip
# 安装文件夹名
directoryName = Game2027
# 快捷方式名
shortcutName = uumxd
# 默认安装路径, 这里最后一个文件夹要和 directoryName 保持一致
defaultInstallPath=D:\\Game\\Game2027
# 主程序的名字, 创建快捷方式用
applicationName = uumxd.exe
# 自定义 headers, 可空, 多个 header 用 ^ 分隔
# headers = User-Agent: pan.com
```

站在 `下载器` 视角: 我在启动后会访问一个 `url` 地址, 把响应信息当作 `文件元信息`

我要从 `文件元信息` 中获取下载文件的 `url`, 并下载文件

## 下载资源的要求

压缩格式必须为 `zip`

压缩包内资源禁止套娃, 即让压缩包的根目录就是所有文件

下载器会创建一个文件夹(`文件元信息` 的 `directoryName` 作为文件夹名字), 并把资源解压到这个文件夹下


## 卖萌
![296e02d32e5e897284da1c3757649697](https://github.com/user-attachments/assets/fdfd6110-c71a-4587-9098-ca09ededc290)
