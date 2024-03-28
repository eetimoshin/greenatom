package com.etim.atom.security;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<MyUser> person = userRepository.findPeopleByUsername(username);

        if (person.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        return new MyUserDetails(person.get());
    }

    public ResponseEntity<?> save(MyUser myUser) {
        //отключаем шифрование для заполнения бд из скрипта
//        myUser.setPassword(passwordEncoder.encode(myUser.getPassword()));
        if (userRepository.findPeopleByUsername(myUser.getUsername()).isEmpty()) {
            userRepository.save(myUser);
            return ResponseEntity.status(200).body("Successfully registered!");
        } else {
            return ResponseEntity.status(404).body("This username already exists!");
        }
    }
}
