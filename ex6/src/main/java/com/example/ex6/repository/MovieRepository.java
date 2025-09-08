package com.example.ex6.repository;

import com.example.ex6.entity.Movie;
import com.example.ex6.repository.search.SearchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long>, SearchRepository {

  // 2개(Movie와 Review) 조인해서 평점과 갯수만 출력
  @Query("select m, avg(coalesce(r.grade,0)), count(distinct r) " +
      "from Movie m left outer join Review r " +
      "on r.movie = m group by m ")
  Page<Object[]> getListPage(Pageable pageable);

  // 3개(Movie, Review, MovieImage) 조인해서 평점, 갯수, mi 첫번째 등록된 test0.jpg만 출력
  @Query("select m, mi, avg(coalesce(r.grade,0)), count(distinct r) " +
      "from Movie m left outer join Review r on r.movie = m " +
      "left outer join MovieImage mi on mi.movie = m  " +
      "group by m ")
  Page<Object[]> getListPageMi(Pageable pageable);

  // 3개(Movie, Review, MovieImage) 조인해서 평점, 갯수, imgName의 max값 출력(Image정보는 못가져옴)
//  조인 후 특정 기준 그룹핑, 정렬하려면JPQL 또는 QueryDSL에서 명확히 필드 단위로 처리
//  @Query("select m, max(mi.imgName), avg(coalesce(r.grade,0)), count(distinct r) " +
//      "from Movie m left outer join Review r on r.movie = m " +
//      "left outer join MovieImage mi on mi.movie = m  " +
//      "group by m ")
//  Page<Object[]> getListPageMaxMi(Pageable pageable);

  // 3개(Movie, Review, MovieImage) 조인해서 평점, 갯수, 가장최근 등록된 MovieImage 정보1개
  // MovieImage가 비어있을 때 나오지 않는 문제 발생
//  @Query("select m, mi, avg(coalesce(r.grade,0)), count(distinct r) from Movie m " +
//      "left outer join MovieImage mi on mi.movie = m " +
//      "left outer join Review     r  on r.movie  = m " +
//      "where inum = (select max(mi2.inum) from MovieImage mi2 where mi2.movie=m) " +
//      "group by m ")
//  Page<Object[]> getListPageMaxMi(Pageable pageable);

  @Query("select m, mi, avg(coalesce(r.grade,0)), count(distinct r) from Movie m " +
      "left outer join MovieImage mi on mi.movie = m and mi.inum = (" +
      "  select max(mi2.inum) from MovieImage mi2 where mi2.movie = m" +
      ") " +
      "left outer join Review r on r.movie = m " +
      "group by m")
  Page<Object[]> getListPageMaxMi(Pageable pageable);


  // Movie에 대한 정보(Movie, MovieImage 전부)
//  @Query("select m, mi " +
//      "from Movie m left outer join MovieImage mi on mi.movie = m  " +
//      "where m.mno=:mno ")
//  List<Object[]> getMovieWithAll(Long mno);

  // Movie에 대한 정보(Movie, MovieImage 전부, 평점, 댓글 갯수)
  @Query("select m, mi, avg(coalesce(r.grade,0)), count(r) " +
      "from Movie m left outer join MovieImage mi on mi.movie = m  " +
      "left outer join Review r on r.movie = m " +
      "where m.mno=:mno group by mi ")
  List<Object[]> getMovieWithAll(Long mno);

}
