package com.ami.security;

import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.ami.entity.User;
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
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter().write("""
				{"message":"User is not registered in the system"}
				""");
			return;
		}
		
		if (!Boolean.TRUE.equals(user.getActive())) {
		    throw new RuntimeException("Account is inactive");
		}

		String jwt = jwtUtil.generateToken(user);

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		response.getWriter().write("{\"token\":\"" + jwt + "\"}");
	}
}