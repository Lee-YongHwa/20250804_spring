package com.example.ex6.service;

import com.example.ex6.dto.MovieDTO;
import com.example.ex6.dto.MovieImageDTO;
import com.example.ex6.dto.PageRequestDTO;
import com.example.ex6.dto.PageResultDTO;
import com.example.ex6.entity.Movie;
import com.example.ex6.entity.MovieImage;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface MovieService {
  default Map<String, Object> dtoToEntity(MovieDTO movieDTO) {
    // movie, imgList 라는 키를 가질수 있는 entityMap을 선언
    Map<String, Object> entityMap = new HashMap<>();

    Movie movie = Movie.builder()
        .mno(movieDTO.getMno()).title(movieDTO.getTitle())
        .build();
    entityMap.put("movie", movie);  // movie 생성

    //List<MovieImageDTO>를 List<MovieImage> 변환해서 imgList를 생성
    List<MovieImageDTO> imageDTOList = movieDTO.getImageDTOList();
    // imageDTOList에 이미지가 있다면
    if (imageDTOList != null && imageDTOList.size() > 0) {
      List<MovieImage> movieImageList = imageDTOList.stream().map(
          new Function<MovieImageDTO, MovieImage>() {
            @Override
            public MovieImage apply(MovieImageDTO movieImageDTO) {
              return MovieImage.builder()
                  .path(movieImageDTO.getPath())
                  .imgName(movieImageDTO.getImgName())
                  .uuid(movieImageDTO.getUuid())
                  .movie(movie)
                  .build();
            }
          }
      ).collect(Collectors.toList());
      entityMap.put("imgList", movieImageList);
    }
    return entityMap;
  }

  default MovieDTO entitiesToDTO(Movie movie, List<MovieImage> movieImages, Double avg, Long reviewCnt) {
    MovieDTO movieDTO = MovieDTO.builder()
        .mno(movie.getMno())
        .title(movie.getTitle())
        .regDate(movie.getRegDate())
        .modDate(movie.getModDate())
        .build();

    // movieImages가 null이거나 비어있는 경우를 안전하게 처리
    List<MovieImageDTO> movieImageDTOList =
        // movieImages가 비어 있으면 new ArrayList<>()를 하고
        (movieImages == null || movieImages.isEmpty()) ? new ArrayList<>() :
        // movieImages가 비어 있지 않으면
        movieImages.stream()
            .filter(Objects::nonNull) // movieImage가 널이 아닌 경우만 걸러서 map으로 넘김
            .map(mi -> MovieImageDTO.builder()
                                    .path(mi.getPath())
                                    .imgName(mi.getImgName())
                                    .uuid(mi.getUuid())
                                    .build()
            ).collect(Collectors.toList());
    movieDTO.setImageDTOList(movieImageDTOList);
    movieDTO.setAvg(avg);
    movieDTO.setReviewCnt(reviewCnt.intValue());
    return movieDTO;
  }

  Long register(MovieDTO movieDTO);

  PageResultDTO<MovieDTO, Object[]> getList(PageRequestDTO pageRequestDTO);

  MovieDTO get(Long mno);

  void modify(MovieDTO movieDTO);

  void removeWithReviewsAndMovieImages(Long mno);

  void removeMovieImagebyUUID(String uuid);
}
