package com.example.ex8.security.service;

import com.example.ex8.entity.ClubMember;
import com.example.ex8.entity.ClubMemberRole;
import com.example.ex8.repository.ClubMemberRepository;
import com.example.ex8.security.dto.ClubAuthMemberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClubUserDetailService implements UserDetailsService {

  private final ClubMemberRepository clubMemberRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<ClubMember> result = clubMemberRepository.findByEmail(username, false);
    if (!result.isPresent()) throw new UsernameNotFoundException("Check Email or Social");
    ClubMember clubMember = result.get();
    ClubAuthMemberDTO dto = new ClubAuthMemberDTO(
        clubMember.getEmail(), clubMember.getPassword(), clubMember.isFromSocial(),
        clubMember.getRoleSet().stream().map(new Function<ClubMemberRole, SimpleGrantedAuthority>() {
          @Override
          public SimpleGrantedAuthority apply(ClubMemberRole clubMemberRole) {
            return new SimpleGrantedAuthority("ROLE_" + clubMemberRole.name());
          }
        }).collect(Collectors.toSet())
    );
    dto.setName(clubMember.getName());;
    dto.setFromSocial(clubMember.isFromSocial());
    return dto;
  }
}
