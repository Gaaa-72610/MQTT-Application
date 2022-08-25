package com.iot.config;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


@Component  // 注入到Spring容器中
public class MqttConfig implements ApplicationRunner {
    // MQTT服务端 地址:端口号
    public static final String HOST = "tcp://47.106.221.121:1883";
    // MQTT客户端名称
    public static final String ClientId = "Client";
    // MQTT客户端 通过客户端创建连接
    public static MqttClient client;
    // MQTT连接参数
    public static MqttConnectOptions options;

    //连接MQTT服务器
    private void connect() {
        try {
            // 创建客户端
            client = new MqttClient(HOST, ClientId, new MemoryPersistence());
            // MQTT的连接设置
            options = new MqttConnectOptions();
            // 设置是否清空session,
            // 如果设置为false表示服务器会保留客户端的连接记录
            // 设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(false);
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(10);
            // 设置会话心跳时间 单位为秒
            options.setKeepAliveInterval(20);
            // 创建连接
            client.connect(options);
        } catch (Exception e) {
            System.out.println("MQTT连接异常：" + e);
        }
    }

    // 获取客户端
    public MqttClient getClient() {
        return this.client;
    }

    // 继承ApplicationRunner实现run(ApplicationArguments args)方法
    // 目的是在SpringBoot整个应用启动之后运行此段程序
    @Override
    public void run(ApplicationArguments args) {
        // 连接
        this.connect();
        System.out.println("初始化并连接MQTT......");
    }
}