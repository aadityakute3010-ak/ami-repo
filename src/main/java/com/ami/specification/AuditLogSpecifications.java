package com.ami.specification;

import java.util.Collection;

import org.springframework.data.jpa.domain.Specification;

import com.ami.entity.AuditLog;
import com.ami.entity.User;
import com.ami.enums.RoleType;

public final class AuditLogSpecifications {

	private AuditLogSpecifications() {
	}

	public static Specification<AuditLog> module(String module) {
		return (root, query, cb) -> module == null || module.isBlank() ? null : cb.equal(root.get("module"), module);
	}

	public static Specification<AuditLog> entityType(String entityType) {
		return (root, query, cb) -> entityType == null || entityType.isBlank() ? null
				: cb.equal(root.get("entityType"), entityType);
	}

	public static Specification<AuditLog> action(String action) {
		return (root, query, cb) -> action == null || action.isBlank() ? null : cb.equal(root.get("action"), action);
	}

	public static Specification<AuditLog> actionIn(Collection<String> actions) {
		return (root, query, cb) -> actions == null || actions.isEmpty() ? null : root.get("action").in(actions);
	}

	public static Specification<AuditLog> performedBy(String performedBy) {
		return (root, query, cb) -> performedBy == null || performedBy.isBlank() ? null
				: cb.equal(root.get("performedBy"), performedBy);
	}

	// The core visibility rule: SUPER_ADMIN sees everything (no extra predicate);
	// ADMIN sees only their own-scoped entries (targetAdminId = their id) plus
	// global entries (targetAdminId IS NULL).
	public static Specification<AuditLog> visibleTo(User loggedInUser) {

		return (root, query, cb) -> {

			if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {
				return null; // no restriction
			}

			return cb.or(cb.equal(root.get("targetAdminId"), loggedInUser.getId()),
					cb.isNull(root.get("targetAdminId")));
		};
	}
}