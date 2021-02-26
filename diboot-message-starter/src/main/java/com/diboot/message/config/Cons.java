/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.message.config;

import com.diboot.core.config.BaseConfig;

/**
 * 消息数据字典等常量定义
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/19
 */
public class Cons extends com.diboot.core.config.Cons {

    /**
     * 数据字典类型定义
     */
    public enum MESSAGE_STATUS {
        // 发送中 和 异常 是所有短信都具有的状态
        /**
         * 发送中
         */
        SENDING("发送中", "SENDING"),
        /**
         * 异常
         */
        EXCEPTION("发送异常", "EXCEPTION"),
        // 如果是短信、邮件则送达表示发送成功
        /**
         * 签收
         */
        DELIVERY("已送达", "DELIVERY"),
        // 如果是站内信，那么状态是已读、未读
        /**
         * 未读
         */
        UNREAD("未读", "UNREAD"),
        /**
         * 已读
         */
        READ("已读", "READ");

        private String itemName;

        private String itemValue;

        MESSAGE_STATUS(String itemName, String itemValue) {
            this.itemName = itemName;
            this.itemValue = itemValue;
        }

        public String getItemName() {
            return itemName;
        }

        public String getItemValue() {
            return itemValue;
        }
    }

    /**
     * 消息发送通道
     */
    public enum MESSAGE_CHANNEL {
        WEBSOCKET("站内信", "WEBSOCKET"),
        NOTE("短信", "NOTE"),
        EMAIL("邮件", "EMAIL");

        private String itemName;

        private String itemValue;

        MESSAGE_CHANNEL(String itemName, String itemValue) {
            this.itemName = itemName;
            this.itemValue = itemValue;
        }

        public String getItemName() {
            return itemName;
        }

        public String getItemValue() {
            return itemValue;
        }
    }

}