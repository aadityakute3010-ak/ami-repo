package com.ami.specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;

import com.ami.dto.requests.PayloadFilterRequest;
import com.ami.entity.Device;
import com.ami.entity.Meter;
import com.ami.entity.Payload;
import com.ami.entity.User;
import com.ami.enums.DeviceStatus;
import com.ami.enums.RoleType;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public final class PayloadSpecification {

	private PayloadSpecification() {
	}

	public static Specification<Payload> build(PayloadFilterRequest request, User loggedInUser, LocalDateTime from,
			LocalDateTime to) {

		return (root, query, criteriaBuilder) -> {

			/*
			 * Prevent duplicate Payload rows if joins are later expanded.
			 */
			query.distinct(true);

			Join<Payload, Device> deviceJoin = root.join("device", JoinType.INNER);

			Join<Device, Meter> meterJoin = deviceJoin.join("meter", JoinType.INNER);

			List<Predicate> predicates = new ArrayList<>();

			/*
			 * Existing repository behaviour: payloads linked to inactive meters are
			 * excluded.
			 */
			predicates.add(criteriaBuilder.notEqual(meterJoin.get("status"), DeviceStatus.INACTIVE));

			addRoleAccessPredicate(loggedInUser, deviceJoin, criteriaBuilder, predicates);

			addLikePredicate(request.getDeviceId(), deviceJoin.get("deviceId"), criteriaBuilder, predicates);

			addLikePredicate(request.getConsumer(), root.get("consumerNumber"), criteriaBuilder, predicates);

			/*
			 * Existing business mapping: meterNumber is the same value as Device.deviceId.
			 */
			addLikePredicate(request.getMeterNumber(), deviceJoin.get("deviceId"), criteriaBuilder, predicates);

			addLikePredicate(request.getMacAddress(), deviceJoin.get("macAddress"), criteriaBuilder, predicates);

			if (request.getStatus() != null) {
				predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
			}

			if (request.getSourceType() != null) {
				predicates.add(criteriaBuilder.equal(meterJoin.get("sourceType"), request.getSourceType()));
			}

			if (from != null) {
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("receivedAt"), from));
			}

			if (to != null) {
				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("receivedAt"), to));
			}

			if (request.getMinBattery() != null) {
				predicates.add(
						criteriaBuilder.greaterThanOrEqualTo(root.get("batteryPercentage"), request.getMinBattery()));
			}

			if (request.getMaxBattery() != null) {
				predicates
						.add(criteriaBuilder.lessThanOrEqualTo(root.get("batteryPercentage"), request.getMaxBattery()));
			}

			if (request.getMinSignal() != null) {
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("signalQuality"), request.getMinSignal()));
			}

			if (request.getMaxSignal() != null) {
				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("signalQuality"), request.getMaxSignal()));
			}

			addSearchPredicate(request.getSearch(), root, deviceJoin, meterJoin, criteriaBuilder, predicates);

			return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
		};
	}

	private static void addRoleAccessPredicate(User loggedInUser, Join<Payload, Device> deviceJoin,
			jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {

		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {
			return;
		}

		if (loggedInUser.getRole() == RoleType.ADMIN) {
			predicates.add(criteriaBuilder.equal(deviceJoin.get("assignedAdmin").get("id"), loggedInUser.getId()));
			return;
		}

		/*
		 * Preserves the existing repository/service behaviour: non-Super-Admin and
		 * non-Admin users see payloads assigned through Device.assignedUser.
		 */
		predicates.add(criteriaBuilder.equal(deviceJoin.get("assignedUser").get("id"), loggedInUser.getId()));
	}

	private static void addLikePredicate(String value, jakarta.persistence.criteria.Expression<String> field,
			jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {

		String normalizedValue = normalize(value);

		if (normalizedValue == null) {
			return;
		}

		predicates.add(criteriaBuilder.like(criteriaBuilder.lower(field), containsPattern(normalizedValue)));
	}

	private static void addSearchPredicate(String search, jakarta.persistence.criteria.Root<Payload> root,
			Join<Payload, Device> deviceJoin, Join<Device, Meter> meterJoin,
			jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {

		String normalizedSearch = normalize(search);

		if (normalizedSearch == null) {
			return;
		}

		String pattern = containsPattern(normalizedSearch);

		List<Predicate> searchPredicates = new ArrayList<>();

		searchPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(deviceJoin.get("deviceId")), pattern));

		searchPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(deviceJoin.get("deviceName")), pattern));

		searchPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(deviceJoin.get("serialNumber")), pattern));

		searchPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(deviceJoin.get("macAddress")), pattern));

		searchPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(meterJoin.get("meterName")), pattern));

		searchPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("consumerNumber")), pattern));

		searchPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("failureReason")), pattern));

		predicates.add(criteriaBuilder.or(searchPredicates.toArray(Predicate[]::new)));
	}

	private static String normalize(String value) {

		if (value == null || value.isBlank()) {
			return null;
		}

		return value.trim().toLowerCase(Locale.ROOT);
	}

	private static String containsPattern(String value) {
		return "%" + value + "%";
	}
}