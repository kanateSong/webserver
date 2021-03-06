package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.VersionDaoI;
import com.seater.smartmining.entity.Version;
import com.seater.smartmining.entity.repository.VersionRepository;
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
 * @Date 2019/4/4 0004 10:49
 */
@Component
public class VersionDaoImpl implements VersionDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    VersionRepository versionRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:version:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public Version get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, Version.class);
        }
        if(versionRepository.existsById(id))
        {
            Version log = versionRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public Version save(Version log) throws JsonProcessingException {
        Version log1 = versionRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        versionRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<Version> query() {
        return versionRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<Version> query(Specification<Version> spec) {
        return versionRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<Version> query(Pageable pageable) {
        return versionRepository.findAll(pageable);
    }

    @Override
    public Page<Version> query(Specification<Version> spec, Pageable pageable) {
        return versionRepository.findAll(spec, pageable);
    }

    @Override
    public List<Version> getAll() {
        return versionRepository.findAll();
    }
}
