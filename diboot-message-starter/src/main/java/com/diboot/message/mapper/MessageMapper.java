package com.diboot.message.mapper;


import com.diboot.core.mapper.BaseCrudMapper;
import com.diboot.message.entity.Message;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息Mapper
 *
 * @author : uu
 * @version : v2.0
 * @Date 2021/2/25  09:39
 */
@Mapper
public interface MessageMapper extends BaseCrudMapper<Message> {

}

