package com.project01.cartservice.services;

import java.util.concurrent.TimeUnit;

public interface RedisService {
   void saveData(String key,Object value);
   <T> T getData(String key);
   void deleteData(String key);
   void saveDataOnTime(String key,Object value,long timeout, TimeUnit unit);
   boolean existsKey(String key);
}
