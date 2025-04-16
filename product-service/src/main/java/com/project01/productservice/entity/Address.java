package com.project01.productservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum Address{
    HA_NOI("Hà Nội"),
    TP_HCM("TP Hồ Chí Minh"),
    DA_NANG("Đà Nẵng");

    private String city;
}
