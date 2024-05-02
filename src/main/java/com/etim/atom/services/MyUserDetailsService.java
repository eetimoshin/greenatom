package com.etim.atom.services;

import com.etim.atom.security.MyUser;
import com.etim.atom.security.MyUserDetails;
import com.etim.atom.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findPeopleByUsername(username)
                .map(MyUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public ResponseEntity<?> save(MyUser myUser) {
        //отключаем шифрование для заполнения бд из скрипта
        myUser.setPassword(passwordEncoder.encode(myUser.getPassword()));
        if (userRepository.findPeopleByUsername(myUser.getUsername()).isEmpty()) {
            userRepository.save(myUser);
            return ResponseEntity.status(200).body("Successfully registered!");
        } else {
            return ResponseEntity.status(404).body("This username already exists!");
        }
    }
}
