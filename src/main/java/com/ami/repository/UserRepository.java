package com.ami.repository;

import com.ami.entity.User;
import com.ami.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	Optional<User> findByUserName(String userName);

	Boolean existsByEmail(String email);

	Boolean existsByUserName(String userName);

	List<User> findByRole(RoleType role);

	List<User> findByCreatedByAndRole(User createdBy, RoleType role);

	List<User> findByRoleAndCreatedBy(RoleType role, User createdBy);

	Page<User> findAll(Pageable pageable);

	Page<User> findByRole(RoleType role, Pageable pageable);

	Page<User> findByCreatedBy(User createdBy, Pageable pageable);

	Page<User> findByRoleAndCreatedBy(RoleType role, User createdBy, Pageable pageable);

	Page<User> findByActiveAndCreatedBy(Boolean active, User createdBy, Pageable pageable);
	
	Page<User> findByActive(Boolean active,Pageable pageable); 
	
	

	//SEARCH FOR ADMIN
	@Query("""
			    SELECT u FROM User u
			    WHERE u.createdBy.id = :adminId
			    AND (
			        LOWER(u.userName) LIKE LOWER(CONCAT('%', :keyword, '%'))
			        OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
			        OR LOWER(u.phoneNo) LIKE LOWER(CONCAT('%', :keyword, '%'))
			        OR LOWER(u.city) LIKE LOWER(CONCAT('%', :keyword, '%'))
			        OR LOWER(u.state) LIKE LOWER(CONCAT('%', :keyword, '%'))
			    )
			""")
	Page<User> searchUsersForAdmin(@Param("keyword") String keyword, @Param("adminId") Long adminId, Pageable pageable);

	//SEARCH FOR SUPER ADMIN
	@Query("""
			    SELECT u FROM User u
			    WHERE
			        LOWER(u.userName) LIKE LOWER(CONCAT('%', :keyword, '%'))
			        OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
			        OR LOWER(u.phoneNo) LIKE LOWER(CONCAT('%', :keyword, '%'))
			        OR LOWER(u.city) LIKE LOWER(CONCAT('%', :keyword, '%'))
			        OR LOWER(u.state) LIKE LOWER(CONCAT('%', :keyword, '%'))
			""")
	Page<User> searchUsersForSuperAdmin(@Param("keyword") String keyword, Pageable pageable);

}