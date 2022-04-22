package com.nagisazz.accessadd.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Record {
    /**
     * 
     */
    private Long id;

    /**
     * 访问地址
     */
    private String url;

    /**
     * 设定访问次数
     */
    private Integer number;

    /**
     * 实际访问次数
     */
    private Integer reality;

    /**
     * 状态，1：访问完成，2：正在访问
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 结束访问时间
     */
    private LocalDateTime endTime;
}