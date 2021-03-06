package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectLoadLogDaoI;
import com.seater.smartmining.entity.ProjectLoadLog;
import com.seater.smartmining.entity.repository.ProjectLoadLogRepository;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class ProjectLoadLogDaoImpl implements ProjectLoadLogDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectLoadLogRepository projectLoadLogRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectloadlog:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}


    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectLoadLog> query(Specification< ProjectLoadLog> spec, Pageable pageable) {
        return projectLoadLogRepository.findAll(spec, pageable);
    }

    @Override
    public Page< ProjectLoadLog> query(Specification< ProjectLoadLog> spec) {
        return projectLoadLogRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectLoadLog> query(Pageable pageable) {
        return projectLoadLogRepository.findAll(pageable);
    }

    @Override
    public Page< ProjectLoadLog> query() {
        return projectLoadLogRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public  ProjectLoadLog get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj,  ProjectLoadLog.class);
        }
        if(projectLoadLogRepository.existsById(id))
        {
            ProjectLoadLog log = projectLoadLogRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectLoadLog save( ProjectLoadLog log) throws IOException {
        ProjectLoadLog log1 = projectLoadLogRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectLoadLogRepository.deleteById(id);
    }

    @Override
    public List< ProjectLoadLog> getAll() {
        return projectLoadLogRepository.findAll();
    }

    @Override
    public Date getMaxUnloadDateByCarCode(String carCode) {
        List<Date> list = projectLoadLogRepository.getMaxUnloadDateByCarCode(carCode);
        if(list == null || list.size() <= 0)
            return null;

        return list.get(0);
    }

    @Override
    public List<Map> getMachineCountByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectLoadLogRepository.getMachineCountByProjectIdAndTime(projectId, startTime, endTime);
    }
}
