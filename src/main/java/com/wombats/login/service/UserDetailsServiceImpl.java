package com.wombats.login.service;

import com.wombats.login.entity.Authority;
import com.wombats.login.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscar usuario en BD
        com.wombats.login.entity.User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Usuario inexistente"));

        // Mapear lista de Authority con la de Spring security
        List<GrantedAuthority> grantList = new ArrayList<>();
        for(Authority authority : user.getAuthority()) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(authority.getAuthority());
            grantList.add(grantedAuthority);
        }

        // Crear el objeto que va en la sesion
        return (UserDetails) new User(user.getUsername(), user.getPassword(), grantList);
    }

}
