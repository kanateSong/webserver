package com.seater.smartmining.controller;

import com.seater.smartmining.entity.ProjectRunningTrajectoryLog;
import com.seater.smartmining.service.ProjectRunningTrajectoryLogServiceI;
import com.seater.smartmining.utils.params.Result;
import com.seater.user.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/12/17 0017 10:06
 */
@RestController
@RequestMapping("/api/projectRunningTrajectoryLog")
public class ProjectRunningTrajectoryLogController {

    @Autowired
    private ProjectRunningTrajectoryLogServiceI projectRunningTrajectoryLogServiceI;

    @RequestMapping("/query")
    public Result query(HttpServletRequest request, Integer current, Integer pageSize){
        Long projectId = CommonUtil.getProjectId(request);
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        Specification<ProjectRunningTrajectoryLog> spec = new Specification<ProjectRunningTrajectoryLog>() {
            List<Predicate> list = new ArrayList<Predicate>();
            @Override
            public Predicate toPredicate(Root<ProjectRunningTrajectoryLog> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if(projectId != null && projectId != 0)
                    list.add(criteriaBuilder.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId"))));
                query.orderBy(criteriaBuilder.desc(root.get("id").as(Long.class)));
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return Result.ok(projectRunningTrajectoryLogServiceI.query(spec, PageRequest.of(cur, page)));
    }
}
