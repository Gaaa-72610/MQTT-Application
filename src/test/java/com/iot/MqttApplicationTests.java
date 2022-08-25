package com.iot;

import com.iot.config.MqttConfig;
import com.iot.mapper.MqttMapper;
import com.iot.pojo.HTData;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
class MqttApplicationTests {
    @Autowired
    MqttMapper mqttMapper;

    @Autowired
    MqttConfig mqttConfig;

    @Test
    void contextLoads() {
        HTData htData = new HTData();
        htData.setRecvTime(new Date());
        htData.setRecvData1("111");
        htData.setRecvData2("222");
        mqttMapper.insertData(htData);
    }

    @Test
    void test1(){

    }
}
