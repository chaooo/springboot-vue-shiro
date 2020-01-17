package top.itdn.server.controller;

import org.apache.shiro.ShiroException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import top.itdn.server.exception.UnauthorizedException;
import top.itdn.server.utils.ResponseVo;

import javax.servlet.http.HttpServletRequest;

/**
 * Description
 *
 * @author : Charles
 * @date : 2020/1/17
 */
public class ExceptionApi {
    /** 捕捉 shiro 的异常 */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ShiroException.class)
    public ResponseVo handle401(ShiroException e) {
        return new ResponseVo(401, e.getMessage());
    }

    /** 捕捉UnauthorizedException */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseVo handle401() {
        return new ResponseVo(401, "Unauthorized");
    }

    /** 捕捉其他所有异常 */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseVo globalException(HttpServletRequest request, Throwable ex) {
        return new ResponseVo(getStatus(request).value(), ex.getMessage());
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }
}
