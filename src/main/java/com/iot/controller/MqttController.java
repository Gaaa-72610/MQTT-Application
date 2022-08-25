package com.iot.controller;

import com.iot.config.MqttConfig;
import com.iot.mapper.MqttMapper;
import com.iot.pojo.HTData;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Controller
@CrossOrigin
public class MqttController{
    //订阅树莓派发布的两个主题，分别为节点1和节点2的数据
    private static final String[] TOPIC = {"MQTTpi1","MQTTpi2"};
    //qos=1代表Sender发送的一条消息，Receiver至少能收到一次
    private static final int[] QOS = {1,1};
    //整合节点1、节点2数据提供给前端
    private static String result;
    //节点1数据
    private static String result1;
    //节点2数据
    private static String result2;
    private static int count = 0;
    private MqttMessage message;

    //Mapper接口注入进行数据存库操作
    @Autowired
    private MqttMapper mqttMapper;

    //注入MQTT配置类，对连接设置消息回调和订阅主题
    @Autowired
    private MqttConfig mqttConfig;
    private MqttTopic mqttTopic1;
    private MqttTopic mqttTopic2;


    MqttCallback mc = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {
            result = "MQTT连接已断开";
            System.out.println(result);
        }
        //接收消息
        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            if(count % 2 == 0){
                count = 0;
                System.out.println("接收消息主题1 : " + topic);
                System.out.println("接收消息Qos1 : " + message.getQos());
                //节点1数据
                String res1 = new String(message.getPayload());
                System.out.println("==================="+res1);
                if (res1 != "null"){
                    result1 = new String(message.getPayload());
                }
                //result1 = new String(message.getPayload());
                System.out.println("接收消息内容1 : " + result1);
                System.out.println("==========================================================");
            }else {
                System.out.println("接收消息主题2 : " + topic);
                System.out.println("接收消息Qos2 : " + message.getQos());
                //节点2数据
                String res2 = new String(message.getPayload());
                System.out.println("==================="+res2);
                if (res2 != "null"){
                    result2 = new String(message.getPayload());
                }
                System.out.println("接收消息内容2 : " + result2);
                System.out.println("==========================================================");
            }
            System.out.println(count++);
        }
        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    };


    // 初始化操作，提供/init接口给前端
    // 只有访问此接口后才会真正进行消息接收
    @GetMapping("/init")
    @ResponseBody
    public String init(){
        MqttClient client = mqttConfig.getClient();
        // 设置回调函数，通过回调函数进行消息接收
        client.setCallback(mc);

        //初始化发布主题内容
        message = new MqttMessage();
        message.setQos(1);
        message.setRetained(true);
        mqttTopic1 = client.getTopic("Node1");
        mqttTopic2 = client.getTopic("Node2");
        try {
            // 订阅消息
            client.subscribe(TOPIC, QOS);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return "ok";
    }

    // 提供/test接口给前端进行数据展示
    @GetMapping("/test")
    @ResponseBody
    public String subscribe(){
        //节点1、节点2数据拼接
        result = result1 + "," + result2;
        System.out.println(result);
        //返回拼接好的数据
        return result;
    }

    //发布主题 数据下行 控制pyb灯亮灭
    @RequestMapping("/node1/{node1}")
    @ResponseBody
    public String publish1(@PathVariable(value = "node1") String node1){
        System.out.println(node1);
        String res = "node1:"+node1;
        if (node1.equals("0")){
            message.setPayload(res.getBytes());
        }else {
            message.setPayload(res.getBytes());
        }
        try {
            mqttTopic1.publish(message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return node1;
    }

    @RequestMapping("/node2/{node2}")
    @ResponseBody
    public String publish2(@PathVariable(value = "node2") String node2){
        System.out.println(node2);
        String res2 = "node2:"+node2;
        if (node2.equals("0")){
            message.setPayload(res2.getBytes());
        }else {
            message.setPayload(res2.getBytes());
        }
        try {
            mqttTopic2.publish(message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return node2;
    }


    //// 定时任务 每一秒进行一次数据存至MySQL数据库
    //@Scheduled(cron = "* * * * * ?")
    //public void dataToDB(){
    //    HTData htData = new HTData();
    //    // 节点1数据
    //    htData.setRecvData1(result1);
    //    // 节点2数据
    //    htData.setRecvData2(result2);
    //    // 当前时间
    //    htData.setRecvTime(new Date());
    //    // 当数据不为空时才进行存库
    //    if (result != null){
    //        System.out.println(new Date() + "存库成功！");
    //        mqttMapper.insertData(htData);
    //    }
    //}





    @GetMapping("/init111")
    //@ResponseBody
    public String init1(){
        MqttClient client = mqttConfig.getClient();
        client.setCallback(mc);
        // 订阅消息
        try {
            client.subscribe(TOPIC, QOS);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return "test";
    }

    // 提供/test接口给前端进行数据展示
    @GetMapping("/test111")
    @ResponseBody
    public String subscribe1(Model model){
        String[] strings1 = result1.split(",");
        String[] strings2 = result2.split(",");
        result = "节点1湿度:" + strings1[0] + ",温度:" + strings1[1] + "                  " + "节点2湿度:"
                + strings2[0] + ",温度:" + strings2[1];
        System.out.println(result);
        return result;
    }
}
