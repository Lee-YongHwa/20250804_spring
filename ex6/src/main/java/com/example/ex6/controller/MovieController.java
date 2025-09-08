package com.example.ex6.controller;

import com.example.ex6.dto.MovieDTO;
import com.example.ex6.dto.PageRequestDTO;
import com.example.ex6.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/movie")
@Log4j2
@RequiredArgsConstructor
public class MovieController {
  private final MovieService movieService;

  @GetMapping("register")
  public void register(){}

  @PostMapping("/register")
  public String register(MovieDTO movieDTO, RedirectAttributes ra) {
    Long mno = movieService.register(movieDTO);
    ra.addFlashAttribute("msg", mno);
    return "redirect:/movie/list";
  }

  @GetMapping({"", "/", "/list"})
  public String list(PageRequestDTO pageRequestDTO, Model model) {
    model.addAttribute("pageResultDTO", movieService.getList(pageRequestDTO));
    typeKeywordInit(pageRequestDTO);
    return "/movie/list";
  }

  @GetMapping({"/read", "/modify"})
  public void get(Long mno, PageRequestDTO pageRequestDTO, Model model) {
    typeKeywordInit(pageRequestDTO);
    model.addAttribute("movieDTO", movieService.get(mno));
  }

  @PostMapping("/modify")
  public String modify(MovieDTO movieDTO, RedirectAttributes ra, PageRequestDTO pageRequestDTO) {
    log.info("modify.... movieDTO:" + movieDTO); //movieDTO에는 mno, title, imageDTOList 가 넘어옴
    movieService.modify(movieDTO); // service 이동
    typeKeywordInit(pageRequestDTO);
    ra.addFlashAttribute("msg", movieDTO.getMno() + " 수정");
    ra.addAttribute("mno", movieDTO.getMno());
    ra.addAttribute("page", pageRequestDTO.getPage());
    ra.addAttribute("type", pageRequestDTO.getType());
    ra.addAttribute("keyword", pageRequestDTO.getKeyword());
    return "redirect:/movie/read";
  }

  @PostMapping("/remove")
  public String remove(Long mno, RedirectAttributes ra, PageRequestDTO pageRequestDTO){
    log.info("remove post... mno: " + mno);
    movieService.removeWithReviewsAndMovieImages(mno);

    if(movieService.getList(pageRequestDTO).getDtoList().size() == 0 && pageRequestDTO.getPage() != 1) {
      pageRequestDTO.setPage(pageRequestDTO.getPage()-1);
    }
    typeKeywordInit(pageRequestDTO);
    ra.addFlashAttribute("msg", mno + " 삭제");
    ra.addAttribute("page", pageRequestDTO.getPage());
    ra.addAttribute("type", pageRequestDTO.getType());
    ra.addAttribute("keyword", pageRequestDTO.getKeyword());
    return "redirect:/movie/list";
  }
  private void typeKeywordInit(PageRequestDTO pageRequestDTO){
    if (pageRequestDTO.getType().equals("null")) pageRequestDTO.setType("");
    if (pageRequestDTO.getKeyword().equals("null")) pageRequestDTO.setKeyword("");
  }
}
