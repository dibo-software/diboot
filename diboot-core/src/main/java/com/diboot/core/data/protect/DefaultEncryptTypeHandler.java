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
package com.diboot.core.data.protect;

import com.diboot.core.exception.InvalidUsageException;
import com.diboot.core.util.ContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 加密，解密 Mybatis TypeHandler实现
 *
 * @author JerryMa
 * @version v3.1.1
 * @date 2023/10/10
 */
@Slf4j
public class DefaultEncryptTypeHandler extends BaseTypeHandler<String> {

    private DataEncryptHandler dataEncryptHandler;

    /**
     * 暴露这个方法，以便BindQuery查询时调用以加密后匹配密文
     * @return
     */
    protected DataEncryptHandler getDataEncryptHandler() {
        if(this.dataEncryptHandler == null) {
            this.dataEncryptHandler = ContextHolder.getBean(DataEncryptHandler.class);
            if(this.dataEncryptHandler == null) {
                throw new InvalidUsageException("exception.invalidUsage.defaultEncryptTypeHandler.getDataEncryptHandler.message");
            }
        }
        return this.dataEncryptHandler;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        String encryptedValue = getDataEncryptHandler().encrypt(parameter);
        ps.setString(i, encryptedValue);
        log.debug("字段保护 - 存储前加密：{} -> {}", parameter, encryptedValue);
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return decryptValue(value);
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return decryptValue(value);
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return decryptValue(value);
    }

    protected String decryptValue(String value) {
        String decryptedValue = getDataEncryptHandler().decrypt(value);
        log.debug("字段保护 - 读取后解密：{} -> {}", value, decryptedValue);
        return decryptedValue;
    }
}
