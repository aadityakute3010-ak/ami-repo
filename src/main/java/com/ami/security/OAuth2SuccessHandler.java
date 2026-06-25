package com.ami.security;

import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.ami.entity.User;
import com.ami.enums.StatusType;
import com.ami.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

	private final UserRepository userRepository;
	private final JwtUtil jwtUtil;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {

		OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

		String email = oauthUser.getAttribute("email");

		User user = userRepository.findByEmail(email).orElse(null);

		if (user == null) {
			response.sendRedirect("http://localhost:5173/unauthorized");
			return;
		}

		if (user.getStatus().equals(StatusType.INACTIVE)) {
			response.sendRedirect("http://localhost:5173/unauthorized");
			return;
		}

		String jwt = jwtUtil.generateToken(user);

		response.sendRedirect("http://localhost:5173/oauth-success?token=" + jwt);
	}
}