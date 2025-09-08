package com.example.ex7.controller;

import com.example.ex7.security.dto.ClubAuthMemberDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sample")
@Log4j2
public class SampleController {
  @GetMapping("/all")
  public void exAll() {
    log.info("exAll:: 로그인 안해도 접근");
  }

  @GetMapping("/manager")
  public void exManager(@AuthenticationPrincipal ClubAuthMemberDTO clubAuthMemberDTO) {
    log.info("exManager:: 로그인한 사용자만 접근");
    log.info(clubAuthMemberDTO);
  }

  @GetMapping("/admin")
  public void exAdmin() {
    log.info("exAdmin:: 관리자권한만 접근");
  }

}
