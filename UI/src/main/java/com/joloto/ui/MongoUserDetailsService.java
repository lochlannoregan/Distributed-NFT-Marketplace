package com.joloto.ui;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class MongoUserDetailsService implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    NFTUser user = userRepository.findByUsername(username);

    if (user == null) {
      throw new UsernameNotFoundException("User not found");
    }
    List<SimpleGrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("user"));

    System.out.println("Username found is:" + user.getUsername());
    System.out.println("Password found is:" + user.getPassword());

    return User.builder().username(user.getUsername()).password(user.getPassword()).authorities(authorities).roles("NFTUSER").build();
  }

}
