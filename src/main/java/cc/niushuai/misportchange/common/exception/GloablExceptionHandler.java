package cc.niushuai.misportchange.common.exception;

import cc.niushuai.misportchange.stepchange.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ns
 * @date 2020/11/6
 */
@Slf4j
@ControllerAdvice
public class GloablExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = BizException.class)
    public Result bizException(HttpServletRequest request, BizException e) {
        log.error("bizException: {}", e.getMsg(), e);
        return Result.error(e.getCode(), e.getMsg());
    }

    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result methodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException validException) {
        log.error("validException: {}", validException.getMessage(), validException);

        StringBuilder builder = new StringBuilder();

        for (FieldError error : validException.getBindingResult().getFieldErrors()) {
            builder.append(error.getDefaultMessage());
            builder.append(",");
        }

        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }

        return Result.error(40001, builder.toString());
    }

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Result exception(HttpServletRequest request, Exception exception) {
        log.error("exception: {}", exception.getMessage(), exception);
        return Result.error(50000, exception.getMessage());
    }

}
