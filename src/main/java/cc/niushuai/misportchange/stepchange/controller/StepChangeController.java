package cc.niushuai.misportchange.stepchange.controller;

import cc.niushuai.misportchange.stepchange.bean.MiUser;
import cc.niushuai.misportchange.stepchange.bean.Result;
import cc.niushuai.misportchange.stepchange.service.StepChangeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author ns
 * @date 2020/11/5
 */
@RestController
@RequestMapping("/mi/stepChange")
public class StepChangeController {

    @Resource
    private StepChangeService stepChangeService;

    @PostMapping("/change")
    public Result change(@RequestBody @Valid MiUser user) throws Exception {

        if (null == user.getTimeStamp()) {
            return Result.ok();
        }

        String res = stepChangeService.change(user);

        return Result.ok();
    }
}
