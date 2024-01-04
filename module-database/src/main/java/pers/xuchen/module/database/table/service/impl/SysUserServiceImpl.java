package pers.xuchen.module.database.table.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.xuchen.module.database.table.dao.SysUserDao;
import pers.xuchen.module.database.table.entity.SysUser;
import pers.xuchen.module.database.table.service.SysUserService;


@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserDao, SysUser> implements SysUserService {

}
