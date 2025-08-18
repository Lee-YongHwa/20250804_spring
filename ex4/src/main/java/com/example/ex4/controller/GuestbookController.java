package com.example.ex4.controller;

import com.example.ex4.dto.GuestbookDTO;
import com.example.ex4.dto.PageRequestDTO;
import com.example.ex4.service.GuestbookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/guestbook")
@Log4j2
@RequiredArgsConstructor
public class GuestbookController {
  // has-a관계  :: interface
  private final GuestbookService guestbookService;

  // 페이지의 목록 요청, PageRequestDTO:요청된 페이지의 정보를 가진 객체
  @GetMapping({"","/","list"})
  public String list(PageRequestDTO pageRequestDTO, Model model) {
    log.info("guestbook/list..." + pageRequestDTO);
    model.addAttribute("pageResultDTO", guestbookService.getList(pageRequestDTO));
    return "/guestbook/list";
  }

  // 등록 페이지로 이동
  @GetMapping("/register")
  public void register(){}

  // 실제 등록할 때
  @PostMapping("/register")
  public String registerPost(GuestbookDTO guestbookDTO, RedirectAttributes ra){
    Long result = guestbookService.register(guestbookDTO);
    log.info(">>"+result+" 번 글이 등록되었습니다.");
    ra.addFlashAttribute("msg", result + " 번 글이 등록되었습니다."); // RedirectAttributes 일회성, 최종 view단까지 보냄
    return "redirect:/guestbook/list";
  }

  // 상세보기페이지와 수정페이지 단순 이동
  @GetMapping({"/read","/modify"})
  public void read(Long gno, PageRequestDTO pageRequestDTO, Model model){
    log.info(">>gno:"+gno);
    log.info(">>pageRequestDTO:"+pageRequestDTO);
    GuestbookDTO guestbookDTO = guestbookService.read(gno, pageRequestDTO);
    typeKeywordInit(pageRequestDTO);
    model.addAttribute("guestbookDTO", guestbookDTO);
  }

  // 실제 수정할 때
  @PostMapping("/modify")
  public String modify(GuestbookDTO guestbookDTO,
                       PageRequestDTO pageRequestDTO, RedirectAttributes ra) {
    Long gno = guestbookService.modify(guestbookDTO);
    typeKeywordInit(pageRequestDTO);
    ra.addFlashAttribute("msg", gno);
    ra.addAttribute("gno", gno);
    ra.addAttribute("page", pageRequestDTO.getPage());
    ra.addAttribute("type", pageRequestDTO.getType());
    ra.addAttribute("keyword", pageRequestDTO.getKeyword());
    return "redirect:/guestbook/read";  // 수정후 상세보기페이지로 이동
  }

  // 실제 삭제할 때(삭제 페이지 이동은 없음)
  @PostMapping("/remove")
  public String remove(GuestbookDTO guestbookDTO,
                       PageRequestDTO pageRequestDTO, RedirectAttributes ra) {
    // 실제 삭제처리
    Long gno = guestbookService.remove(guestbookDTO);

    // 지우는 페이지에 목록 개수가 하나일 때 다음페이지로 보냄
    // 목록 가져와서 좋은 코드 아님. 페이지 목록 개수는 pageRequestDTO에 별도 저장 필요
    if (guestbookService.getList(pageRequestDTO).getDtoList().size() == 0
        && pageRequestDTO.getPage() != 1) {
      pageRequestDTO.setPage(pageRequestDTO.getPage() - 1);
    }

    typeKeywordInit(pageRequestDTO);  //null 문자열을 삭제처리
    ra.addFlashAttribute("msg", gno+" 번 글이 삭제되었습니다."); //일회성
    ra.addAttribute("page", pageRequestDTO.getPage());
    ra.addAttribute("type", pageRequestDTO.getType());
    ra.addAttribute("keyword", pageRequestDTO.getKeyword());
    return "redirect:/guestbook/list";
  }

  // type, keyword에 null 값이 문자열로 올 때 그것을 지울때
  private void typeKeywordInit(PageRequestDTO pageRequestDTO){
    if (pageRequestDTO.getType().equals("null")) pageRequestDTO.setType("");
    if (pageRequestDTO.getKeyword().equals("null")) pageRequestDTO.setKeyword("");
  }
}
