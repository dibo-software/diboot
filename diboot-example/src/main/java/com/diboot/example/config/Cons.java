package com.diboot.example.config;

import com.diboot.example.vo.DepartmentVO;
import com.diboot.example.vo.OrganizationVO;
import com.diboot.example.vo.PositionVO;

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
        ONE,    //一级图标
        TWO     //二级图标
    }

    public static Map<String, Map<String, String>> ICON = new HashMap(){{
        //组织
        put(OrganizationVO.class.getSimpleName(), new HashMap(){{
                    put(TREE_ICON_LEVEL.ONE.name(), "gold");
                    put(TREE_ICON_LEVEL.TWO.name(), "bank");
                }}
        );
        //部门
        put(DepartmentVO.class.getSimpleName(), new HashMap(){{
                    put(TREE_ICON_LEVEL.ONE.name(), "gold");
                    put(TREE_ICON_LEVEL.TWO.name(), "bank");
                }}
        );
        //职位
        put(PositionVO.class.getSimpleName(), new HashMap(){{
                    put(TREE_ICON_LEVEL.ONE.name(), "gold");
                    put(TREE_ICON_LEVEL.TWO.name(), "bank");
                }}
        );
    }};
}
