package com.example.ex7.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
public class CustomLogoutSuccessHandler implements AuthenticationSuccessHandler {
  private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
  private PasswordEncoder passwordEncoder;

  public CustomLoginSuccessHandler(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest req, HttpServletResponse res, Authentication auth)
      throws IOException, ServletException {
    MemberDTO AuthMemberDTO = (AuthMemberDTO) auth.getPrincipal();

    // 처음 social 로그인 했을 때 페이지 이동
    if (AuthMemberDTO.isFromSocial()
        && passwordEncoder.matches("1", AuthMemberDTO.getPassword())) {
      redirectStrategy.sendRedirect(req, res, "/auth/modify");
      return;
    }

    Collection<GrantedAuthority> authors =
        (Collection<GrantedAuthority>) AuthMemberDTO.getAuthorities();
    List<String> result = authors.stream().map(new Function<GrantedAuthority, String>() {
      @Override
      public String apply(GrantedAuthority grantedAuthority) {
        return grantedAuthority.getAuthority();
      }
    }).sorted().collect(Collectors.toList());
    log.info(">>>>" + result);
    for (int i = 0; i < result.size(); i++) {
      String tmp = "";
      if(result.get(i).equals("ROLE_ADMIN")) tmp = "/sample/admin";
      else if(result.get(i).equals("ROLE_MANAGER")) tmp = "/sample/manager";
      else tmp = "/sample/all";
      res.sendRedirect(req.getContextPath()+tmp);
      break;
    }

  }
}