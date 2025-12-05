package com.example.api.repository.search;

import com.example.api.entity.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
public class SearchJournalRepositoryImpl extends QuerydslRepositorySupport implements SearchJournalRepository {
  public SearchJournalRepositoryImpl() {
    super(Journal.class);
  }

  @Override
  public Page<Object[]> searchPage(String type, String keyword, Pageable pageable) {
    log.info("Search Page....");

    // 1) 도메인을 확보
    QJournal qJournal = QJournal.journal;
    QPhotos qPhotos = QPhotos.photos;
    QMembers qMembers = QMembers.members;
    QComments qComments = QComments.comments;

    // 2) 도메인을 조인
    JPQLQuery<Journal> jpqlQuery = from(qJournal);
    jpqlQuery.leftJoin(qPhotos).on(qPhotos.journal.eq(qJournal));
    jpqlQuery.leftJoin(qComments).on(qComments.journal.eq(qJournal));
    jpqlQuery.leftJoin(qMembers).on(qJournal.members.eq(qMembers));

    // 3) Tuple 생성:조인한 객체와 select를 활용해서 필요한 데이터를 tuple로 생성
    JPQLQuery<Tuple> tuple = jpqlQuery.select(
        qJournal, qPhotos, qMembers, qComments.likes.sum().coalesce(0L), qComments.count()
    );

    // 4) 조건절 검색을 위한 객체 생성
    BooleanBuilder builder = new BooleanBuilder();
    BooleanExpression expression = qJournal.jno.gt(0l); // 모든 데이터를 위한 조건지정
    builder.and(expression); // 검색 기본 객체로 먼저 결합

    // 5) 검색조건 추가
    if (type != null) {
      String[] typeArr = type.split(""); // ""의 의미는 한글자씩 분리함
      BooleanBuilder conditionBuilder = new BooleanBuilder();
      for (String t : typeArr) {
        switch (t) { // 화살표연산자 활용시 break 생략가능, default 필히포함(java 14+)
          case "t" -> conditionBuilder.or(qJournal.title.contains(keyword));
          case "w" -> conditionBuilder.or(qMembers.email.contains(keyword));
          default -> conditionBuilder.or(qJournal.content.contains(keyword));
        }
      } // 반복문 돌면서 발생한 조건 누적
      builder.and(conditionBuilder); //누적된 조건을 최초 조건과 합체
    }

    // 6) 조인된 tuple에 추가된 조건절 적용
    tuple.where(builder);

    // 7) 조인된 데이터의 select를 위한 group by 설정
    tuple.groupBy(qJournal);

    // 8) 정렬조건 생성
    Sort sort = pageable.getSort();
    sort.stream().forEach(order -> {
      Order direction = order.isAscending() ? Order.ASC : Order.DESC;
      String prop = order.getProperty();
      PathBuilder orderByExpression = new PathBuilder(Journal.class, "journal");
      tuple.orderBy(new OrderSpecifier<Comparable>(direction, orderByExpression.get(prop)));
    });

    // 9) tuple의 데이터를 가져오기 위한 시작 위치 지정(offset 지정)
    tuple.offset(pageable.getOffset());

    // 10) tuple의 데이터를 가져올 때 개수 지정
    tuple.limit(pageable.getPageSize());

    // 11) 최종결과를 tuple의 fetch()를 통해서 컬렉션으로 변환
    List<Tuple> result = tuple.fetch();

    // 12) tuple의 검색 결과 개수
    long count = tuple.fetchCount();
    log.info("총 개수 출력" + count);

    // 13) Page 객체를 PageImpl 객체로  변환
    return new PageImpl<Object[]>(result.stream().map(new Function<Tuple, Object[]>() {
      @Override
      public Object[] apply(Tuple tuple) {
        return tuple.toArray();
      }
    }).collect(Collectors.toList()), pageable, count);
  }
}
