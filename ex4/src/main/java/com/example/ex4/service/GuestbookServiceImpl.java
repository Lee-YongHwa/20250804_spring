package com.example.ex4.service;

import com.example.ex4.dto.GuestbookDTO;
import com.example.ex4.dto.PageRequestDTO;
import com.example.ex4.dto.PageResultDTO;
import com.example.ex4.entity.Guestbook;
import com.example.ex4.entity.QGuestbook;
import com.example.ex4.repository.GuestbookRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class GuestbookServiceImpl implements GuestbookService {

  private final GuestbookRepository guestbookRepository;

  @Override
  public Long register(GuestbookDTO guestbookDTO) {
    return guestbookRepository.save(dtoToEntity(guestbookDTO)).getGno();
  }

  @Override
  public PageResultDTO<GuestbookDTO, Guestbook> getList(PageRequestDTO pageRequestDTO) {
    Pageable pageable = pageRequestDTO.getPageable(Sort.by("gno").descending());

    // 검색 조건 처리
    BooleanBuilder booleanBuilder = getSearch(pageRequestDTO);

    // queryDSL 적용 :: findAll(검색조건, 페이지정보)->페이지목록
    Page<Guestbook> page = guestbookRepository.findAll(booleanBuilder, pageable);

    // repository에서 정보를 가져오면 entity라서 이걸 dto로 변환하는 함수
    Function<Guestbook, GuestbookDTO> fn = guestbook -> entityToDto(guestbook);

    // PageResultDTO(검색된 페이지 목록, 변환하기 위한 Function)
    return new PageResultDTO<>(page, fn);
  }

  private BooleanBuilder getSearch(PageRequestDTO pageRequestDTO) {
    String type = pageRequestDTO.getType();
    String keyword = pageRequestDTO.getKeyword();
    QGuestbook qGuestbook = QGuestbook.guestbook;
    BooleanBuilder booleanBuilder = new BooleanBuilder();
    BooleanExpression booleanExpression = qGuestbook.gno.gt(0l);
    booleanBuilder.and(booleanExpression); //첫번째 조건 적용
    BooleanBuilder conditionBuilder = new BooleanBuilder();

    // 검색 조건이 없는 경우(검색조건이 전체보기일 경우, 첫페이지 포함)
    if(type==null || type.trim().length() ==0) return booleanBuilder;
    if(keyword==null || keyword.trim().length() ==0) return booleanBuilder;

    // 검색 조건이 있는 경우
    if(type.contains("t")) conditionBuilder.or(qGuestbook.title.contains(keyword));
    if(type.contains("c")) conditionBuilder.or(qGuestbook.content.contains(keyword));
    if(type.contains("w")) conditionBuilder.or(qGuestbook.writer.contains(keyword));
    booleanBuilder.and(conditionBuilder);
    return booleanBuilder;
  }

  @Override
  public GuestbookDTO read(Long gno, PageRequestDTO pageRequestDTO) {
    Optional<Guestbook> result = guestbookRepository.findById((Long)gno);
    if (result.isPresent()) {
      return entityToDto(result.get());
    }
    return null;
  }
  @Override
  public Long modify(GuestbookDTO guestbookDTO) {
    Long result = null;
    Optional<Guestbook> find = guestbookRepository.findById(guestbookDTO.getGno());
    if (find.isPresent()) {
      Guestbook guestbook = find.get();  //찾은것의 일부만 변경
      guestbook.changeTitle(guestbookDTO.getTitle());
      guestbook.changeContent(guestbookDTO.getContent());
      guestbookRepository.save(guestbook);
      result = guestbook.getGno();
    }
    return result;
  }

  @Override
  public Long remove(GuestbookDTO guestbookDTO) {
    guestbookRepository.deleteById(guestbookDTO.getGno());
    return guestbookDTO.getGno();
  }
}
