package com.ukpatel.expense.tracker.auth.service;

import com.ukpatel.expense.tracker.auth.repo.UserMstRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements UserDetailsService {

    private final UserMstRepo userMstRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userMstRepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not Found!!"));
    }
}
