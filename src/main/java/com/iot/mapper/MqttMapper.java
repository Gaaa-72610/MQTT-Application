package com.iot.mapper;

import com.iot.pojo.HTData;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface MqttMapper {
    // 定义数据存库函数
    int insertData(HTData htData);
}

