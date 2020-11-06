package cc.niushuai.misportchange.stepchange.service.impl;

import cc.niushuai.misportchange.stepchange.bean.MiUser;
import cc.niushuai.misportchange.stepchange.biz.MiStepBiz;
import cc.niushuai.misportchange.stepchange.service.StepChangeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author ns
 * @date 2020/11/5
 */
@Slf4j
@Service
public class StepChangeServiceImpl implements StepChangeService {

    @Resource
    private MiStepBiz miStepBiz;

    @Override
    public String change(MiUser user) throws Exception {
        String res = "";

        // 修改类型
        String stepType = user.getStepType();
        switch (stepType) {
            // 小米运动
            case "MI":
                res = miStepBiz.miChange(user);
                break;
            // 乐心
            case "LX":
                break;
            // 未知
            default:

        }
        return res;
    }
}
