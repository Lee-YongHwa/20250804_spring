package com.example.ex8.security.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Log4j2
@Getter
@Setter
@ToString
// ClubAuthMemberDTO:: session을 생성할 때 로그인정보를 담기 위한 객체
// 데이터베이스로부터의 정보를 User에, social에서의 정보를 OAuth2User에 처리
public class ClubAuthMemberDTO extends User implements OAuth2User {

  private String email;
  private String password;
  private String name;
  private boolean fromSocial;

  private Map<String, Object> attr; // 각종 social로부터 오는 정보를 담기 위한 변수

  public ClubAuthMemberDTO(
      String username, String password,
      boolean fromSocial,
      Collection<? extends GrantedAuthority> authorities,
      String email, String name
  ) {
    super(username, password, authorities);
    this.email = email;this.password = password;this.name=name; this.fromSocial=fromSocial;
  }

  public ClubAuthMemberDTO(String username, String password, boolean fromSocial,
                           Collection<? extends GrantedAuthority> authorities,
                           Map<String, Object> attr) {
    this(username, password, fromSocial, authorities, username, username);
    this.attr = attr;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return attr;
  }
}
