package com.ami.service.impl;

import com.ami.service.GeoCodingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class GeoCodingServiceImpl implements GeoCodingService {

	private final Map<String, GeoLocation> cache = new ConcurrentHashMap<>();

	private final HttpClient httpClient = HttpClient.newHttpClient();

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Value("${locationiq.api-key}")
	private String locationIqApiKey;

	@Override
	public GeoLocation getCoordinates(String locationText) {

		if (locationText == null || locationText.isBlank()) {
			return null;
		}

		List<String> attempts = buildSearchAttempts(locationText);

		System.out.println("FINAL LOCATIONIQ SEARCH ATTEMPTS = " + attempts);

		for (String attempt : attempts) {

			String cacheKey = attempt.toLowerCase().trim();

			if (cache.containsKey(cacheKey)) {
				return cache.get(cacheKey);
			}

			GeoLocation location = callLocationIq(attempt);

			if (location != null) {
				cache.put(cacheKey, location);
				return location;
			}
		}

		return null;
	}

	private GeoLocation callLocationIq(String locationText) {

		try {
			String encodedLocation = URLEncoder.encode(locationText, StandardCharsets.UTF_8).replace("+", "%20");

			String url = "https://us1.locationiq.com/v1/search.php" + "?key=" + locationIqApiKey + "&q="
					+ encodedLocation + "&format=json" + "&limit=5" + "&countrycodes=in" + "&addressdetails=1"
					+ "&normalizecity=1";

			System.out.println("LOCATION TEXT = " + locationText);
			System.out.println("LOCATIONIQ URL = " + url);

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("User-Agent", "Mozilla/5.0")
					.header("Accept", "application/json").GET().build();

			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			System.out.println("RESPONSE STATUS = " + response.statusCode());
			System.out.println("RESPONSE BODY = " + response.body());

			if (response.statusCode() == 404) {
				System.out.println("No LocationIQ result found for: " + locationText);
				return null;
			}

			if (response.statusCode() < 200 || response.statusCode() >= 300) {
				System.out.println("LocationIQ failed. Status=" + response.statusCode());
				return null;
			}

			List<Map<String, Object>> responseBody = objectMapper.readValue(response.body(),
					new TypeReference<List<Map<String, Object>>>() {
					});

			if (responseBody == null || responseBody.isEmpty()) {
				System.out.println("No LocationIQ result found for: " + locationText);
				return null;
			}

			for (Map<String, Object> item : responseBody) {

				if (!isAcceptableLocationIqResult(item, locationText)) {
					continue;
				}

				Double latitude = Double.valueOf(item.get("lat").toString());
				Double longitude = Double.valueOf(item.get("lon").toString());

				System.out.println("ACCEPTED LOCATION = " + locationText + ", lat=" + latitude + ", lon=" + longitude
						+ ", displayName=" + item.get("display_name"));

				return new GeoLocation(latitude, longitude);
			}

			System.out.println("All LocationIQ results rejected for: " + locationText);
			return null;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private boolean isAcceptableLocationIqResult(Map<String, Object> item, String locationText) {

		String expectedCity = getExpectedCity(locationText);
		String expectedState = getExpectedState(locationText);
		String expectedCountry = getExpectedCountry(locationText);
		String expectedKeyword = getExpectedKeyword(locationText);

		String displayName = item.get("display_name") != null ? item.get("display_name").toString() : "";

		Object addressObject = item.get("address");

		if (!(addressObject instanceof Map<?, ?> address)) {
			System.out.println("Rejected because address object missing. Result=" + displayName);
			return false;
		}

		String resultState = getAddressValue(address, "state");
		String resultCountry = getAddressValue(address, "country");

		if (!matchesExpectedCity(address, expectedCity)) {
			System.out.println("Rejected city mismatch. Search=" + locationText + ", expectedCity=" + expectedCity
					+ ", result=" + displayName);
			return false;
		}

		if (!containsIgnoreCase(resultState, expectedState)) {
			System.out.println("Rejected state mismatch. Search=" + locationText + ", expectedState=" + expectedState
					+ ", resultState=" + resultState + ", result=" + displayName);
			return false;
		}

		if (!containsIgnoreCase(resultCountry, expectedCountry)) {
			System.out.println("Rejected country mismatch. Search=" + locationText + ", expectedCountry="
					+ expectedCountry + ", resultCountry=" + resultCountry + ", result=" + displayName);
			return false;
		}

		if (!isGenericKeyword(expectedKeyword)) {
			if (!containsIgnoreCase(displayName, expectedKeyword)) {
				System.out.println("Rejected keyword mismatch. Search=" + locationText + ", expectedKeyword="
						+ expectedKeyword + ", result=" + displayName);
				return false;
			}
		}

		return true;
	}

	private String getExpectedKeyword(String locationText) {

		String[] parts = locationText.split(",");

		if (parts.length == 0) {
			return "";
		}

		return removePincode(parts[0]).trim();
	}

	private String getAddressValue(Map<?, ?> address, String key) {

		Object value = address.get(key);

		return value != null ? value.toString() : "";
	}

	private String firstNonBlank(String... values) {

		for (String value : values) {
			if (value != null && !value.isBlank()) {
				return value;
			}
		}

		return "";
	}

	private boolean matchesExpectedCity(Map<?, ?> address, String expectedCity) {

		String resultCity = firstNonBlank(getAddressValue(address, "city"), getAddressValue(address, "town"),
				getAddressValue(address, "village"), getAddressValue(address, "municipality"));

		if (containsIgnoreCase(resultCity, expectedCity)) {
			return true;
		}

		String county = getAddressValue(address, "county");
		String stateDistrict = getAddressValue(address, "state_district");

		return containsIgnoreCase(county, expectedCity) || containsIgnoreCase(stateDistrict, expectedCity);
	}

	private boolean isGenericKeyword(String keyword) {

		if (keyword == null || keyword.isBlank()) {
			return true;
		}

		String normalized = normalize(keyword);

		return normalized.equals("building") || normalized.equals("buildinga") || normalized.equals("buildingb")
				|| normalized.equals("address") || normalized.equals("buildingaddress")
				|| normalized.equals("buildingaaddress") || normalized.equals("buildingbaddress")
				|| normalized.equals("area") || normalized.equals("road") || normalized.equals("lane")
				|| normalized.equals("street");
	}

	private List<String> buildSearchAttempts(String locationText) {

		String cleaned = cleanLocation(locationText);

		String[] splitParts = cleaned.split(",");
		List<String> parts = new ArrayList<>();

		for (String part : splitParts) {
			String value = part.trim();

			if (!value.isBlank()) {
				parts.add(value);
			}
		}

		List<String> attempts = new ArrayList<>();

		if (parts.size() < 3) {
			attempts.add(cleaned);
			return removeDuplicateAttempts(attempts);
		}

		String country = parts.get(parts.size() - 1);
		String state = parts.get(parts.size() - 2);
		String city = parts.get(parts.size() - 3);

		List<String> addressParts = parts.subList(0, parts.size() - 3);

		for (int i = addressParts.size() - 1; i >= 0; i--) {
			String area = removePincode(addressParts.get(i)).trim();

			if (!area.isBlank()) {
				attempts.add(area + ", " + city + ", " + state + ", " + country);
			}
		}

		String pincode = extractPincode(cleaned);

		if (pincode != null) {
			attempts.add(pincode + ", " + city + ", " + state + ", " + country);
		}

		attempts.add(city + ", " + state + ", " + country);

		return removeDuplicateAttempts(attempts);
	}

	private String getExpectedCity(String locationText) {

		String[] parts = locationText.split(",");

		if (parts.length < 3) {
			return "";
		}

		return parts[parts.length - 3].trim();
	}

	private String getExpectedState(String locationText) {

		String[] parts = locationText.split(",");

		if (parts.length < 2) {
			return "";
		}

		return parts[parts.length - 2].trim();
	}

	private String getExpectedCountry(String locationText) {

		String[] parts = locationText.split(",");

		if (parts.length < 1) {
			return "";
		}

		return parts[parts.length - 1].trim();
	}

	private String cleanLocation(String locationText) {

		String[] rawParts = locationText.replaceAll("\\s+", " ").trim().split(",");

		Set<String> uniqueParts = new LinkedHashSet<>();

		for (String part : rawParts) {
			String cleanedPart = part.trim();

			if (!cleanedPart.isBlank()) {
				uniqueParts.add(cleanedPart);
			}
		}

		return String.join(", ", uniqueParts);
	}

	private String extractPincode(String text) {

		java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\\b\\d{6}\\b").matcher(text);

		if (matcher.find()) {
			return matcher.group();
		}

		return null;
	}

	private String removePincode(String text) {
		return text.replaceAll("\\b\\d{6}\\b", "").replaceAll("\\s+", " ").trim();
	}

	private List<String> removeDuplicateAttempts(List<String> attempts) {

		Set<String> uniqueAttempts = new LinkedHashSet<>();

		for (String attempt : attempts) {
			if (attempt != null && !attempt.isBlank()) {
				uniqueAttempts.add(attempt.trim());
			}
		}

		return new ArrayList<>(uniqueAttempts);
	}

	private boolean containsIgnoreCase(String text, String search) {

		if (search == null || search.isBlank()) {
			return true;
		}

		if (text == null) {
			return false;
		}

		return normalize(text).contains(normalize(search));
	}

	private String normalize(String value) {

		if (value == null) {
			return "";
		}

		return value.toLowerCase().replaceAll("[^a-z0-9]", "");
	}
}