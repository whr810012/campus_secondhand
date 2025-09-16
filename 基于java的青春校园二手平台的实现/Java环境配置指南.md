# Java环境配置指南

## 当前状态
- ✅ 前端服务运行正常 (http://localhost:8080)
- ❌ 后端Java环境未配置
- ❌ Maven环境未配置

## 需要安装的软件

### 1. Java JDK 8 或更高版本
**推荐下载地址：**
- Oracle JDK: https://www.oracle.com/java/technologies/downloads/
- OpenJDK: https://adoptium.net/

**安装步骤：**
1. 下载适合Windows的JDK安装包
2. 运行安装程序，按默认设置安装
3. 配置环境变量：
   - 添加 `JAVA_HOME` 环境变量，值为JDK安装路径（如：`C:\Program Files\Java\jdk-11.0.x`）
   - 在 `PATH` 环境变量中添加 `%JAVA_HOME%\bin`

### 2. Apache Maven
**下载地址：** https://maven.apache.org/download.cgi

**安装步骤：**
1. 下载Maven二进制压缩包
2. 解压到合适的目录（如：`C:\Program Files\Apache\maven`）
3. 配置环境变量：
   - 添加 `MAVEN_HOME` 环境变量，值为Maven解压路径
   - 在 `PATH` 环境变量中添加 `%MAVEN_HOME%\bin`

## 验证安装
安装完成后，重新打开命令行窗口，运行以下命令验证：

```bash
# 验证Java安装
java -version

# 验证Maven安装
mvn -version
```

## 启动后端服务
环境配置完成后，在项目根目录运行：

```bash
mvn spring-boot:run
```

## 数据库配置
后端服务还需要配置数据库连接，请检查 `src/main/resources/application.yml` 或 `application.properties` 文件中的数据库配置。

## 常见问题
1. **环境变量不生效**：重新打开命令行窗口
2. **Maven下载依赖慢**：配置国内镜像源
3. **端口冲突**：检查8080端口是否被占用

---

配置完成后，前后端服务都将正常运行，可以进行联调测试。