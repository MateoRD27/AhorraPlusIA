package com.unimag.ahorroplusia.security.services;

import com.unimag.ahorroplusia.entity.entities.User;
import com.unimag.ahorroplusia.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class JpaUserDetailService  implements UserDetailsService{
    private final UserRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = usuarioRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("\"User Not Found with email"));
        return UserInfoDetail.build(user);
    }
}