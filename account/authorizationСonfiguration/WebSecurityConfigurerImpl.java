package account.authorizationСonfiguration;

import account.handler.CustomAccessFailureHandler;
import account.handler.CustomAuthenticationEntryPoint;
import account.handler.CustomAuthenticationEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;

@EnableWebSecurity
@Configuration
public class WebSecurityConfigurerImpl extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint()) // Handle auth error
                .and()
                .csrf().disable().headers().frameOptions().disable() // for Postman, the H2 console
                .and()
                .authorizeRequests() // manage access
                .mvcMatchers("/api/signup").permitAll()
                .mvcMatchers("/api/empl/changepass").hasAnyRole("USER", "ACCOUNTANT", "ADMINISTRATOR")
                .mvcMatchers("/api/empl/payment").hasAnyRole("USER", "ACCOUNTANT")
                .mvcMatchers("/api/acct/payments").hasRole("ACCOUNTANT")
                .mvcMatchers("/api/admin/user/**").hasRole("ADMINISTRATOR")
                .mvcMatchers("api/admin/user/access").hasRole("ADMINISTRATOR")
                .mvcMatchers("api/security/events").hasRole("AUDITOR")
                .mvcMatchers(HttpMethod.DELETE, "/api/admin/user/**").hasAuthority("Role_Administrator".toUpperCase())
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().accessDeniedHandler(accessFailureHandler())
                .and()
                .logout().permitAll();
        ;
    }

    @Bean
    public AccessDeniedHandler accessFailureHandler() {
        return new CustomAccessFailureHandler();
    }

    @Bean
    public CustomAuthenticationEventListener myAuthenticationEventListener() {
        return new CustomAuthenticationEventListener();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .authenticationEventPublisher(myAuthenticationEventListener())
                .userDetailsService(userDetailsService).passwordEncoder(getEncoder());
    }

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }


}