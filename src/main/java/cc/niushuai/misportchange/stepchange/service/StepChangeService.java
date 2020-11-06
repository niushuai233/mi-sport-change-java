package cc.niushuai.misportchange.stepchange.service;

import cc.niushuai.misportchange.stepchange.bean.MiUser;

/**
 * @author ns
 * @date 2020/11/5
 */
public interface StepChangeService {

    String change(MiUser user) throws Exception;
}
