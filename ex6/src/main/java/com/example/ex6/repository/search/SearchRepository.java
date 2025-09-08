package com.example.ex6.repository.search;

import com.example.ex6.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// @ManyToOn과 같이 연관관계를 가진 여러 엔티티의 JPQL 처리하기 위함.
public interface SearchRepository {
  Movie searchTest();

//  Page<Object[]> searchPage(String type, String keyword, Pageable pageable);
  Page<Object[]> searchPage(String type, String keyword, Pageable pageable);
}
