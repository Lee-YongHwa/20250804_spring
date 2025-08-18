package com.example.ex5.repository.search;

import com.example.ex5.entity.Board;
import com.example.ex5.entity.QBoard;
import com.example.ex5.entity.QMember;
import com.example.ex5.entity.QReply;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

// ex4에서는 QuerydslPredicateExecutor를 활용했지만
// ex5에서는 클래스에 상속을 이용하여 QuerydslRepositorySupport를 사용.
// 목적 : Q도메인을 활용한 동적검색을 할 수 있다.
@Log4j2
public class SearchBoardRepositoryImpl extends QuerydslRepositorySupport
    implements SearchBoardRepository {

  public SearchBoardRepositoryImpl() {
    super(Board.class);
  }

  @Override
  public Board searchTest() {
    log.info("search Test.......");
    QBoard qBoard = QBoard.board;
    QReply qReply = QReply.reply;
    QMember qMember = QMember.member;

    JPQLQuery<Board> jpqlQuery = from(qBoard);
    jpqlQuery.leftJoin(qMember).on(qBoard.writer.eq(qMember));
    jpqlQuery.leftJoin(qReply).on(qReply.board.eq(qBoard));


//      jpqlQuery.select(qBoard, qMember, qReply.count()).where(qBoard.bno.eq(50l));
//      log.info(jpqlQuery);
//      List<Board> result = jpqlQuery.fetch();
//      log.info(">>>" + result);

    JPQLQuery<Tuple> tuple = jpqlQuery.select(qBoard, qMember, qReply.count());
    tuple.groupBy(qBoard);
    log.info(tuple);
    List<Tuple> result = tuple.fetch();
    log.info(result);

    return null;
  }

  // JPQLQuery를 이용하여 Page<Object[]> 생성 목적
  @Override
  public PageImpl<Object[]> searchPage(String type, String keyword, Pageable pageable) {
    log.info("Search Page...");

    // 1) 도메인을 확보
    QBoard qBoard = QBoard.board;
    QReply qReply = QReply.reply;
    QMember qMember = QMember.member;

    // 2) 도메인을 조인
    JPQLQuery<Board> jpqlQuery = from(qBoard);
    jpqlQuery.leftJoin(qMember).on(qBoard.writer.eq(qMember));
    jpqlQuery.leftJoin(qReply).on(qReply.board.eq(qBoard));

    // 3) Tuple 생성 : 조인한 개체와 select를 활용해서 필요한 데이터를 tuple로 생성 
    JPQLQuery<Tuple> tuple = jpqlQuery.select(qBoard, qMember, qReply.count());

    // 4) 조건절 검색을 위한 객체 생성
    BooleanBuilder builder = new BooleanBuilder();
    BooleanExpression expression = qBoard.bno.gt(0l); // 모든 데이터를 들고 오기위한 밑밥
    builder.and(expression); // 검색 기본 객체로 먼저 결함

    // 5) 검색조건 추가
    if (type != null) {
      String[] typeArr = type.split(""); // ""의 의미는 한글자씩 분리함
      BooleanBuilder conditionBuilder = new BooleanBuilder();
      for (String t : typeArr) {
        switch (t) {  // 화살표연산자 활용시 break 생략가능, default 필히 포함 (java 14+)
          case "t" -> conditionBuilder.or(qBoard.title.contains(keyword));
          case "w" -> conditionBuilder.or(qMember.email.contains(keyword));
          default -> conditionBuilder.or(qBoard.content.contains(keyword));
        }
      } // 반복문 돌면서 발생한 조건 누적
      builder.and(conditionBuilder); // 누적된 조건을 최초 조건과 합체
    }

    // 6) join된 tuple에 추가된 조건절 적용
    tuple.where(builder);

    // 7) 조인된 데이터의 select를 위한 group by 설정
    tuple.groupBy(qBoard);

    // 8) 정렬조건 생성
    Sort sort = pageable.getSort();
    sort.stream().forEach(new Consumer<Sort.Order>() {
      @Override
      public void accept(Sort.Order order) {
        Order direction = order.isAscending() ? Order.ASC : Order.DESC;
        String prop = order.getProperty();
        PathBuilder orderbyExpression = new PathBuilder(Board.class, "board");
        tuple.orderBy(new OrderSpecifier<Comparable>(direction, orderbyExpression));
      }
    });

    // 9) tuple의 데이터를 가져오기 위한 시작 위치 지정(offset 지정)
    tuple.offset(pageable.getOffset());

    // 10) tuple의 데이터를 가져올 때 개수 지정
    tuple.limit(pageable.getPageSize());

    // 11) 최정결과를 tuple의 fetch()를 통해 컬렉션으로 변환
    List<Tuple> result = tuple.fetch();

    // 12) tuple의 검색 결과 개수
    long count = tuple.fetchCount();
    log.info("총 갯수 출력" + count);

    // 13) Page객체를 PageImpl 객체로 변환
    return new PageImpl<Object[]>(result.stream().map(new Function<Tuple, Object[]>() {
      @Override
      public Object[] apply(Tuple tuple) {
        return tuple.toArray();
      }
    }).collect(Collectors.toList()), pageable, count);
  }
}
