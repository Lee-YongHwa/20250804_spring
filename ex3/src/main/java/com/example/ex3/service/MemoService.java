package com.example.ex3.service;

import com.example.ex3.dto.MemoDTO;
import com.example.ex3.entity.Memo;

import java.util.List;

public interface MemoService {
  List<MemoDTO> getMemoList();
  List<MemoDTO> getMemoPage(int page);

  default Memo dtoToEntity(MemoDTO memoDTO) {
    return Memo.builder()
        .mno(memoDTO.getMno())
        .memoText(memoDTO.getMemoText())
        .build();
  }
  default MemoDTO entityToDto(Memo memo) {
    return MemoDTO.builder()
        .mno(memo.getMno())
        .memoText(memo.getMemoText())
        .build();
  }

  void registMemo(MemoDTO memoDTO);
}
