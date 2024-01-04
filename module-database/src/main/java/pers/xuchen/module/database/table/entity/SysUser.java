package pers.xuchen.module.database.table.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;
import pers.xuchen.module.database.base.BaseEntity;


@Data
@ToString(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    /**
     * 用户名
     */
    private String nickName;
    /**
     * 密码
     */
    private String password;
}
