package com.example.ex7.service;

import com.example.ex7.dto.ClubMemberDTO;

public interface ClubMemberService {
  void modify(ClubMemberDTO clubMemberDTO);

  void userAccess();
  void managerAccess();
  void adminAccess();
  void selfAccess(String username);
}
