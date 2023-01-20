/*
 * Copyright (c) 2015-2029, www.dibo.ltd (service@dibo.ltd).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.diboot.core.util;

import com.diboot.core.config.BaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * Snowflake ID生成器控件
 * @author mazc@dibo.ltd
 * @version v3.0
 * @date 2023/01/11
 *
 */
public class IdGenerator {
    private static final Logger logger = LoggerFactory.getLogger(IdGenerator.class);
    private static long workerId = BaseConfig.getWorkerId(); // 当前程序id标识
    private static long dataCenterId = BaseConfig.getDataCenterId(); // 数据中心id标识
    private static long sequence = 0L;
    private static long workerIdBits = 5L;
    private static long datacenterIdBits = 5L;
    private static long sequenceBits = 12L;

    private static long workerIdShift = sequenceBits;
    private static long datacenterIdShift = sequenceBits + workerIdBits;
    private static long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private static long sequenceMask = -1L ^ (-1L << sequenceBits);

    private static long lastTimestamp = -1L;
    /**
     * 时间戳差值
     */
    private static final long startTimestamp = 1352037060000L;

    /***
     * 生成下一个id
     * @return
     */
    public static synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            logger.error("服务器时钟错误！");
            throw new RuntimeException("服务器时钟错误，无法生成ID！");
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        return (timestamp << timestampLeftShift) | (dataCenterId << datacenterIdShift) | (workerId << workerIdShift) | sequence;
    }

    /***
     * 日期时间相关处理
     * @return
     */
    private static long timeGen() {
        return System.currentTimeMillis() - startTimestamp;
    }
    private static long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 生成String类型的雪花id
     * @return
     */
    public static synchronized String nextIdStr() {
        return S.valueOf(nextId());
    }

    /**
     * 生成String类型的UUID
     * @return
     */
    public static String newUUID() {
        return S.newUuid();
    }

}