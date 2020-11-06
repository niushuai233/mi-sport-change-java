package cc.niushuai.misportchange.stepchange.bean;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author ns
 * @date 2020/11/5
 */
@Data
public class MiUser {

    @NotNull(message = "请输入用户名")
    private String username;
    @NotNull(message = "请输入密码")
    private String password;
    @NotNull(message = "请选择运动类型")
    private String stepType;
    @NotNull(message = "请输入要修改的步数")
    private Long step;
    private Long timeStamp;
}
