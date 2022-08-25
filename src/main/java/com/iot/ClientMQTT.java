package com.iot;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttWireMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Date;


public class ClientMQTT {
    public static final String HOST = "tcp://47.106.221.121:1883";
    public static final String[] TOPIC = {"MQTTpi1","MQTTpi2"};
    public static final int[] QOS = {1,1};
    private static final String ClientId = "Client";
    private static MqttClient client;
    private static MqttConnectOptions options;
    private static String result1;
    private static String result2;
    private static long count = 0;
    private static String temp;

    private void start() {
        try {
            // host为主机名，
            // clientid即连接MQTT的客户端ID，一般以唯一标识符表示
            // MemoryPersistence设置clientid的保存形式，默认为以内存保存
            client = new MqttClient(HOST, ClientId, new MemoryPersistence());
            // MQTT的连接设置
            options = new MqttConnectOptions();
            // 设置是否清空session,
            // 这里如果设置为false表示服务器会保留客户端的连接记录
            // 这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(false);
            // 设置连接的用户名
            //options.setUserName(userName);
            // 设置连接的密码
            //options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(10);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(20);
            // 设置回调
            //client.setCallback(new PushCallback());
            //MqttTopic topic = client.getTopic(TOPIC);
            // setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知消息
            //options.setWill(TOPIC, "close".getBytes(), 1, true);
            client.connect(options);
            // 订阅消息
            client.subscribe(TOPIC,QOS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static class PushCallback1 implements MqttCallback {
        public void connectionLost(Throwable cause) {
            // 连接丢失后，一般在这里面进行重连
            System.out.println("连接断开，可以做重连");
        }

        public void deliveryComplete(IMqttDeliveryToken token) {

        }

        public void messageArrived(String topic, MqttMessage message) throws Exception {
            System.out.println(new Date());
            // subscribe后得到的消息会执行到这里面
            if(count % 2 != 0){
                count = 0;
                System.out.println("接收消息主题1 : " + topic);
                System.out.println("接收消息Qos1 : " + message.getQos());
                result1 = new String(message.getPayload());
                System.out.println("接收消息内容1 : " + result1);
                System.out.println("==========================================================");
            }else{
                System.out.println("接收消息主题2 : " + topic);
                System.out.println("接收消息Qos2 : " + message.getQos());
                result2 = new String(message.getPayload());
                System.out.println("接收消息内容2 : " + result2);
                System.out.println("==========================================================");
            }
            System.out.println(count++);
        }
    }


    public static void publish(int qos, boolean retained, String topic, String pushMessage) {
        MqttMessage message = new MqttMessage();
        message.setQos(qos);
        message.setRetained(retained);
        message.setPayload(pushMessage.getBytes());
        MqttTopic mTopic = client.getTopic(topic);
        if (null == mTopic) {
            System.out.println("topic：" + topic + " 不存在");
        }
        MqttDeliveryToken token;
        try {
            token = mTopic.publish(message);
            token.waitForCompletion();

            if (!token.isComplete()) {
                System.out.println("消息发送成功");
            }
        } catch (MqttPersistenceException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

     public static void main(String[] args) throws MqttException {
         ClientMQTT client1 = new ClientMQTT();
         client1.start();
         client.setCallback(new PushCallback1());
         MqttMessage message = new MqttMessage();
         MqttTopic mqttTopic = client.getTopic("Java");
         message.setQos(1);
         message.setRetained(true);
         for (int i = 0; i < 10; i++) {
             message.setPayload(("hello"+i).getBytes());
             MqttDeliveryToken token = mqttTopic.publish(message);
             System.out.println(i);
             //token.waitForCompletion();
         }
     }
 }