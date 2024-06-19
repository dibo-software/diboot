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
package diboot.core.test.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.binding.Binder;
import com.diboot.core.binding.JoinsBinder;
import com.diboot.core.binding.QueryBuilder;
import com.diboot.core.binding.query.dynamic.ExtQueryWrapper;
import com.diboot.core.config.Cons;
import com.diboot.core.vo.Pagination;
import diboot.core.test.StartupApplication;
import diboot.core.test.binder.dto.DepartmentDTO;
import diboot.core.test.binder.dto.ProblemDTO;
import diboot.core.test.binder.dto.UserDTO;
import diboot.core.test.binder.entity.Department;
import diboot.core.test.binder.entity.Problem;
import diboot.core.test.binder.entity.StrIdTest;
import diboot.core.test.binder.entity.User;
import diboot.core.test.binder.service.DepartmentService;
import diboot.core.test.binder.service.ProblemService;
import diboot.core.test.binder.service.StrIdTestService;
import diboot.core.test.binder.service.UserService;
import diboot.core.test.binder.vo.DepartmentVO;
import diboot.core.test.config.SpringMvcConfig;
import org.apache.ibatis.jdbc.SQL;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * BindQuery测试
 * @author Mazc@dibo.ltd
 * @version v2.0.6
 * @date 2020/04/14
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SpringMvcConfig.class})
@SpringBootTest(classes = {StartupApplication.class})
public class TestJoinQuery {

    @Autowired
    DepartmentService departmentService;

    @Autowired
    UserService userService;

    @Autowired
    StrIdTestService strIdTestService;

    @Autowired
    ProblemService problemService;

    @Test
    public void testDateCompaire(){
        Department example = departmentService.list(null).get(0);
        DepartmentDTO departmentDTO = new DepartmentDTO();
        departmentDTO.setCreateTime(example.getCreateTime());
        departmentDTO.setCharacter(example.getCharacter());
        QueryWrapper<Department> queryWrapper = QueryBuilder.toQueryWrapper(departmentDTO);
        List<Department> list = departmentService.list(queryWrapper);
        Assert.assertTrue(list.size() >= 1);
    }

    @Test
    public void testInQuery(){
        List<Long> parentIds = new ArrayList<>();
        DepartmentDTO departmentDTO = new DepartmentDTO();
        departmentDTO.setParentIds(parentIds);
        QueryWrapper<Department> queryWrapper = QueryBuilder.toQueryWrapper(departmentDTO);
        System.out.println(queryWrapper.getExpression());
        List<Department> list = Binder.joinQueryList(queryWrapper, Department.class);
        Assert.assertTrue(list.size() > 1);

        parentIds.add(10001L);
        queryWrapper = QueryBuilder.toQueryWrapper(departmentDTO);
        list = Binder.joinQueryList(queryWrapper, Department.class);
        Assert.assertTrue(list.size() > 0);

    }

    @Test
    public void testSingleTableQuery(){
        Department entity = new Department();
        entity.setParentId("10001");
        entity.setName("测试组");
        entity.setOrgId("100001");

        QueryWrapper<Department> queryWrapper = QueryBuilder.toQueryWrapper(entity);
        System.out.println(queryWrapper.getExpression());
        List<Department> list = Binder.joinQueryList(queryWrapper, Department.class);
        Assert.assertTrue(list.size() == 1);
        Assert.assertTrue(queryWrapper.getSqlSegment().contains("parent_id"));
        Assert.assertTrue(queryWrapper.getSqlSegment().contains("name"));
        Assert.assertTrue(queryWrapper.getSqlSegment().contains("org_id"));

        List<String> fields = Arrays.asList("name", "orgId", "parentId");
        queryWrapper.clear();
        queryWrapper = QueryBuilder.toQueryWrapper(entity, fields);
        Assert.assertTrue(queryWrapper.getSqlSegment().contains("parent_id"));
        Assert.assertTrue(queryWrapper.getSqlSegment().contains("name"));
        Assert.assertTrue(queryWrapper.getSqlSegment().contains("org_id"));
        Assert.assertTrue(queryWrapper.getParamNameValuePairs().size() == fields.size());

        list = Binder.joinQueryList(queryWrapper, Department.class);
        Assert.assertTrue(list.size() == 1);
    }

