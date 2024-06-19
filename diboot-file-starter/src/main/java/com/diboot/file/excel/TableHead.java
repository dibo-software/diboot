package com.diboot.file.excel;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * TableHead excel表头
 * @author JerryMa
 * @version v3.0.0
 * @date 2022/11/14
 * Copyright © diboot.com
 */
@Getter
@Setter
public class TableHead implements Serializable {
    private static final long serialVersionUID = -6384590232454767380L;

    private String key;
    private String title;
    private List<TableHead> children;
}
