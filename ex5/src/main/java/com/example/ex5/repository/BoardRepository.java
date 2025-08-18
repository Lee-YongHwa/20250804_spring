package com.example.ex5.repository;

import com.example.ex5.entity.Board;
import com.example.ex5.repository.search.SearchBoardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long>
    , SearchBoardRepository {
// SearchBoardRepository를 별도로 사용하는 이유 : 복수개의 엔티티를 검색하기 위해 별도의 interface 로 분리

//   JPQL  :: JPA(Java Persistence API)+SQL 에서 사용하는 객체지향 쿼리 언어
//   select b, r from Board b left join Reply r on r.board = b where b.bno=100;

//   SQL :: RDBMS에서 사용하는 구조화된 쿼리 언어
//   select b.*, r.* from db7.board b, db7.reply r where b.bno = r.board_bno and b.bno =100;


  // 연관관계 있는 경우 :: board가 member를 참조하기 때문에 left join을 적용
  @Query("select b, w from Board b left join b.writer w where b.bno=:bno ")
  Object getBoardWithWriter(Long bno);

  // 연관관계 없는 경우 :: board가 reply를 참조하지 않기 때문에 left join ON 을 적용
  @Query("select b, r from Board b left join Reply r on r.board = b where b.bno=:bno ")
  List<Object[]> getBoardWithReply(Long bno);

  // ex4에서는 Page를 구할 때 findAll()를 활용해서 카운트를 구할 필요가 없었음.
  // 그러나 @Query를 사용해서 Page를 구할 때는 countQuery에 대한 내용도 적시 해야 함.
  @Query(value = "select b, w, count(r) from Board b " +
      "left join b.writer w " +
      "left join Reply r ON r.board = b " +
      "group by b "
      , countQuery = "select count(b) from Board b ")
  Page<Object[]> getBoardWithReplyCount(Pageable pageable);

  @Query("select b, w, count(r) from Board b " +
      "left join b.writer w " +
      "left join Reply r ON r.board = b " +
      "where b.bno=:bno " )
  Object getBoardByBno(Long bno);
}
