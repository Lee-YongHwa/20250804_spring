package com.example.ex6.repository;

import com.example.ex6.entity.Member;
import com.example.ex6.entity.Movie;
import com.example.ex6.entity.Review;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReviewRepositoryTests {

  @Autowired
  private ReviewRepository reviewRepository;

  @Test
  public void insertReviews() {
    IntStream.rangeClosed(1, 200).forEach(i->{
      Long mno = (long) (Math.random() * 100) + 1; //Movie  mno
      Long mid = (long) (Math.random() * 100) + 1; //Member mid

      Review review = Review.builder()
          .member(Member.builder().mid(mid).build())
          .movie(Movie.builder().mno(mno).build())
          .grade((int) (Math.random() * 5) + 1)
          .text("이 영화는..." + i)
          .build();
      reviewRepository.save(review);
    });
  }

  //@Transactional  // Review에서 Member에 대하여 Fetch방식이 LAZY라서 사용
  @Test
  public void testFindByMovie() {
    List<Review> result =
        reviewRepository.findByMovie(Movie.builder().mno(20L).build());
    result.forEach(row->{
      System.out.println(row.getReviewNum());
      System.out.println(row.getGrade());
      System.out.println(row.getText());
      System.out.println(row.getMember().getEmail());
    });
  }


}