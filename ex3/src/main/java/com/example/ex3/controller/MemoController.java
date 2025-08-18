package com.example.ex3.controller;

import com.example.ex3.dto.MemoDTO;
import com.example.ex3.service.MemoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/memo")
@RequiredArgsConstructor
@Log4j2
public class MemoController {

  private final MemoService memoService;

  // 1) 요청된 url이 resource와 같을 때
  @RequestMapping("/regMemo")
  public void regMemo() {  }

  // 2) 요청된 url과 resource가 다를 때
//  @RequestMapping("register")
//  public String register() {
//    // resource 주소를 표현할 때 context path 제외
//    return "/memo/regMemo";
//  }

  // 3) GetMapping
  @GetMapping("register")
  public String getRegister() {
    // resource 주소를 표현할 때 context path 제외
    return "/memo/regMemo";
  }

  @PostMapping("register")
  public String postRegister(MemoDTO memoDTO) {
    // 등록 처리 부분
    log.info(memoDTO);
    System.out.println(">>"+memoDTO);
    memoService.registMemo(memoDTO);

    // redirect :: controller에 있는 또 다른 주소로 이동.
    return "redirect:/memo/list";
  }

  // Model :: view단에 key와 value형태로 데이터를 보내기 위한 객체
  @GetMapping("list")
  public String listMemo(Model model) {
    // result :: List<MemoDTO>, Memo 목록 불러오는 부분
    model.addAttribute("result", memoService.getMemoList());
    return "/memo/listMemo";
  }

  @GetMapping("page")
  public String pageMemo(Model model, int page) {
    // result :: Page<MemoDTO>를 List<MemoDTO>로 변환한 목록 불러오는 부분
    model.addAttribute("result", memoService.getMemoPage(page));
    return "/memo/listMemo";
  }
}
