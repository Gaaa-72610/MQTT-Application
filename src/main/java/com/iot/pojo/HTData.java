package com.iot.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HTData implements Serializable {
    private Long id;
    private Date recvTime;
    private String recvData1;
    private String recvData2;
}


