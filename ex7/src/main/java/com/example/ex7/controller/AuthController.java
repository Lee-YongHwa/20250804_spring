package com.example.ex7.controller;

import com.example.ex7.dto.ClubMemberDTO;
import com.example.ex7.service.ClubMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
@Log4j2
@RequiredArgsConstructor
public class AuthController {
  private final ClubMemberService clubMemberService;

  @RequestMapping({"/login","/logout","/modify","/accessDenied","/authenticationFailure"})
  public void login(){}

  @PostMapping("/modify")
  public String modify(ClubMemberDTO clubMemberDTO){
    log.info("clubMemberDTO: " + clubMemberDTO);
    clubMemberService.modify(clubMemberDTO);
    return "redirect:/";
  }
}
