package com.example.ex3.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
public class MemberController {

  @GetMapping("join")
  public void memberJoin() {

  }
  @GetMapping("list")
  public void memberList() {

  }
}
