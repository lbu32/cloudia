package com.cloudia.configuration;

import com.cloudia.service.AuthenticatedUserService;
import com.cloudia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackageClasses = AuthenticatedUserService.class)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{

    //PasswordEncoder, der eine starke Hashfunktion nutzt. Passwörter werden nicht im Klartext in der DB gespeichert.
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;


    @Autowired
    private UserRepository userRepository;

    /**Interface, das eine Standardmethode zum Arbeiten mit Datenbankverbindungen bietet, check credentials i
     Datei application.properties */
    @Autowired
    private DataSource dataSource;

    // Referenz zu den Datenbankabfragen, siehe Queries in application.properties
    @Value("${spring.queries.users-query}")
    private String usersQuery;

    @Value("${spring.queries.roles-query}")
    private String rolesQuery;


    /**
     Der AuthenticationManagerBuilder identifiziert User auf Basis des
     usersQuery, rolesQuery, dataSource, bCryptPasswordEncoder.
     */
    @Autowired
    public void globalSecurityConfiguration(AuthenticationManagerBuilder auth) throws Exception {
        auth.
                userDetailsService(userDetailsService);
    }



    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.
                jdbcAuthentication()
                .usersByUsernameQuery(usersQuery)
                .authoritiesByUsernameQuery(rolesQuery)
                .dataSource(dataSource)
                .passwordEncoder(bCryptPasswordEncoder);
    }


    /**Der antMatcher gewährt Zugang basierend auf Rollen, den Parametern für Login,
     *  zum Exception Handling oder Erfolgseite
     *  https://docs.spring.io/spring-security/site/docs/4.1.3.RELEASE/reference/htmlsingle/#multiple-httpsecurity
     *
     *  CSRF = Cross-Site Request Forgery
     *  CSRF ist ein Angriff der den User austrickst, indem er dazu verleitet wird, eine
     *  verseuchte Anfrage zu stellen .
    */

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.
                authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/registration").permitAll()
                .antMatchers("/user/**").hasAuthority("USER")
                .antMatchers("/admin/**").hasAuthority("ADMIN").anyRequest()
                .authenticated().and().csrf().disable().formLogin() // CSRF prüft nicht nach einem Token bei der Anmeldung
                .loginPage("/login").failureUrl("/login?error=true")
                .defaultSuccessUrl("/home")
                .usernameParameter("email")
                .passwordParameter("password")
                .and().logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/").and().exceptionHandling()
                .accessDeniedPage("/access-denied");
    }



}
