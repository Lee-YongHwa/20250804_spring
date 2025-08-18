package com.example.ex5.repository.search;

import com.example.ex5.entity.Board;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

// @ManyToOne과 같이 연관관계를 가진 경우 여러 Entity를 JPQL 처리하기 위함
public interface SearchBoardRepository {
  Board searchTest();

  PageImpl<Object[]> searchPage(String type, String keyword, Pageable pageable);
}
