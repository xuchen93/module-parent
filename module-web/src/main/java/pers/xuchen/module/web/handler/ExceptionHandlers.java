package pers.xuchen.module.web.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import pers.xuchen.module.core.model.R;
import pers.xuchen.module.core.model.ex.AuthException;
import pers.xuchen.module.core.model.ex.BusiException;

@Slf4j
@ControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler(BusiException.class)
    @ResponseBody
    public R throwable(BusiException e) {
        return R.fail(e.getMessage());
    }

    @ExceptionHandler(AuthException.class)
    @ResponseBody
    public R throwable(AuthException e) {
        return R.fail(e.getCode(),e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public R throwable(Exception e) {
        log.error("拦截到未知异常", e);
        return R.fail();
    }
}
