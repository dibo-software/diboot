package com.diboot.core.data.copy;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain = true)
public class CopyInfo {

    private String form;

    private String to;

    private boolean override;

    public CopyInfo(String from, String to, boolean override) {
        this.form = from;
        this.to = to;
        this.override = override;
    }

}