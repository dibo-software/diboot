package com.diboot.core.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.diboot.core.binding.manager.RelationsBinder;
import com.diboot.core.entity.BaseEntity;
import com.diboot.core.service.BaseService;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Status;
import com.diboot.core.vo.Pagination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * 增删改查通用管理功能-父类
 * @author Mazhicheng
 * @version 2.0
 * @date 2019/01/01
 */
@RestController
public abstract class BaseCrudRestController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(BaseCrudRestController.class);

	/**
	 * 获取service实例
	 * @return
	 */
	protected abstract BaseService getService();

	/***
	 * 获取某资源的集合
	 * <p>
	 * url参数示例: /dictionary/list?_pageSize=20&_pageIndex=1&_orderBy=itemValue&type=GENDAR
	 * </p>
	 * @param request
	 * @return JsonResult
	 * @throws Exception
	 */
	protected <T> JsonResult getEntityList(HttpServletRequest request, Wrapper queryWrapper) throws Exception {
		// 查询当前页的数据
		List entityList = getService().getEntityList(queryWrapper);
		// 返回结果
		return new JsonResult(Status.OK, entityList);
	}

	/***
	 * 获取某资源的集合
	 * <p>
	 * url参数示例: /dictionary/list?_pageSize=20&_pageIndex=1&_orderBy=itemValue&type=GENDAR
	 * </p>
	 * @param request
	 * @return JsonResult
	 * @throws Exception
	 */
	protected JsonResult getEntityListWithPaging(HttpServletRequest request, Wrapper queryWrapper) throws Exception {
		// 构建分页
		Pagination pagination = buildPagination(request);
		// 查询当前页的数据
		List entityList = getService().getEntityList(queryWrapper, pagination);
		// 返回结果
		return new JsonResult(Status.OK, entityList).bindPagination(pagination);
	}

	/***
	 * 获取某VO资源的集合
	 * <p>
	 * url参数示例: /dictionary/list?_pageSize=20&_pageIndex=1&_orderBy=itemValue&type=GENDAR
	 * </p>
	 * @param request
	 * @return JsonResult
	 * @throws Exception
	 */
	protected <T> JsonResult getVOListWithPaging(HttpServletRequest request, Wrapper queryWrapper, Class<T> clazz) throws Exception {
		// 构建分页
		Pagination pagination = buildPagination(request);
		// 查询当前页的数据
		List<T> voList = getService().getViewObjectList(queryWrapper, pagination, clazz);
		// 返回结果
		return new JsonResult(Status.OK, voList).bindPagination(pagination);
	}

	/***
	 * 根据id获取某资源对象
	 * @param id
	 * @return JsonResult
	 * @throws Exception
	 */
	protected JsonResult getEntity(Long id) throws Exception {
		Object entity = getService().getEntity(id);
		return new JsonResult(Status.OK, entity);
    }

	/***
	 * 创建资源对象
	 * @param entity
	 * @param result
	 * @return JsonResult
	 * @throws Exception
	 */
	protected JsonResult createEntity(BaseEntity entity, BindingResult result, ModelMap modelMap) throws Exception {
        // Model属性值验证结果
		if(result != null && result.hasErrors()) {
            return new JsonResult(Status.FAIL_INVALID_PARAM, super.getBindingError(result));
        }
		if(modelMap.get(ERROR) != null){
			return new JsonResult(Status.FAIL_VALIDATION, (String) modelMap.get(ERROR));
		}
        // 执行保存操作
        boolean success = getService().createEntity(entity);
        if(success){
			// 组装返回结果
			Map<String, Object> data = new HashMap<>(2);
			data.put(PARAM_ID, entity.getId());
			return new JsonResult(Status.OK, data);
        }
        else{
        	log.warn("创建操作未成功，model="+entity.getClass().getSimpleName());
			// 组装返回结果
			return new JsonResult(Status.FAIL_OPERATION);
        }
    }

	/***
	 * 根据ID更新资源对象
	 * @param entity
	 * @param result
	 * @return JsonResult
	 * @throws Exception
	 */
	protected JsonResult updateEntity(BaseEntity entity, BindingResult result, ModelMap modelMap) throws Exception{
		// Model属性值验证结果
		if(result.hasErrors()) {
			return new JsonResult(Status.FAIL_INVALID_PARAM, super.getBindingError(result));
		}
		if(modelMap.get(ERROR) != null){
			return new JsonResult(Status.FAIL_VALIDATION, (String) modelMap.get(ERROR));
		}
		// 执行保存操作
		boolean success = getService().updateEntity(entity);
        if(success){
			// 组装返回结果
			Map<String, Object> data = new HashMap<>(2);
			data.put(PARAM_ID, entity.getId());
			return new JsonResult(Status.OK, data);
        }
        else{
        	log.warn("更新操作失败，model="+entity.getClass().getSimpleName()+", id="+entity.getId());
			// 返回操作结果
			return new JsonResult(Status.FAIL_OPERATION);
        }
	}
	
	/***
	 * 根据id删除资源对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	protected JsonResult deleteEntity(Serializable id) throws Exception{
		if(id == null) {
			return new JsonResult(Status.FAIL_INVALID_PARAM, "请选择需要删除的条目！");
        }
		// 是否有权限删除
		BaseEntity model = (BaseEntity) getService().getEntity(id);
		// 执行删除操作
		String error = beforeDelete(model);
		if(error != null){
			// 返回json
			return new JsonResult(Status.FAIL_OPERATION, error);
		}
		// 执行删除操作
		boolean success = getService().deleteEntity(id);
		if(success){
			log.info("删除操作成功，model="+model.getClass().getSimpleName()+", id="+id);
			// 组装返回结果
			Map<String, Object> data = new HashMap<>(2);
			data.put(PARAM_ID, model.getId());
			return new JsonResult(Status.OK, data);
		}
		else{
			log.warn("删除操作未成功，model="+model.getClass().getSimpleName()+", id="+id);
			return new JsonResult(Status.FAIL_OPERATION);
		}
	}

	/**
	 * 自动转换为VO并绑定关联关系
	 * @param entityList
	 * @param voClass
	 * @param <VO>
	 * @return
	 */
	protected <VO> List<VO> convertToVoAndBindRelations(List entityList, Class<VO> voClass){
		// 转换为VO
		List<VO> voList = RelationsBinder.convertAndBind(entityList, voClass);
		return voList;
	}

	//============= 供子类继承重写的方法 =================
	/***
	 * 是否有删除权限，如不可删除返回错误提示信息，如 Status.FAIL_NO_PERMISSION.label()
	 * @param entity
	 * @return
	 */
	protected String beforeDelete(BaseEntity entity){
		return null;
	}

}