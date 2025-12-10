package com.zcpbysj.campusidletrade_server.entity.vo.food;

import lombok.Data;

import java.time.LocalTime;

/**
 * 食堂VO
 */
@Data
public class CanteenVO {
    private Long id;
    private String name;
    private String location;
    private String image;
    private LocalTime openTime;
    private LocalTime closeTime;
    private Integer queueCount;
    private Integer totalSeats;
    private Integer availableSeats;
    private String status;
    private Integer estimatedWaitMinutes;
}
