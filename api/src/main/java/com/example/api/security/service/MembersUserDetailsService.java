package com.example.api.security.service;

import com.example.api.entity.Members;
import com.example.api.entity.MembersRole;
import com.example.api.repository.MembersRepository;
import com.example.api.security.dto.MembersAuthDTO;
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
public class MembersUserDetailsService implements UserDetailsService {

  private final MembersRepository membersRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    log.info("loadUserByUsername.............");
    Optional<Members> result = membersRepository.findByEmail(username);
    // 데이터베이스에 사용자가 없을 경우 강제로 exception 발생, 프레임웍이 에러메시지를 컨트롤러로 전달
    if(!result.isPresent()) throw new UsernameNotFoundException("Check Email or Social");
    Members clubMember = result.get(); // 사용자가 데이터베이스에 있을 경우 가져옴.
    // MembersAuthDTO::User를 상속 받음, User는 UserDetails를 implement한 객체임.
    MembersAuthDTO dto = new MembersAuthDTO(
        clubMember.getEmail(), clubMember.getPassword(), clubMember.isFromSocial(),
        clubMember.getRoleSet().stream().map(new Function<MembersRole, SimpleGrantedAuthority>() {
          @Override
          public SimpleGrantedAuthority apply(MembersRole clubMemberRole) {
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
