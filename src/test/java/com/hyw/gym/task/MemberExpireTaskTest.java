package com.hyw.gym.task;

import com.hyw.gym.GymApplication;
import com.hyw.gym.dao.UserDao;
import com.hyw.gym.entity.User;
import com.hyw.gym.enums.UserType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 手动校准测试：跑一次 MemberExpireTask.updateRemainingDays()，
 * 打印校准前后所有会员的剩余天数/到期日/状态，用于人工核对校准逻辑是否正确。
 *
 * 运行：mvn -Dtest=MemberExpireTaskTest test
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GymApplication.class)
public class MemberExpireTaskTest {

    @Autowired
    private MemberExpireTask memberExpireTask;
    @Autowired
    private UserDao userDao;

    @Test
    public void runCalibrationOnce() {
        System.out.println("========== 校准前 ==========");
        printMembers();

        memberExpireTask.updateRemainingDays();

        System.out.println("========== 校准后 ==========");
        printMembers();
    }

    private void printMembers() {
        List<User> members = userDao.findByUserType(UserType.MEMBER.getType());
        if (members.isEmpty()) {
            System.out.println("（数据库中没有会员数据）");
            return;
        }
        for (User m : members) {
            System.out.println("id=" + m.getId()
                    + " phone=" + m.getPhone()
                    + " remaining=" + m.getRemainingDays()
                    + " expire=" + m.getMemberXufei()
                    + " status=" + m.getStatus());
        }
    }
}
