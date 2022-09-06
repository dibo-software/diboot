package com.diboot.core.util.sql;

import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 不需要回调的处理器
 */
public class DoNothingCallbackHandler implements RowCallbackHandler {
    @Override
    public void processRow(ResultSet rs) throws SQLException {
    }
}
