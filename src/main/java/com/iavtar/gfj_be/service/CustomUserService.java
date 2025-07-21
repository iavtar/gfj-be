package com.iavtar.gfj_be.service;

import com.iavtar.gfj_be.entity.AppUser;
import com.iavtar.gfj_be.entity.Role;
import com.iavtar.gfj_be.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserService implements UserDetailsService {

    @Autowired
    private AppUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AppUser> appUser = userRepository.findByUsername(username);
        if (appUser.isPresent()) {
            return new User(appUser.get().getUsername(), appUser.get().getPassword(), mapRolesToAuthorities(appUser.get().getRoles()));
        } else {
            throw new UsernameNotFoundException("Username not found");
        }
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName().getValue())).collect(Collectors.toList());
    }

}
