package cc.niushuai.misportchange.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ns
 * @date 2020/11/6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BizException extends Exception {

    private Integer code;
    private String msg;
}
