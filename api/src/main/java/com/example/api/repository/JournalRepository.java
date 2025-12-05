package com.example.api.repository;

import com.example.api.entity.Journal;
import com.example.api.repository.search.SearchJournalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JournalRepository extends JpaRepository<Journal, Long>, SearchJournalRepository {

  @Query("select j, sum(coalesce(c.likes, 0)), count(distinct c) " +
      "from Journal j left outer join Comments c " +
      "on c.journal = j where j.members.mid = :mid group by j ")
  Page getListMyJournal(Pageable pageable, @Param("mid") Long mid);

  @Query("select j, p, sum(coalesce(c.likes, 0)), count(distinct c) " +
      "from Journal j " +
      "left outer join Photos p on p.journal = j " +
      "left outer join Comments c on c.journal = j " +
      "where j.members.mid = :mid group by j ")
  Page getListMyJournalPhotos(Pageable pageable, @Param("mid") Long mid);


  @Query("select j, p, sum(coalesce(c.likes, 0)), count(distinct c) " +
      "from Journal j " +
      "left outer join Photos p on p.journal = j " +
      "left outer join Comments c on c.journal = j " +
      "where p.pno = (select max(p2.pno) from Photos p2 where p2.journal = j) " +
      "and j.members.mid = :mid group by j ")
  Page getListMyJournalPhotosDefault(Pageable pageable, @Param("mid") Long mid); // 멤버별 저널 보기::my page

  @Query("select j, p, sum(coalesce(c.likes, 0)), count(distinct c) " +
      "from Journal j " +
      "left outer join Photos p on p.journal = j " +
      "left outer join Comments c on c.journal = j " +
      "where p.pno = (select max(p2.pno) from Photos p2 where p2.journal = j) " +
      "group by j ")
  Page getListAllJournalPhotosDefault(Pageable pageable); // 모든 저널 목록 보기::on main

  @Query("select j, p, m, sum(coalesce(c.likes, 0)), count(distinct c) " +
      "from Journal j " +
      "left outer join Photos p on p.journal = j " +
      "left outer join Comments c on c.journal = j " +
      "left outer join Members m on j.members = m " +
      "where j.jno = :jno " +
      "group by p ")
  List<Object[]> getJournalWithAll(@Param("jno") Long jno);  // 저널 상세보기

}
