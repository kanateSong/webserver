package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectMqttParamsRequestDaoI;
import com.seater.smartmining.entity.ProjectMqttParamsRequest;
import com.seater.smartmining.entity.repository.ProjectMqttParamsRequestRepository;
import com.seater.user.dao.GlobalSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/2 0002 22:36
 */
@Component
public class ProjectMqttParamsRequestDaoImpl implements ProjectMqttParamsRequestDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ProjectMqttParamsRequestRepository projectMqttParamsRequestRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectmqttparamsrequest:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectMqttParamsRequest get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectMqttParamsRequest.class);
        }
        if(projectMqttParamsRequestRepository.existsById(id)){
            ProjectMqttParamsRequest log = projectMqttParamsRequestRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectMqttParamsRequest save(ProjectMqttParamsRequest log) throws JsonProcessingException {
        ProjectMqttParamsRequest log1 = projectMqttParamsRequestRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectMqttParamsRequestRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectMqttParamsRequest> query() {
        return projectMqttParamsRequestRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectMqttParamsRequest> query(Specification<ProjectMqttParamsRequest> spec) {
        return projectMqttParamsRequestRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectMqttParamsRequest> query(Pageable pageable) {
        return projectMqttParamsRequestRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectMqttParamsRequest> query(Specification<ProjectMqttParamsRequest> spec, Pageable pageable) {
        return projectMqttParamsRequestRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectMqttParamsRequest> getAll() {
        return projectMqttParamsRequestRepository.findAll();
    }
}
