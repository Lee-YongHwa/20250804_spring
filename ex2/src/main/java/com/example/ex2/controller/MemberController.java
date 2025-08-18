package com.example.ex2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

// template engine을 사용하면 @Controller를 필수로 사용!
@Controller
@RequestMapping("/member") // 클래스 단의 @RequestMapping 생략가능
public class MemberController {

  // void일 경우 요청된 url이 resource 위치와 동일
  @RequestMapping("/join")
  public void join() {

  }

  // String일 경우 요청된 url을 대신하여 임의로 지정가능
  @RequestMapping("/login")
  public String login() {
    return "/member/loginPrc";
  }
}
