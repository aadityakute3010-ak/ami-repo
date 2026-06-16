package com.ami.service.impl;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.ami.dto.requests.CreateAttributeKeyRequestDto;
import com.ami.dto.requests.CreateDeviceAttributeRequestDto;
import com.ami.dto.responses.DeviceAttributeResponseDto;
import com.ami.entity.AttributeKey;
import com.ami.entity.Device;
import com.ami.entity.DeviceAttribute;
import com.ami.entity.User;
import com.ami.enums.RoleType;
import com.ami.repository.AttributeKeyRepository;
import com.ami.repository.DeviceAttributeRepository;
import com.ami.repository.DeviceRepository;
import com.ami.repository.UserRepository;
import com.ami.service.DeviceAttributeService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeviceAttributeServiceImpl implements DeviceAttributeService {

	private final DeviceRepository deviceRepository;
	private final DeviceAttributeRepository deviceAttributeRepository;
	private final AttributeKeyRepository attributeKeyRepository;
	private final UserRepository userRepository;

	private User getLoggedInUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return userRepository.findByEmail(authentication.getName())
				.orElseThrow(() -> new RuntimeException("User not found"));
	}

	@Override
	public List<AttributeKey> getActiveAttributeKeys() {
		return attributeKeyRepository.findByActiveTrue();
	}

	@Override
	public DeviceAttributeResponseDto createAttribute(Long deviceId, CreateDeviceAttributeRequestDto request) {

		User loggedInUser = getLoggedInUser();

		// 1. Get Device
		Device device = deviceRepository.findById(deviceId).orElseThrow(() -> new RuntimeException("Device not found"));

		if (!Boolean.TRUE.equals(device.getActive())) {
			throw new RuntimeException("Device is inactive");
		}

		// 2. ROLE CHECK
		if (loggedInUser.getRole() == RoleType.ADMIN) {

			if (device.getAssignedAdmin() == null || !device.getAssignedAdmin().getId().equals(loggedInUser.getId())) {
				throw new RuntimeException("You can only modify your own devices");
			}

		} else if (loggedInUser.getRole() != RoleType.SUPER_ADMIN) {
			throw new RuntimeException("Access denied");
		}

		// 3. Get AttributeKey from DB
		AttributeKey key = attributeKeyRepository.findById(request.getAttributeKeyId())
				.orElseThrow(() -> new RuntimeException("Invalid attribute key"));

		if (!key.isActive()) {
			throw new RuntimeException("Attribute key is inactive");
		}

		// 4. Prevent duplicate per device
		boolean exists = deviceAttributeRepository.existsByDeviceIdAndAttributeKeyId(deviceId, key.getId());

		if (exists) {
			throw new RuntimeException("Attribute already exists for this device");
		}

		if (request.getAttributeValue() == null || request.getAttributeValue().isBlank()) {
			throw new RuntimeException("Attribute value is required");
		}

		// 5. Create entity
		DeviceAttribute attribute = DeviceAttribute.builder().device(device).attributeKey(key)
				.attributeValue(request.getAttributeValue()).build();

		DeviceAttribute saved = deviceAttributeRepository.save(attribute);

		// 6. Response
		return DeviceAttributeResponseDto.builder().id(saved.getId()).attributeKeyId(saved.getAttributeKey().getId())
				.attributeKey(saved.getAttributeKey().getKeyName()).attributeValue(saved.getAttributeValue()).build();
	}

	@Override
	public List<DeviceAttributeResponseDto> getDeviceAttributes(Long deviceId) {

		User loggedInUser = getLoggedInUser();

		Device device = deviceRepository.findById(deviceId).orElseThrow(() -> new RuntimeException("Device not found"));

		if (!Boolean.TRUE.equals(device.getActive())) {
			throw new RuntimeException("Device is inactive");
		}

		// 1. ACCESS CONTROL
		if (loggedInUser.getRole() == RoleType.ADMIN) {

			if (device.getAssignedAdmin() == null || !device.getAssignedAdmin().getId().equals(loggedInUser.getId())) {
				throw new RuntimeException("Access denied");
			}

		} else if (loggedInUser.getRole() == RoleType.USER) {

			if (device.getAssignedUser() == null || !device.getAssignedUser().getId().equals(loggedInUser.getId())) {
				throw new RuntimeException("Access denied");
			}
		}

		// 2. FETCH
		return deviceAttributeRepository.findByDeviceId(deviceId).stream().map(attr -> DeviceAttributeResponseDto
				.builder().id(attr.getId()).attributeKeyId(attr.getAttributeKey().getId())
				.attributeKey(attr.getAttributeKey().getKeyName()).category(attr.getAttributeKey().getCategory())
				.attributeValue(attr.getAttributeValue()).build()).toList();
	}

	@Override
	public AttributeKey createAttributeKey(CreateAttributeKeyRequestDto request) {

		User user = getLoggedInUser();
		// Only SUPER_ADMIN can add keys
		if (user.getRole() != RoleType.SUPER_ADMIN) {
			throw new RuntimeException("Only Super Admin can create attribute keys");
		}

		// prevent duplicates
		if (attributeKeyRepository.existsByKeyNameIgnoreCase(request.getKeyName())) {
			throw new RuntimeException("Attribute key already exists");
		}

		AttributeKey key = AttributeKey.builder().keyName(request.getKeyName()).unit(request.getUnit())
				.category(request.getCategory()).active(true).build();

		return attributeKeyRepository.save(key);
	}

	@Override
	public AttributeKey updateAttributeKey(Long keyId, CreateAttributeKeyRequestDto request) {

		User user = getLoggedInUser();

		if (user.getRole() != RoleType.SUPER_ADMIN) {
			throw new RuntimeException("Only Super Admin can update attribute keys");
		}

		AttributeKey key = attributeKeyRepository.findById(keyId)
				.orElseThrow(() -> new RuntimeException("Attribute key not found"));

		// prevent duplicate name (if changed)
		if (!key.getKeyName().equals(request.getKeyName())
				&& attributeKeyRepository.existsByKeyNameIgnoreCase(request.getKeyName())) {
			throw new RuntimeException("Attribute key already exists");
		}

		key.setKeyName(request.getKeyName());
		key.setUnit(request.getUnit());
		key.setCategory(request.getCategory());

		return attributeKeyRepository.save(key);
	}

	@Override
	public void deleteAttributeKey(Long keyId) {

		User user = getLoggedInUser();

		if (user.getRole() != RoleType.SUPER_ADMIN) {
			throw new RuntimeException("Only Super Admin can delete attribute keys");
		}

		AttributeKey key = attributeKeyRepository.findById(keyId)
				.orElseThrow(() -> new RuntimeException("Attribute key not found"));

		// IMPORTANT: check usage
		boolean inUse = deviceAttributeRepository.existsByAttributeKeyId(keyId);

		if (inUse) {
			throw new RuntimeException("Cannot delete key. It is used in device attributes.");
		}

		// SOFT DELETE
		key.setActive(false);

		attributeKeyRepository.save(key);
	}

}