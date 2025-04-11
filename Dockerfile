FROM ubuntu:latest
LABEL authors="admin"
# 安装基础工具并更新系统
RUN apt-get update && \
    apt-get install -y wget && \
    apt-get clean

# 从网络下载 Liberica JDK .deb 包到镜像
RUN wget -O /tmp/bellsoft-jdk21.0.6+10-linux-amd64.deb \
    https://download.bell-sw.com/java/21.0.6+10/bellsoft-jdk21.0.6+10-linux-amd64.deb

# 安装 Liberica JDK 21
RUN dpkg -i /tmp/bellsoft-jdk21.0.6+10-linux-amd64.deb || \
    (apt-get update && apt-get install -f -y) && \
    rm -f /tmp/*.deb

# 验证安装
RUN java -version

# 创建应用目录
RUN mkdir -p /app
WORKDIR /app

# 复制应用 JAR 文件到容器
COPY target/CloudDrive-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8080

# 运行命令
ENTRYPOINT ["java","-jar","/app/app.jar"]
