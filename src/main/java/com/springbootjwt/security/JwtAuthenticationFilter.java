package com.springbootjwt.security;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	
	private Logger logger=LoggerFactory.getLogger(OncePerRequestFilter.class);
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private JwtHelper jwtHelper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String requestHeader=request.getHeader("Authorization");
		logger.info(" Header: {}"+ requestHeader);
		String username=null;
		String token=null;
		
		if (requestHeader != null && requestHeader.startsWith("Bearer ")) {

			token = requestHeader.substring(7);
			try {

				username = this.jwtHelper.getUsernameFromToken(token);

			} catch (IllegalArgumentException e) {

				logger.info("Illegal arguement during fetching the username !!!");
				e.printStackTrace();

			} catch (ExpiredJwtException e) {

				logger.info("Sorry ! JWT Token has expired");
				e.printStackTrace();

			} catch (MalformedJwtException e) {

				logger.info("Some changes have been made to token !!! Invalid token");
				e.printStackTrace();

			} catch (Exception e) {

				e.printStackTrace();

			}
		}
		
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
		
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			Boolean validateToken=this.jwtHelper.validateToken(token, userDetails);
			if(validateToken) {
				UsernamePasswordAuthenticationToken authentication = 
						new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
			else {
				logger.info("Validation fails !!!");
			}
		}
		filterChain.doFilter(request,response);
	}	
}
