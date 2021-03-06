package com.seater.smartmining.entity;

import com.seater.smartmining.enums.ProjectCarStatus;
import lombok.Data;

import javax.persistence.*;

/**
 * @Description:渣车排班方案模板
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/15 0015 10:21
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"projectId", "programmeId", "carId","carCode"}, name = "repeatCheck")})
@Data
public class ScheduleCarModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id = 0L;       //主键ID

    @Column
    public Long projectId = 0L;    //项目ID

    @Column
    private Long programmeId = 0L;      //方案ID

    @Column
    public Long carId = 0L;        //车辆ID

    @Column
    public String carCode = "";        //车辆编号

    @Column
    public String groupCode = "";      //分组编号

    @Column
    public Long carBrandId = 0L;       //车辆类型ID

    @Column
    public String carBrandName = "";       //车辆类型名称

    @Column
    public Long carModelId = 0L;       //车辆品牌ID

    @Column
    public String carModelName = "";       //车辆品牌名称

    @Column
    public Long carOwnerId = 0L;       //车主ID

    @Column
    public String carOwnerName = "";       //车主名称

    @Column
    public Boolean isVaild = true;             //是否有效

    @Column
    public Boolean fault = false;           //是否故障

    @Column
    @Enumerated(EnumType.ORDINAL)
    private ProjectCarStatus status = ProjectCarStatus.Unknow;
}
