package com.example.ex3.repository;

import com.example.ex3.entity.Memo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemoRepositoryTests {
  @Autowired
  MemoRepository memoRepository;

  @Test
  public void testClass() {
    System.out.println(memoRepository.getClass().getName());
  }

  @Test
  public void testInsertDummies() {
    IntStream.rangeClosed(1, 100).forEach(new IntConsumer() {
      @Override
      public void accept(int i) {
        Memo memo = Memo.builder()
            .memoText("Sample... " + i)
            .build();
        memoRepository.save(memo);
      }
    });
  }

  @Test
  public void testSelect() {
    Long mno = 100L;
    Optional<Memo> result = memoRepository.findById(mno);
    if (result.isPresent()) {
      Memo memo = result.get();
      System.out.println(memo);
    }
  }

  @Transactional
  @Test
  public void testSelect2() {
    Long mno = 50L;
    Memo memo = memoRepository.getOne(mno);
    System.out.println(memo);
  }

  @Test
  public void testUpdate() {
    Memo memo = Memo.builder().mno(100L).memoText("Update Text").build();
    System.out.println(memoRepository.save(memo));
  }

  @Test
  public void testDelete() {
    memoRepository.deleteById(100L);
  }

  @Test
  public void testPageDefault() {
    Pageable pageable = PageRequest.of(0, 10); // 0 page에 10개의 데이터를 표기
    Page<Memo> result = memoRepository.findAll(pageable);
    System.out.println(result);
    System.out.println("Total Pages:" + result.getTotalPages()); //총 페이지 수
    System.out.println("Total Count:" + result.getTotalElements()); // 총 개별 건수
    System.out.println("Page Number:" + result.getNumber()); // 현재 페이지 번호::0부터시작
    System.out.println("Page Size:" + result.getSize()); // 페이지당 데이터 갯수
    System.out.println("Next Page:" + result.hasNext()); // 다음 페이지 유무
    System.out.println("First Page:" + result.isFirst()); // 첫페이지 인지 확인
    System.out.println("===================================================");
    for (Memo memo : result.getContent()) {
      System.out.println(memo);
    }
  }

  @Test
  public void testSort() {
    Sort sort1 = Sort.by("mno").descending();
//    Pageable pageable = PageRequest.of(0, 10, sort1);
    Sort sort2 = Sort.by("memoText").ascending();
    Sort sortAll = sort1.and(sort2);
    Pageable pageable = PageRequest.of(5, 10, sortAll);
    Page<Memo> result = memoRepository.findAll(pageable);
    result.get().forEach(memo -> System.out.println(memo));
  }

  @Test
  public void testFindByMnoBetweenOrderByMnoDesc() {
    List<Memo> list = memoRepository.findByMnoBetweenOrderByMnoDesc(1l, 10L);
    for (Memo m : list) System.out.println(m);
  }

  @Test
  public void testFindByMnoBetween() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("mno").descending());
    Page<Memo> result = memoRepository.findByMnoBetween(10l, 30l, pageable);
    result.get().forEach(memo -> System.out.println(memo));
  }

  @Transactional  // 삭제할때 필요함.
  @Commit   // 삭제할때 필요함.
  @Test
  public void testDeleteMemoByMnoLessThan() {
    memoRepository.deleteMemoByMnoLessThan(10L);
  }


  @Test
  public void testGetListDesc() {
    List<Memo> result = memoRepository.getListDesc();
    for(Memo m : result) System.out.println(m);
  }

  @Test
  public void testUpdateMemoText() {
    System.out.println(memoRepository.updateMemoText(10L, "Update ten"));
  }

}