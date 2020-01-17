package top.itdn.server.exception;

/**
 * Description
 *
 * @author : Charles
 * @date : 2020/1/17
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String msg) {
        super(msg);
    }
    public UnauthorizedException() {
        super();
    }

}
