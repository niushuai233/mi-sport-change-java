package cc.niushuai.projects.misportchange.stepchange.bean;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
    @Max(value = 49999L, message = "最大不建议超过49999")
    private Long step;
    private Long timeStamp;

    public boolean isEmpty() {

        if (StrUtil.isNotEmpty(username) && StrUtil.isNotEmpty(password) && StrUtil.isNotEmpty(step+"") && StrUtil.isNotEmpty(stepType)) {
            return false;
        }
        return true;
    }
}
