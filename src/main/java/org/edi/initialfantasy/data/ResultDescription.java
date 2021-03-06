package org.edi.initialfantasy.data;

import org.edi.freamwork.data.operation.OpResultDescription;

/**
 * @author Fancy
 * @date 2018/7/13
 */
public class ResultDescription extends OpResultDescription {

    public static final String OK = "ok";

    public static final String FAIL = "fail";

    public static final String DOCENTRY_IS_NULL = "DOCENTRY信息为空";

    public static final String TOKEN_IS_EMPTY = "TOKEN信息为空";

    public static final String TOKEN_IS_EXPIRED = "TOKEN已过期";

    public static final String TOKEN_IS_ERROR = "TOKEN不存在";

    public static final String COMPANY_IS_NONEXISTENT = "公司不存在";

    public static final String USER_IS_NONEXISTENT = "用户不存在";

    public static final String USERPASSWORD_IS_ERROR = "用户密码错误";

    public static final String LOGIN_EXCEPTION = "登录异常>>>>>>>>";

    public static final String USER_LOGININ = "用户登录>>>>>>>>";

    public static final String USER_LOGINOUT = "用户退出>>>>>>>>";

    public static final String COMPANY_FILE_NOT_FOUND = "公司配置文件未找到";

    public static final String COMPANY_INFO_ERROR = "公司配置文件配置错误";
}
