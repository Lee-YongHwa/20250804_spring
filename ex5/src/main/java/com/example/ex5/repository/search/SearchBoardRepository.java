package com.example.ex5.repository.search;

import com.example.ex5.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// @ManyToOn과 같이 연관관계를 가진 여러 엔티티의 JPQL 처리하기 위함.
public interface SearchBoardRepository {
  Board searchTest();

  Page<Object[]> searchPage(String type, String keyword, Pageable pageable);
}