    @Test
    public void testDynamicSqlQuery(){
        // 初始化DTO，测试不涉及关联的情况
        DepartmentDTO dto = new DepartmentDTO();
        //dto.setOrgName("");
        ExtQueryWrapper query = QueryBuilder.toDynamicJoinQueryWrapper(dto);
        List<Department> departmentList = query.queryList(Department.class);
        Assert.assertTrue(departmentList.size() > 3);

        dto.setParentId(10001L);

        // 验证 转换后的wrapper可以直接查询
        QueryWrapper<DepartmentDTO> queryWrapper = QueryBuilder.toQueryWrapper(dto);

        QueryWrapper<Department> deptQueryWrapper = new QueryWrapper<>();
        deptQueryWrapper.eq("parent_id", 10001L);
        List<Department> departments = departmentService.list(deptQueryWrapper);
        Assert.assertTrue(departments.size() == 3);

        // builder直接查询，不分页 3条结果
        List<Department> builderResultList = QueryBuilder.toDynamicJoinQueryWrapper(dto).queryList(Department.class);
        Assert.assertTrue(builderResultList.size() == 3);

        dto.setParentId(10001L);
        dto.setOrgName("苏州帝博");
        dto.setParentName("产品部");

        // 验证直接查询指定字段
        List<String> fields = Arrays.asList("parentId", "parentName", "orgName");
        builderResultList = QueryBuilder.toDynamicJoinQueryWrapper(dto, fields).queryList(Department.class);
        Assert.assertTrue(builderResultList.size() == 3);

        // 转换为queryWrapper
        queryWrapper.clear();
        queryWrapper = QueryBuilder.toQueryWrapper(dto);
        queryWrapper.select("id,name,parent_id,org_id");

        // 查询单条记录
        Department department = Binder.joinQueryOne(queryWrapper, Department.class);
        Assert.assertTrue(department.getName() != null);

        // 不分页 3条结果
        List<Department> list = JoinsBinder.queryList(queryWrapper, Department.class);
        Assert.assertTrue(list.size() == 3);
        Department departmentFirst = departmentService.getSingleEntity(queryWrapper);
        Assert.assertTrue(departmentFirst.getId().equals(list.get(0).getId()));

        // 不分页，直接用wrapper查
        list = QueryBuilder.toDynamicJoinQueryWrapper(dto).queryList(Department.class);
        Assert.assertTrue(list.size() == 3);

        // 测试继续绑定VO  是否有影响
        List<DepartmentVO> voList = Binder.convertAndBindRelations(list, DepartmentVO.class);
        Assert.assertTrue(voList.size() == 3);
        Assert.assertTrue(voList.get(0).getDepartment() != null);
        Assert.assertTrue(voList.get(0).getOrganizationVO() != null);

        // 分页
        Pagination pagination = new Pagination();
        pagination.setPageSize(2);
        pagination.setPageIndex(1);

        // 第一页  2条结果
        list = Binder.joinQueryList(queryWrapper, Department.class, pagination);
        Assert.assertTrue(list.size() == pagination.getPageSize());

        // 测试排序
        pagination.setOrderBy("orgName:DESC,parentName");
        pagination.setPageIndex(2);
        // 第二页 1条结果
        list = Binder.joinQueryList(queryWrapper, Department.class, pagination);
        Assert.assertTrue(list.size() == 1);


    }

    /**
     * 测试有中间表的动态sql join
     */
    @Test
    public void testDynamicSqlQueryWithMiddleTable() {
        // 初始化DTO，测试不涉及关联的情况
        UserDTO dto = new UserDTO();
        dto.setDeptName("研发组");
        dto.setDeptId("10002");

        // builder直接查询，不分页 3条结果
        List<User> builderResultList = QueryBuilder.toDynamicJoinQueryWrapper(dto).queryList(User.class);
        Assert.assertTrue(builderResultList.size() == 2);

        dto.setOrgName("苏州帝博");
        builderResultList = QueryBuilder.toDynamicJoinQueryWrapper(dto).queryList(User.class);
        Assert.assertTrue(builderResultList.size() == 2);

        dto.setOrgName("");
        builderResultList = QueryBuilder.toDynamicJoinQueryWrapper(dto).queryList(User.class);
        Assert.assertTrue(builderResultList.size() == 2);

        List<String> roleCodes = new ArrayList<>();
        roleCodes.add("ADMIN");
        roleCodes.add("OTHER");

        dto.setRoleCodes(roleCodes);
        builderResultList = QueryBuilder.toDynamicJoinQueryWrapper(dto).queryList(User.class);
        Assert.assertTrue(builderResultList.size() == 1);
    }

    /**
     * 测试有中间表的动态sql join
     */
    @Test
    public void testDynamicSqlQueryWithMiddleTable2() {
        // 初始化DTO，测试不涉及关联的情况
        UserDTO dto = new UserDTO();
        List<String> roleCodes = new ArrayList<>();
        roleCodes.add("ADMIN");
        roleCodes.add("OTHER");
        dto.setRoleCodes(roleCodes);

        List<User> builderResultList = QueryBuilder.toDynamicJoinQueryWrapper(dto).queryList(User.class);
        Assert.assertTrue(builderResultList.size() == 1);
    }

    /**
     * 测试有中间表的动态sql join
     */
    @Test
    public void testDynamicSqlQueryWithMiddleTable3() {
        // 初始化DTO，测试不涉及关联的情况
        UserDTO dto = new UserDTO();
        dto.setRoleId(101L);
        List<User> builderResultList = QueryBuilder.toDynamicJoinQueryWrapper(dto).queryList(User.class);
        Assert.assertTrue(builderResultList.size() == 1);
        QueryWrapper<User> queryWrapper = QueryBuilder.toQueryWrapper(dto);
        List<User> userList = userService.getEntityList(queryWrapper, new Pagination());
        Assert.assertTrue(userList.size() > 0);
    }

