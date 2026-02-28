package com.Project.TaskManager.security.service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.Project.TaskManager.model.User;

import com.Project.TaskManager.exceptions.ResourceNotFoundException;
import com.Project.TaskManager.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService{

    private final UserRepository userRepository;
    
    
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email)
        throws UsernameNotFoundException{
            User user = userRepository.findByEmail(email)
                        .orElseThrow(()-> new ResourceNotFoundException("User Not Found WIth Email: "+ email));

                return UserDetailsImpl.build(user);
        }    
    }
    
    

