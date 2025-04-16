package com.project01.orderservice.utils;

import com.project01.orderservice.dto.account.AccountDTO;
import java.time.LocalDateTime;

public class ReferenceOrder {

    public static String getReferenceOrder(AccountDTO account) {
        LocalDateTime today = LocalDateTime.now();
        return "ORD-"+today.getYear()+today.getMonthValue()+today.getDayOfMonth()+today.getHour()+today.getMinute()+today.getSecond()+"-"+account.getId();
    }
}
