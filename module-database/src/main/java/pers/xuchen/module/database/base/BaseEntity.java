package pers.xuchen.module.database.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.ASSIGN_ID)
    protected String id;
    @TableField(fill = FieldFill.INSERT)
    protected String createUser;
    @TableField(fill = FieldFill.INSERT)
    protected LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected String updateUser;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected LocalDateTime updateTime;
    @Version
    protected Long version;
}
