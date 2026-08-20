package com.ami.repository;

import com.ami.entity.User;
import com.ami.enums.RoleType;
import com.ami.enums.SourceType;
import com.ami.enums.StatusType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
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

	Page<User> findByStatus(StatusType status, Pageable pageable);

	Page<User> findByStatusAndCreatedBy(StatusType status, User createdBy, Pageable pageable);

	List<User> findByCreatedBy(User createdBy);

	@Query("""
			    SELECT COUNT(u) > 0
			    FROM User u
			    WHERE LOWER(u.email) = LOWER(:email)
			    AND u.id <> :userId
			""")
	boolean existsEmailForOtherUser(@Param("email") String email, @Param("userId") Long userId);

	@Query("""
			    SELECT COUNT(u) > 0
			    FROM User u
			    WHERE LOWER(u.userName) = LOWER(:userName)
			    AND u.id <> :userId
			""")
	boolean existsUserNameForOtherUser(@Param("userName") String userName, @Param("userId") Long userId);

	@Query("""
			SELECT u
			FROM User u
			WHERE 
			u.id <> :loggedInUserId
		    AND (:role IS NULL OR u.role = :role)
			AND (:status IS NULL OR u.status = :status)
			AND (:fromDateTime IS NULL OR u.createdAt >= :fromDateTime)
			AND (:toDateTime IS NULL OR u.createdAt <= :toDateTime)
			AND (
			    :keyword IS NULL
			    OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
			    OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
			    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
			    OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :keyword, '%'))
			    OR LOWER(CONCAT(u.firstName,' ',u.lastName)) LIKE LOWER(CONCAT('%', :keyword, '%'))
			)
			""")
	Page<User> findUsersWithFilters(Long loggedInUserId, String keyword, RoleType role, StatusType status, LocalDateTime fromDateTime,
			LocalDateTime toDateTime, Pageable pageable);

	@Query("""
			SELECT u
			FROM User u
			WHERE u.createdBy.id = :adminId
			AND u.id <> :loggedInUserId
			AND (:role IS NULL OR u.role = :role)
			AND (:status IS NULL OR u.status = :status)
			AND (:fromDateTime IS NULL OR u.createdAt >= :fromDateTime)
			AND (:toDateTime IS NULL OR u.createdAt <= :toDateTime)
			AND (
			    :keyword IS NULL
			    OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
			    OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
			    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
			    OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :keyword, '%'))
			    OR LOWER(CONCAT(u.firstName,' ',u.lastName)) LIKE LOWER(CONCAT('%', :keyword, '%'))
			)
			""")
	Page<User> findUsersWithFiltersForAdmin(Long adminId, Long loggedInUserId, String keyword, RoleType role, StatusType status,
			LocalDateTime fromDateTime, LocalDateTime toDateTime, Pageable pageable);

	@Query("""
			    SELECT u FROM User u
			    WHERE u.role = :role
			    AND u.status = com.ami.enums.StatusType.ACTIVE
			    AND :sourceType MEMBER OF u.assignedSources
			    AND (
			        :search IS NULL OR
			        LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR
			        LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR
			        LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR
			        LOWER(u.userName) LIKE LOWER(CONCAT('%', :search, '%'))
			    )
			""")
	List<User> findEligibleAdmins(@Param("role") RoleType role, @Param("sourceType") SourceType sourceType,
			@Param("search") String search);

	@Query("""
			SELECT u
			FROM User u
			JOIN u.assignedSources s
			WHERE u.createdBy.id = :adminId
			AND u.role = com.ami.enums.RoleType.USER
			AND u.status = com.ami.enums.StatusType.ACTIVE
			AND s = :sourceType
			ORDER BY u.firstName ASC
			""")
	List<User> findEligibleUsersByAdminAndSource(@Param("adminId") Long adminId,
			@Param("sourceType") SourceType sourceType);

	long count();

	long countByStatus(StatusType status);

	long countByRole(RoleType role);

	@Query("""
			SELECT COUNT(u)
			FROM User u
			WHERE u.createdBy.id = :adminId
			""")
	long countByCreatedBy(@Param("adminId") Long adminId);

	@Query("""
			SELECT COUNT(u)
			FROM User u
			WHERE u.createdBy.id = :adminId
			AND u.status = :status
			""")
	long countByCreatedByAndStatus(@Param("adminId") Long adminId, @Param("status") StatusType status);

	@Query("""
			SELECT COUNT(u)
			FROM User u
			WHERE u.createdBy.id = :adminId
			AND u.role = :role
			""")
	long countByCreatedByAndRole(@Param("adminId") Long adminId, @Param("role") RoleType role);

	@Query("""
			SELECT COUNT(u)
			FROM User u
			WHERE u.createdBy.id = :adminId
			AND u.assignedSources IS NOT EMPTY
			""")
	long countAssignedUsersByAdmin(@Param("adminId") Long adminId);

	@Query("""
			SELECT COUNT(u)
			FROM User u
			WHERE u.createdBy.id = :adminId
			AND u.createdAt >= :start
			""")
	long countCreatedTodayByAdmin(@Param("adminId") Long adminId, @Param("start") LocalDateTime start);

	@Query("""
			SELECT COUNT(u)
			FROM User u
			WHERE u.createdBy.id = :adminId
			AND u.status = :status
			AND u.createdAt >= :start
			""")
	long countCreatedTodayByAdminAndStatus(@Param("adminId") Long adminId, @Param("status") StatusType status,
			@Param("start") LocalDateTime start);

	@Query("""
			SELECT COUNT(u)
			FROM User u
			WHERE u.createdBy.id = :adminId
			AND u.role = :role
			AND u.createdAt >= :start
			""")
	long countCreatedTodayByAdminAndRole(@Param("adminId") Long adminId, @Param("role") RoleType role,
			@Param("start") LocalDateTime start);

	@Query("""
			SELECT COUNT(u)
			FROM User u
			WHERE u.createdBy.id = :adminId
			AND u.assignedSources IS NOT EMPTY
			AND u.createdAt >= :start
			""")
	long countAssignedUsersCreatedTodayByAdmin(@Param("adminId") Long adminId, @Param("start") LocalDateTime start);

	@Query("""
			SELECT COUNT(u)
			FROM User u
			WHERE u.assignedSources IS NOT EMPTY
			""")
	long countAssignedUsers();

	@Query("""
			SELECT COUNT(u)
			FROM User u
			WHERE u.createdAt >= :start
			""")
	long countCreatedToday(@Param("start") LocalDateTime start);

	@Query("""
			SELECT COUNT(u)
			FROM User u
			WHERE u.status = :status
			AND u.createdAt >= :start
			""")
	long countCreatedTodayByStatus(@Param("status") StatusType status, @Param("start") LocalDateTime start);

	@Query("""
			SELECT COUNT(u)
			FROM User u
			WHERE u.role = :role
			AND u.createdAt >= :start
			""")
	long countCreatedTodayByRole(@Param("role") RoleType role, @Param("start") LocalDateTime start);

	@Query("""
			SELECT COUNT(u)
			FROM User u
			WHERE u.assignedSources IS NOT EMPTY
			AND u.createdAt >= :start
			""")
	long countAssignedUsersCreatedToday(@Param("start") LocalDateTime start);

	@Query("""
			SELECT u
			FROM User u
			WHERE (:role IS NULL OR u.role = :role)
			AND (:status IS NULL OR u.status = :status)
			AND (:fromDateTime IS NULL OR u.createdAt >= :fromDateTime)
			AND (:toDateTime IS NULL OR u.createdAt <= :toDateTime)
			AND (
			    :keyword IS NULL
			    OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
			    OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
			    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
			    OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :keyword, '%'))
			    OR LOWER(CONCAT(u.firstName,' ',u.lastName)) LIKE LOWER(CONCAT('%', :keyword, '%'))
			)
			""")
	List<User> findUsersWithFiltersForExport(String keyword, RoleType role, StatusType status,
			LocalDateTime fromDateTime, LocalDateTime toDateTime);

	@Query("""
			SELECT u
			FROM User u
			WHERE u.createdBy.id = :adminId
			AND (:role IS NULL OR u.role = :role)
			AND (:status IS NULL OR u.status = :status)
			AND (:fromDateTime IS NULL OR u.createdAt >= :fromDateTime)
			AND (:toDateTime IS NULL OR u.createdAt <= :toDateTime)
			AND (
			    :keyword IS NULL
			    OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
			    OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
			    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
			    OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :keyword, '%'))
			    OR LOWER(CONCAT(u.firstName,' ',u.lastName)) LIKE LOWER(CONCAT('%', :keyword, '%'))
			)
			""")
	List<User> findUsersWithFiltersForAdminExport(Long adminId, String keyword, RoleType role, StatusType status,
			LocalDateTime fromDateTime, LocalDateTime toDateTime);

}
