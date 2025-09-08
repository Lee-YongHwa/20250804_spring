package com.example.ex8.security.service;

import com.example.ex8.entity.ClubMember;
import com.example.ex8.entity.ClubMemberRole;
import com.example.ex8.repository.ClubMemberRepository;
import com.example.ex8.security.dto.ClubAuthMemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class ClubUserDetailsService implements UserDetailsService {

  private final ClubMemberRepository clubMemberRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<ClubMember> result = clubMemberRepository.findByEmail(username);
    // 데이터베이스에 사용자가 없을 경우 강제로 exception 발생, 프레임웍이 에러메시지를 컨트롤러로 전달
    if(!result.isPresent()) throw new UsernameNotFoundException("Check Email or Social");
    ClubMember clubMember = result.get(); // 사용자가 데이터베이스에 있을 경우 가져옴.
    // ClubAuthMemberDTO::User를 상속 받음, User는 UserDetails를 implement한 객체임.
    ClubAuthMemberDTO dto = new ClubAuthMemberDTO(
        clubMember.getEmail(), clubMember.getPassword(), clubMember.isFromSocial(),
        clubMember.getRoleSet().stream().map(new Function<ClubMemberRole, SimpleGrantedAuthority>() {
          @Override
          public SimpleGrantedAuthority apply(ClubMemberRole clubMemberRole) {
            return new SimpleGrantedAuthority("ROLE_"+clubMemberRole.name());
          }
        }).collect(Collectors.toSet())
        , clubMember.getEmail(), clubMember.getName()
    );
    dto.setName(clubMember.getName());
    dto.setFromSocial(clubMember.isFromSocial());
    return dto;  //ClubAuthMemberDTO를 리턴함으로써 session에 저장됨.
  }
}
