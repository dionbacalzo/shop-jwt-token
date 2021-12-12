package com.shop.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.shop.security.RestAuthenticationEntryPoint;
import com.shop.security.ShopAccessDeniedHandler;
import com.shop.security.ShopAuthenticationSuccessHandler;
import com.shop.security.ShopLogoutSuccessHandler;
import com.shop.service.LoginManagerImpl;
import com.shop.util.JwtAuthenticationEntryPoint;
import com.shop.util.JwtRequestFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableMongoRepositories(basePackages = "com.shop.dao")
@ComponentScan(basePackages = {"com.shop.security","com.shop.util"})
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
	public SecurityConfig() {
        super();
    }
	
    @Autowired
    private ShopAccessDeniedHandler accessDeniedHandler;
    
    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    
    @Autowired
    private ShopAuthenticationSuccessHandler mySuccessHandler;
    
    @Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Autowired
	private JwtRequestFilter jwtRequestFilter;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth.authenticationProvider(new LoginManagerImpl());
      auth.userDetailsService(userDetailsService);
    }
    
    @Override
    public void configure(WebSecurity web) throws Exception {
      web
        .ignoring()
           .antMatchers("/resources/**");
    }
    
    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new ShopLogoutSuccessHandler();
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	http.csrf().disable()
        .cors()
        .and()
        .exceptionHandling()
	        .accessDeniedHandler(accessDeniedHandler)
	        .authenticationEntryPoint(restAuthenticationEntryPoint)
        .and()
        .authorizeRequests()
	        .antMatchers("/content").permitAll()
	        .antMatchers("/login").permitAll()
	        .antMatchers("/profile").authenticated()
	        .antMatchers("/upload").authenticated()
	        .antMatchers("/save").authenticated()
	        .antMatchers("/delete").authenticated()
	        .antMatchers("/uploadItems").authenticated()
	        .antMatchers("/updateUser").authenticated()
	        .antMatchers("/updatePassword").authenticated()
	        .antMatchers("/admin").hasAuthority("ADMIN")
	        .antMatchers("/accountResetList").hasAuthority("ADMIN")
	        .antMatchers("/resetAccount").hasAuthority("ADMIN")
        .and()
        // make sure we use stateless session; session won't be used to store user's state.
		.exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
		.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    	// Add a filter to validate the tokens with every request
    	http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);;
    }
}