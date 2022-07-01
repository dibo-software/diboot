/*
 * Copyright (c) 2015-2021, www.dibo.ltd (service@dibo.ltd).
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

/**
 * 消息数据字典等常量定义
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/19
 * @Copyright © diboot.com
 */
public class Cons extends com.diboot.core.config.Cons {

    /**
     * 数据字典类型定义
     */
    public enum MESSAGE_STATUS {
        // 发送中 和 异常 是所有信息都具有的状态；
        /**
         * 发送中
         */
        PENDING("待发送"),
        /**
         * 异常
         */
        FAILED("发送失败"),
        // 如果是短信、邮件则送达表示发送成功
        /**
         * 签收
         */
        DELIVERY("已送达"),
        /**
         * 已读
         */
        READ("已读");

        private String label;
        MESSAGE_STATUS(String label){
            this.label = label;
        }

        public String label(){
            return label;
        }
    }

    /**
     * 消息发送通道
     */
    public enum MESSAGE_CHANNEL {
        SMS("短信"),
        SYS_MSG("系统消息"),
        WEBSOCKET("站内信"),
        EMAIL("邮件");

        private String label;
        MESSAGE_CHANNEL(String label){
            this.label = label;
        }

        public String label(){
            return label;
        }
    }

}