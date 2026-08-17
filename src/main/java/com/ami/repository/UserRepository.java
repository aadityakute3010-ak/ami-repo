package com.ami.repository;

import com.ami.entity.User;
import com.ami.enums.EngineerAttendanceStatus;
import com.ami.enums.EngineerAvailabilityStatus;
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
	
	Long countByRole(RoleType role);

	Long countByAttendanceStatus(
	        EngineerAttendanceStatus status);
	
	Long countByRoleAndAvailabilityStatus(
	        RoleType role,
	        EngineerAvailabilityStatus status);

	Long countByAvailabilityStatus(
	        EngineerAvailabilityStatus status);
	
	List<User> findByRoleAndAvailabilityStatus(
	        RoleType role,
	        EngineerAvailabilityStatus availabilityStatus);
	
	

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

	@Query("""
		    SELECT u
		    FROM User u
		    WHERE u.role = com.ami.enums.RoleType.SERVICE_ENGINEER
		    AND (:search IS NULL
		         OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
		         OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
		         OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))
		    AND (:attendanceStatus IS NULL
		         OR u.attendanceStatus = :attendanceStatus)
		    AND (:availabilityStatus IS NULL
		         OR u.availabilityStatus = :availabilityStatus)
		""")
		Page<User> findEngineerOperations(
		        @Param("search") String search,
		        @Param("attendanceStatus") EngineerAttendanceStatus attendanceStatus,
		        @Param("availabilityStatus") EngineerAvailabilityStatus availabilityStatus,
		        Pageable pageable);
}