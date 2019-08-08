package com.diboot.example.config;

import com.diboot.example.entity.Department;
import com.diboot.example.entity.Organization;

import java.util.HashMap;
import java.util.Map;

/*
* 常量类
* */
public class Cons {
    /*
    * 树图标常量
    * */
    public static enum TREE_ICON_LEVEL{
        ONE,
        TWO
    }

    public static Map<String, Map<String, String>> ICON = new HashMap(){{
        put(Organization.class.getSimpleName(), new HashMap(){{
                    put(TREE_ICON_LEVEL.ONE.name(), "");
                    put(TREE_ICON_LEVEL.TWO.name(), "");
                }}
        );
        put(Department.class.getSimpleName(), new HashMap(){{
                    put(TREE_ICON_LEVEL.ONE.name(), "");
                    put(TREE_ICON_LEVEL.TWO.name(), "");
                }}
        );
    }};
}
