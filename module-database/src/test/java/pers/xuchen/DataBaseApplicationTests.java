package pers.xuchen;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import pers.xuchen.module.database.table.service.SysUserService;

@SpringBootTest
@Slf4j
class DataBaseApplicationTests {

    @Autowired
    SysUserService sysUserService;

    @Value("${test.key:}")
    String value;

    @Test
    void contextLoads() {
//        for (int i = 1; i < 11; i++) {
//            SysUser user = new SysUser();
//            user.setNickName("nickname"+i);
//            user.setPassword("password"+i);
////            user.setCreateTime(LocalDateTime.now());
//            user.setCreateUser("createUser"+i);
////            user.setUpdateTime(LocalDateTime.now());
//            user.setUpdateUser("updateUser"+i);
//            sysUserService.save(user);
//            ThreadUtil.sleep(500);
//        }
        log.info(JSONUtil.toJsonPrettyStr(sysUserService.list()));
//        log.info(sysUserService.list().toString());
//        List<SysUser> list = sysUserService.listObjs(new QueryWrapper(new SysUser() {{
//            setNickName("nickname5");
//        }}));
//        Page<SysUser> userPage = sysUserService.page(new Page<>(), new QueryWrapper<>());

    }


}
