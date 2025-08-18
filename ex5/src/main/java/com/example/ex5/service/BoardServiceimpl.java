package com.example.ex5.service;

import com.example.ex5.dto.BoardDTO;
import com.example.ex5.dto.PageRequestDTO;
import com.example.ex5.dto.PageResultDTO;
import com.example.ex5.entity.Board;
import com.example.ex5.entity.Member;
import com.example.ex5.repository.BoardRepository;
import com.example.ex5.repository.ReplyRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@Service
@Log4j2
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
  private final BoardRepository boardRepository;
  private final ReplyRepository replyRepository;

  @Override
  public Long register(BoardDTO boardDTO) {
    return boardRepository.save(dtoToEntity(boardDTO)).getBno();
  }

  @Override
  // PageResultDTO<DTO,EN> :: EN으로 입력되면 DTO로 받는 구조
  public PageResultDTO<BoardDTO, Object[]> getList(PageRequestDTO pageRequestDTO) {
    // 동적 검색 없는 내용
    Page<Object[]> page = boardRepository.getBoardWithReplyCount(
        pageRequestDTO.getPageable(Sort.by("bno").descending())
    );
    // 동적 검색 추가된 내용
    Page<Object[]> page1 = boardRepository.searchPage(
        pageRequestDTO.getType(),
        pageRequestDTO.getKeyword(),
        pageRequestDTO.getPageable(Sort.by("bno").descending())
    );

    // Function<EN, DTO>
    Function<Object[], BoardDTO> fn =
        entity -> entityToDto((Board) entity[0],(Member) entity[1],(Long) entity[2]);
    return new PageResultDTO<>(page1, fn);
  }

  @Override
  public BoardDTO read(Long bno, PageRequestDTO pageRequestDTO) {
    Object result = boardRepository.getBoardByBno(bno);
    Object[] arr = (Object[]) result;
    return entityToDto((Board) arr[0], (Member) arr[1], (Long) arr[2]);
  }

  @Transactional
  @Override
  public void removeWithReplies(Long bno) {
//    replyRepository.deleteByBoard_Bno(bno);
    replyRepository.deleteByBno(bno);
    boardRepository.deleteById(bno);
  }

  @Override
  public Long modify(BoardDTO boardDTO, PageRequestDTO pageRequestDTO) {
    Long bno = null;
    Optional<Board> result = boardRepository.findById(boardDTO.getBno());
    if (result.isPresent()) {
      log.info(">>>>>>>>"+result.get());
      Board board = result.get();
      board.changeTitle(boardDTO.getTitle());
      board.changeContent(boardDTO.getContent());
      boardRepository.save(board);
      bno = board.getBno();
    }
    return bno;
  }

  @Override
  public Long remove(BoardDTO boardDTO) {
    boardRepository.deleteById(boardDTO.getBno());
    return boardDTO.getBno();
  }
}
