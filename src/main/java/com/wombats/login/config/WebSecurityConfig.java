package com.wombats.login.config;

import com.wombats.login.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    // Util para evitar que la seguridad se aplique a los resources
    String[] resources = new String[]{"/include/**", "/css/**", "/icons/**", "/img/**", "/js/**", "/layer/**"};

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeRequests() // Permite restringir y/o dar acceso request HTTP
                // antMatchers - Lista de URL que corresponden a un RequestMapping
                .antMatchers(resources).permitAll() // Especifica que estas URLs son accesibles por cualquiera
                .antMatchers("/", "/index").permitAll()
                .antMatchers("/admin").access("hasRole('ADMIN')")
                .antMatchers("/user").access("hasRole('USER') or hasRole('ADMIN')") // Permite el acceso cumpliendo la expresion “hasRole"
                .anyRequest().authenticated() // Permite y da acceso a cualquier usuario que este autenticado a los demas EP
                .and()
                .formLogin() // Permite personalizar el proceso de inicio de sesion
                .loginPage("/login") // Indica la url de la pagina de inicio de sesion
                .permitAll()
                .defaultSuccessUrl("/menu") // Indica a cual URL sera redirigido cuando el usuario inicie sesion.
                .failureUrl("/login?error=true") // Indica a cual URL sera redirigido cuando el inicio de sesion falla.
                // Indica el nombre de los parametros respectivamente.
                .usernameParameter("username")
                .passwordParameter("password")
                .and()
                .logout() // Personaliza el proceso de cierre de sesion
                .permitAll()
                .logoutSuccessUrl("/login?logout"); // Indica la URL donde sera redirigido cuando el usuario cierre sesion;
    }

    //BCryptPasswordEncoder bCryptPasswordEncoder;

    // Encriptador de contrasenas
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(4);
    }

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    // Registra el servicio para usuarios y el encriptador de contrasenas
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

}
