package top.itdn.serverend.entity.vo;

/**
 * Description
 *
 * @author : Charles
 * @date : 2020/1/12
 */
public class ResponseVo<T> {

    /** 状态码 */
    private int code;
    /** 提示信息 */
    private String msg;
    /** 返回的数据 */
    private T data;

    public ResponseVo() {}
    public ResponseVo(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public ResponseVo(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }
}
