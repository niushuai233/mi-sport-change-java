package cc.niushuai.projects.misportchange;

import cc.niushuai.projects.misportchange.common.config.RestTemplateConfig;
import cc.niushuai.projects.misportchange.stepchange.bean.MiUser;
import cc.niushuai.projects.misportchange.stepchange.biz.MiStepBiz;
import cc.niushuai.projects.misportchange.stepchange.service.impl.StepChangeServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MiSportChangeApplication {

    public static void main(String[] args) throws Exception {

        MiUser userFromArgs = getMiUserFromArgs(args);
        if (null == userFromArgs) {
            // 以服务形式启动
            SpringApplication.run(MiSportChangeApplication.class, args);
        } else {
            // 以main方法形式启动
            StepChangeServiceImpl stepChangeService = new StepChangeServiceImpl();
            MiStepBiz miStepBiz = new MiStepBiz();
            miStepBiz.setRestTemplateBean(new RestTemplateConfig().restTemplate());
            stepChangeService.setMiStepBizBean(miStepBiz);
            stepChangeService.change(userFromArgs);
        }

    }

    /**
     * 根据传递的参数来判断以何种形式启动  默认以web形式启动
     *
     * @param args
     * @return boolean
     *
     * @author niushuai233
     * @date 2020/11/10 14:59
     **/
    private static MiUser getMiUserFromArgs(String[] args) {
        MiUser miUser = new MiUser();
        // 存在参数则判断
        if (args.length > 0) {
            for (String arg : args) {
                String[] split = arg.split("=");
                if (split.length == 1) {
                    endApplication();
                }
                String key = split[0];
                String val = split[1];

                if ("--run".equalsIgnoreCase(key) && "web".equalsIgnoreCase(val)) {
                    // 服务方式运行
                    return null;
                }

                if ("--user".equalsIgnoreCase(key)) {
                    miUser.setUsername(val);
                } else if ("--password".equalsIgnoreCase(key)) {
                    miUser.setPassword(val);
                } else if ("--step".equalsIgnoreCase(key)) {
                    miUser.setStep(Long.valueOf(val));
                }
            }
            miUser.setStepType("MI");
        }
        if (miUser.isEmpty()) {
            endApplication();
        }
        return miUser;
    }

    private static void endApplication() {
        System.err.println("参数不合法, 请检查");
        System.out.println("usage: ");
        System.out.println("    单机运行一次: java -jar mi-sport-change-java.jar --run=single --user=[账号] --password=[密码] --step=[要修改的步数]");
        System.out.println("    Web 服务运行: java -jar mi-sport-change-java.jar --run=web");
        System.exit(0);
    }
}
