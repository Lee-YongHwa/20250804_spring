package com.example.ex7.service;

import com.example.ex7.dto.ClubMemberDTO;
import com.example.ex7.entity.ClubMember;
import com.example.ex7.repository.ClubMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class ClubMemberServiceImpl implements ClubMemberService {
  private final ClubMemberRepository clubMemberRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void modify(ClubMemberDTO clubMemberDTO) {
    log.info(">>>" + clubMemberDTO);
    Optional<ClubMember> result = clubMemberRepository.findByEmail(clubMemberDTO.getEmail());
    ClubMember clubMember = null;
    if (result.isPresent()) {
      clubMember = result.get();
    }
    clubMember.setName(clubMemberDTO.getName());
    clubMember.setPassword(passwordEncoder.encode(clubMemberDTO.getPassword()));
    clubMemberRepository.save(clubMember);
  }

  @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
  @Override
  public void userAccess() {

  }

  @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
  @Override
  public void managerAccess() {

  }

  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public void adminAccess() {

  }

  @PreAuthorize("#username == authentication.name")
  @Override
  public void selfAccess(String username) {

  }
}