    @Test
    public void testDynamicSqlQueryWithOrders(){
        // 初始化DTO，测试不涉及关联的情况
        DepartmentDTO dto = new DepartmentDTO();
        dto.setParentId(10001L);

        // 分页
        Pagination pagination = new Pagination();
        pagination.setPageSize(2);
        pagination.setPageIndex(1);
        pagination.setOrderBy("parentId:DESC");

        // 测试排序
        // 验证 转换后的wrapper可以直接查询
        QueryWrapper<DepartmentDTO> queryWrapper = QueryBuilder.toDynamicJoinQueryWrapper(dto, pagination);
        // 第二页 1条结果
        List<Department> departments = departmentService.getEntityList(queryWrapper, pagination);
        Assert.assertTrue(departments.size() == pagination.getPageSize());

        dto.setOrgName("苏州");
        pagination.setOrderBy("orgName:DESC,parentName");

        // 验证 转换后的wrapper可以直接查询
        queryWrapper = QueryBuilder.toDynamicJoinQueryWrapper(dto, pagination);
        // 第二页 1条结果
        departments = departmentService.getEntityList(queryWrapper, pagination);
        Assert.assertTrue(departments.size() == pagination.getPageSize());
    }

    @Test
    public void testIgnoreEmptyDynamicSqlQuery(){
        // 初始化DTO，测试不涉及关联的情况
        DepartmentDTO dto = new DepartmentDTO();
        // builder直接查询，不分页 3条结果
        List<Department> builderResultList = departmentService.getEntityList(QueryBuilder.toQueryWrapper(dto));
        Assert.assertTrue(builderResultList.size() == 6);

        dto.setName("");
        builderResultList = departmentService.getEntityList(QueryBuilder.toQueryWrapper(dto));
        Assert.assertTrue(builderResultList.size() == 6);

        dto.setOrgName("");
        builderResultList = departmentService.getEntityList(QueryBuilder.toQueryWrapper(dto));
        Assert.assertTrue(builderResultList.size() == 6);
    }

    @Test
    public void testBindQueryGroup(){
        DepartmentDTO departmentDTO = new DepartmentDTO();
        departmentDTO.setSearch("帝博");
        List<Department> list = QueryBuilder.toDynamicJoinQueryWrapper(departmentDTO).queryList(Department.class);
        Assert.assertTrue(list.size() > 5);
        departmentDTO.setSearch("测试");
        list = QueryBuilder.toDynamicJoinQueryWrapper(departmentDTO).queryList(Department.class);
        Assert.assertTrue(list.size() > 2);
        departmentDTO.setSearch("WW");
        list = QueryBuilder.toDynamicJoinQueryWrapper(departmentDTO).queryList(Department.class);
        Assert.assertTrue(list.size() > 0);
        departmentDTO.setSearch("").setOrgName("苏州");
        list = QueryBuilder.toDynamicJoinQueryWrapper(departmentDTO).queryList(Department.class);
        Assert.assertTrue(list.size() > 5);
    }


    /**
     * 测试空值和null
     */
    @Test
    public void testNullEmptyQuery() {
        Department entity = new Department();
        entity.setName("测试组");
        entity.setOrgId("100001");
        QueryWrapper<Department> queryWrapper = QueryBuilder.toQueryWrapper(entity);
        System.out.println(queryWrapper.getExpression());
        Assert.assertTrue(queryWrapper.getSqlSegment().contains("parent_id IS NULL"));

        entity.setParentId("10001");
        queryWrapper = QueryBuilder.toQueryWrapper(entity);
        Assert.assertTrue(queryWrapper.getSqlSegment().contains("parent_id = #{"));
    }

    @Test
    public void test(){
        String sql = buildCheckDeletedColSql("test");
        Assert.assertTrue(sql.contains("SELECT is_deleted"));
        Assert.assertTrue(sql.contains("FROM test"));
        Assert.assertTrue(sql.contains("LIMIT 1"));
    }

    @Test
    public void testStringIdBuildQuery(){
        StrIdTest strIdTest = new StrIdTest();
        strIdTest.setId("123");
        strIdTest.setType("A");
        QueryWrapper<StrIdTest> queryWrapper = QueryBuilder.toDynamicJoinQueryWrapper(strIdTest);
        String sql = queryWrapper.getExpression().getSqlSegment();
        System.out.println(sql);
        Assert.assertTrue(sql.contains("ID_ ="));
    }

    /**
     * 构建检测是否有删除字段的sql
     * @param middleTable
     * @return
     */
    private static String buildCheckDeletedColSql(String middleTable){
        return new SQL(){
            {
                SELECT(Cons.COLUMN_IS_DELETED);
                FROM(middleTable);
                LIMIT(1);
            }
        }.toString();
    }

    @Test
    public void testJoinAttachRef(){
        ProblemDTO problemDTO = new ProblemDTO();
        problemDTO.setUserId("1001");
        QueryWrapper<Problem> queryWrapper = QueryBuilder.toQueryWrapper(problemDTO);
        List<Problem> list = problemService.getEntityList(queryWrapper);
        Assert.assertTrue(list.size() >= 1);
    }
}
