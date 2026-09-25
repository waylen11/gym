package com.hyw.gym.dao;


import com.hyw.gym.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @Description: 管理员Dao层接口
 */
@Transactional
public interface UserDao extends JpaRepository<User,Long> {
    User findByLoginNameAndPassword(String name, String password);


    @Modifying
    @Query(value = "update sys_user set password =:password where user_id =:id",nativeQuery = true)
    void updPassword(@Param("id") long id,@Param("password") String password) ;


    List<User> findByUserType(Integer userType);

    User findByPhone(String phone);

}
