package diboot.core.test.binder.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.diboot.core.entity.BaseModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

/**
* 领养 Entity 定义
* @author MyName
* @version 1.0
* @date 2024-09-03
* Copyright © MyCorp
*/

@TableName("mdl_pet_adopt")
@Getter @Setter @Accessors(chain = true)
public class PetAdopt extends BaseModel {
    private static final long serialVersionUID = 1140534725406040048L;
     /**
     * 姓名
     */
     @Length(max=100, message="姓名长度应小于100")
     @BindQuery(comparison = Comparison.LIKE)
     @TableField()
     private String realname;

     /**
     * 宠物品类
     */
     @Length(max=100, message="宠物品类长度应小于100")
     @BindQuery(comparison = Comparison.LIKE)
     @TableField()
     private String petCategory;

     /**
     * 宠物编号
     */
     @Length(max=100, message="宠物编号长度应小于100")
     @BindQuery(comparison = Comparison.LIKE)
     @TableField()
     private String petCode;

     /**
     * 备注
     */
     @Length(max=100, message="备注长度应小于100")
     @BindQuery(comparison = Comparison.LIKE)
     @TableField()
     private String remark;


}