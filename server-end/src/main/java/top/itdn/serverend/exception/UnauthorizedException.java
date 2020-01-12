package top.itdn.serverend.exception;

/**
 * Description
 *
 * @author : Charles
 * @date : 2020/1/12
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String msg) {
        super(msg);
    }
    public UnauthorizedException() {
        super();
    }

}
