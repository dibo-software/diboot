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
* 宠物 Entity 定义
* @author MyName
* @version 1.0
* @date 2024-09-03
* Copyright © MyCorp
*/

@TableName("mdl_pet")
@Getter @Setter @Accessors(chain = true)
public class Pet extends BaseModel {
    private static final long serialVersionUID = 1537809466056333722L;
     /**
     * 品类
     */
     @TableField()
     private String category;

     /**
     * 编号
     */
     @Length(max=100, message="编号长度应小于100")
     @BindQuery(comparison = Comparison.LIKE)
     @TableField()
     private String code;

     /**
     * 备注
     */
     @Length(max=100, message="备注长度应小于100")
     @BindQuery(comparison = Comparison.LIKE)
     @TableField()
     private String remark;


}