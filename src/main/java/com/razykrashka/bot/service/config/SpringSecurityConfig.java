package com.razykrashka.bot.service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                //HTTP Basic authentication
                .httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/**").hasRole("USER")
                .antMatchers(HttpMethod.GET, "/**").hasRole("USER")
                .and()
                .csrf().disable()
                .formLogin().disable();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        String password = "admin";
        String encrytedPassword = this.passwordEncoder().encode(password);

        InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> //
                mngConfig = auth.inMemoryAuthentication();

        UserDetails u1 = User.withUsername("admin").password(encrytedPassword).roles("USER").build();
        UserDetails u2 = User.withUsername("orutsky").password(encrytedPassword).roles("USER").build();

        mngConfig.withUser(u1);
        mngConfig.withUser(u2);

    }
}
