package com.seater.smartmining.quartz.job;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.enums.ProjectCarStatus;
import com.seater.smartmining.mqtt.DeviceMessageHandler;
import com.seater.smartmining.mqtt.MqttSender;
import com.seater.smartmining.mqtt.domain.CarStatusReply;
import com.seater.smartmining.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/25 0025 16:19
 */
@Slf4j
@Component
@EnableScheduling
@Configuration
public class SlagCarStatusJob extends QuartzJobBean {

    @Autowired
    MqttSender mqttSender;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            //DeviceMessageHandler handler = (DeviceMessageHandler) SpringUtils.getBean("deviceMessageHandler");
            JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
            String cmdInd = jobDataMap.getString("cmdInd");
            String topic = jobDataMap.getString("topic");
            Long pktId = jobDataMap.getLong("pktId");
            Long carId = jobDataMap.getLongValue("carId");
            String carCode = jobDataMap.getString("carCode");
            Integer status = jobDataMap.getInt("status");
            String statusName = jobDataMap.getString("statusName");
            Long projectId = jobDataMap.getLong("projectId");
            CarStatusReply reply = new CarStatusReply();
            reply.setCmdInd(cmdInd);
            reply.setProjectId(projectId);
            reply.setProjectID(projectId);
            reply.setPktID(pktId);
            reply.setCarCode(carCode);
            reply.setStatus(status);
            reply.setStatusName(statusName);
            reply.setMessage("发送成功");
            mqttSender.sendDeviceReply(topic, reply);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
