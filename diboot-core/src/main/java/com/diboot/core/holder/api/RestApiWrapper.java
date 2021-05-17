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
package com.diboot.core.holder.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * 接口定义类wrapper
 * @author Mazhicheng
 * @version v2.0
 * @date 2020/02/28
 */
@Getter @Setter
public class RestApiWrapper implements Serializable {
    private static final long serialVersionUID = 4355669647175573884L;

    public RestApiWrapper(){}

    public RestApiWrapper(String className, String title, Annotation annotation){
        this.className = className;
        this.title = title;
        this.annotation = annotation;
    }

    /**
     * 类名
     */
    private String className;

    /**
     * 类别标题
      */
    private String title;

    /**
     * 子节点
     */
    private List<RestApi> children;

    /**
     * 接口类上的注解
     */
    @JsonIgnore
    private Annotation annotation;

    /**
     * 添加节点
     * @param child
     */
    public void addChild(RestApi child) {
        if(this.children == null){
            this.children = new ArrayList<>();
        }
        this.children.add(child);
    }

}