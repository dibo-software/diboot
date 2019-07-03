package com.diboot.example.test.sql;

import com.diboot.core.util.SqlExecutor;
import com.diboot.example.test.ApplicationTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExecuteSqlTest extends ApplicationTest {

    @Test
    public void testExecuteShowTableInfo() throws Exception{
        String sql = "";
        boolean success = SqlExecutor.executeUpdate(sql, null);
        Assert.assertTrue(success);
    }
}
