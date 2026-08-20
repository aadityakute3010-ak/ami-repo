package com.ami.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ami.entity.User;
import com.ami.entity.UserLocation;

public interface UserLocationRepository extends JpaRepository<UserLocation, Long> {

	Optional<UserLocation> findByUser(User user);

	void deleteByUser(User user);

	@Query("""
			SELECT ul
			FROM UserLocation ul
			JOIN ul.user u
			WHERE ul.latitude IS NOT NULL
			AND ul.longitude IS NOT NULL
			AND u.status = com.ami.enums.StatusType.ACTIVE
			AND (:adminId IS NULL OR u.createdBy.id = :adminId)
			""")
	List<UserLocation> findUserMapMarkers(@Param("adminId") Long adminId);

}