package jieyi.lu.huanju.repository;

import jieyi.lu.huanju.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// 不用写实现逻辑，根据名字就能自动生成，方法名转 JPQL 语句的逻辑如下：
/*
   find   By   Username
   ↓      ↓    ↓
 查询   WHERE  username = ?

完整解析规则：

关键字	                   示例	                    生成的 JPQL
findBy	            findByUsername	        WHERE x.username = ?1
findByAnd	        findByUsernameAndAge	WHERE x.username = ?1 AND x.age = ?2
findByOr	        findByUsernameOrEmail	WHERE x.username = ?1 OR x.email = ?2
findByLike	        findByUsernameLike	    WHERE x.username LIKE ?1
findByBetween	    findByAgeBetween	    WHERE x.age BETWEEN ?1 AND ?2
findByLessThan	    findByAgeLessThan	    WHERE x.age < ?1
findByGreaterThan	findByAgeGreaterThan	WHERE x.age > ?1
findByIsNull	    findByEmailIsNull	    WHERE x.email IS NULL
findByOrderBy	    findByOrderByAgeDesc	ORDER BY x.age DESC
countBy	            countByUsername	        SELECT COUNT(x) WHERE x.username = ?1
deleteBy	        deleteByUsername	    DELETE WHERE x.username = ?1
existsBy	        existsByUsername	    SELECT COUNT(x) > 0 WHERE x.username = ?1
*/
@Repository
public interface UserRepository extends JpaRepository<User, Long> { // User：要操作的实体类, Long：实体类中 @Id 主键字段的类型
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
