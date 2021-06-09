/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.iam.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.diboot.core.mapper.BaseCrudMapper;
import com.diboot.iam.entity.IamUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.io.Serializable;

/**
* 系统用户Mapper
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-17
*/
@Mapper
public interface IamUserMapper extends BaseCrudMapper<IamUser> {

    /***
     * 通过ID撤回逻辑删除
     * @param id
     * @return
     */
    @Update("UPDATE `iam_user` SET is_deleted=0 WHERE id=#{id}")
    int canceledDeleteById(Serializable id);

    /***
     * 通过org层级的排序来获取用户分页数据
     * @param page
     * @param queryWrapper
     * @return
     */
    @Select("select u.* from iam_user u left join iam_org o on u.org_id=o.id ${ew.customSqlSegment} order by o.depth asc, o.sort_id desc, o.id desc, u.id desc")
    IPage<IamUser> selectPageSortByOrg(IPage page, @Param("ew") Wrapper<IamUser> queryWrapper);
}

