package com.example.ex6.repository;

import com.example.ex6.entity.Member;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberRepositoryTests {

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  ReviewRepository reviewRepository;

  @Test
  public void insertMembers() {
    IntStream.rangeClosed(1, 100).forEach(i->{
      Member member = Member.builder()
          .email(String.format("r%d@a.a", i))
          .pw("1")
          .nickname("reviewer" + i)
          .build();
      memberRepository.save(member);
    });
  }

  @Transactional
  @Commit
  @Test
  public void testDeleteByMember() {
    //순서 주의
    Long mid = 50l;
    reviewRepository.deleteByMember(Member.builder().mid(mid).build());
    memberRepository.deleteById(mid);
  }
}