package diboot.core.test.binder.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.diboot.core.entity.BaseModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@TableName("mdl_product")
@Getter @Setter @Accessors(chain = true)
public class Product extends BaseModel {
  private static final long serialVersionUID = -5219204005629067148L;
     /**
     * 品类
     */
     @TableField()
     @BindQuery(entity = CoachProductCategory.class, condition = "this.category = category and coach_id = ${coachId}")
     private String category;

     /**
     * 备注
     */
     @BindQuery(comparison = Comparison.LIKE)
     @TableField()
     private String remark;

}