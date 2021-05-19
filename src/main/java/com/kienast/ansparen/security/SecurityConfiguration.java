package com.kienast.ansparen.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter  {
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
		.cors().and()
		.authorizeRequests()
		.antMatchers(HttpMethod.POST, "/ansparen").permitAll()
		.antMatchers(HttpMethod.GET, "/ansparen").permitAll()
		.antMatchers(HttpMethod.GET, "/").permitAll()
		.antMatchers(HttpMethod.GET, "/swagger-ui.html").permitAll()
		.antMatchers(HttpMethod.GET, "/ansparen/{\\d+}").permitAll();
		//.antMatchers("/**").authenticated();
	// @formatter:on
	}

}
