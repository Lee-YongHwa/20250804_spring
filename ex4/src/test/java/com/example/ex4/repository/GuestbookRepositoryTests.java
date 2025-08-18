package com.example.ex4.repository;

import com.example.ex4.entity.Guestbook;
import com.example.ex4.entity.QGuestbook;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GuestbookRepositoryTests {

  @Autowired
  private GuestbookRepository guestbookRepository;

  @Test
  public void insertDummies() {
    IntStream.rangeClosed(1, 300).forEach(i -> {
      Guestbook gb = Guestbook.builder()
          .title("Title..." + i)
          .content("Content..." + i)
          .writer("user" + (i % 10))
          .build();
      guestbookRepository.save(gb);
    });
  }

  @Test
  public void updateTest() {
    // Optional 데이터가 하나일 경우 사용, null도 수용가능.
    Optional<Guestbook> result = guestbookRepository.findById(300l);
    if (result.isPresent()) {
      Guestbook guestbook = result.get();
      guestbook.changeTitle("Change Title...");
      guestbook.changeContent("Change Content...");
      guestbookRepository.save(guestbook);
    }
  }

  @Test
  public void testQuerydsl() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("gno").descending());
    QGuestbook qGuestbook = QGuestbook.guestbook;
    String keyword = "1";
    BooleanBuilder builder = new BooleanBuilder();
    BooleanExpression expression = qGuestbook.title.contains(keyword);
    builder.and(expression);
    Page<Guestbook> result = guestbookRepository.findAll(builder, pageable);
    result.stream().forEach(gb -> System.out.println(gb));
  }
  @Test
  public void testQuerydsl2() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("gno").descending());
    QGuestbook qGuestbook = QGuestbook.guestbook;
    String keyword = "1";
    BooleanBuilder builder = new BooleanBuilder();

    BooleanExpression expression1 = qGuestbook.title.contains(keyword);
    BooleanExpression expression2 = qGuestbook.content.contains(keyword);
    BooleanExpression expressionAll = expression1.or(expression2);

    builder.and(expressionAll);
    Page<Guestbook> result = guestbookRepository.findAll(builder, pageable);
    result.stream().forEach(gb -> System.out.println(gb));
  }
}