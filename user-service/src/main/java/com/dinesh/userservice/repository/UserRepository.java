package com.dinesh.userservice.repository;

import java.util.List;

import com.dinesh.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	@Query(value = "SELECT json_build_object('id', k.id,'phoneNo',k.phone_no) FROM users k WHERE id =?1", nativeQuery = true)
	String findByUser(Integer id);

	@Query(value = "SELECT * FROM users WHERE email =?1 ", nativeQuery = true)
	User findByEmail(String username);

	@Query( value = "SELECT * FROM users WHERE email  = ?1 OR (phone_no  LIKE concat('%', ?2) )", nativeQuery = true)
	User findByEmailOrPhoneNo(String email, String phoneNO);

    @Query( value = "SELECT * FROM users WHERE (phone_no  LIKE concat('%', ?1) )", nativeQuery = true)
	User findUserByPhoneNo(String phoneNo);

	@Query( value = "SELECT * FROM users WHERE (phone_no  LIKE concat('%', ?1) ) OR email = ?2", nativeQuery = true)
	List<User> findUserByPhoneNoOrEmail(String phoneNo, String username);

	//User Profile
	@Query(value = "Select json_build_object('id',id, 'firstName', firstname,'lastName', lastname," +
			"'email', email,'phoneNo', phone_no) from users  where id=?1", nativeQuery = true )
	String findUserByIdProfile(Integer userid);


}
