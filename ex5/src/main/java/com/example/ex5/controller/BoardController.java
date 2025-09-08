package com.example.ex5.controller;

import com.example.ex5.dto.BoardDTO;
import com.example.ex5.dto.PageRequestDTO;
import com.example.ex5.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/board")
@Log4j2
@RequiredArgsConstructor
public class BoardController {
  private final BoardService boardService;

  @GetMapping({"","/","list"})
  public String list(PageRequestDTO pageRequestDTO, Model model) {
    log.info("board/list..." + pageRequestDTO);
    model.addAttribute("pageResultDTO", boardService.getList(pageRequestDTO));
    return "/board/list";
  }
  // 등록 페이지로 이동
  @GetMapping("/register")
  public void register(){}

  // 실제 등록할 때
  @PostMapping("/register")
  public String registerPost(BoardDTO boardDTO, RedirectAttributes ra) {
    Long result = boardService.register(boardDTO);
    log.info(">>"+result+" 번 글이 등록되었습니다.");
    ra.addFlashAttribute("msg", result + " 번 글이 등록되었습니다.");
    return "redirect:/board/list";
  }

  // 상세보기페이지와 수정페이지 단순 이동
  @GetMapping({"/read","/modify"})
  public void read(Long bno, PageRequestDTO pageRequestDTO, Model model){
    BoardDTO boardDTO = boardService.read(bno, pageRequestDTO);
    typeKeywordInit(pageRequestDTO);
    model.addAttribute("boardDTO", boardDTO);
  }

  // 실제 수정할 때
  @PostMapping("/modify")
  public String modify(BoardDTO boardDTO,
                       PageRequestDTO pageRequestDTO, RedirectAttributes ra) {
    Long bno = boardService.modify(boardDTO, pageRequestDTO);
    typeKeywordInit(pageRequestDTO);
    ra.addFlashAttribute("msg", bno);
    ra.addAttribute("bno", bno);
    ra.addAttribute("page", pageRequestDTO.getPage());
    ra.addAttribute("type", pageRequestDTO.getType());
    ra.addAttribute("keyword", pageRequestDTO.getKeyword());
    return "redirect:/board/read";  // 수정후 상세보기페이지로 이동
  }

  // 실제 삭제할 때(삭제 페이지 이동은 없음)
  @PostMapping("/remove")
  public String remove(BoardDTO boardDTO,
                       PageRequestDTO pageRequestDTO, RedirectAttributes ra) {
    boardService.removeWithReplies(boardDTO.getBno());
    if (boardService.getList(pageRequestDTO).getDtoList().size() == 0
        && pageRequestDTO.getPage() != 1) {
      pageRequestDTO.setPage(pageRequestDTO.getPage() - 1);
    }
    typeKeywordInit(pageRequestDTO);  //null 문자열을 삭제처리
    ra.addFlashAttribute("msg", boardDTO.getBno()+" 번 글이 삭제되었습니다.");
    ra.addAttribute("page", pageRequestDTO.getPage());
    ra.addAttribute("type", pageRequestDTO.getType());
    ra.addAttribute("keyword", pageRequestDTO.getKeyword());
    return "redirect:/board/list";
  }

  private void typeKeywordInit(PageRequestDTO pageRequestDTO){
    if (pageRequestDTO.getType().equals("null")) pageRequestDTO.setType("");
    if (pageRequestDTO.getKeyword().equals("null")) pageRequestDTO.setKeyword("");
  }
}
