package diboot.core.test.binder.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.diboot.core.entity.BaseModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@TableName("mdl_coach_category")
@Getter @Setter @Accessors(chain = true)
public class CoachProductCategory extends BaseModel {
  private static final long serialVersionUID = -5219204005629067148L;
     /**
     * 品类
     */
     @TableField()
     private String category;

     /**
     * 教练id
     */
     @TableField()
     private String coachId;

}