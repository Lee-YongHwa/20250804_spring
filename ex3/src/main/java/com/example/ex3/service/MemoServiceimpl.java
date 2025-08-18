package com.example.ex3.service;

import com.example.ex3.dto.MemoDTO;
import com.example.ex3.entity.Memo;
import com.example.ex3.repository.MemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class MemoServiceImpl implements MemoService {
  private final MemoRepository memoRepository;

  @Override
  public List<MemoDTO> getMemoList() {
    List<Memo> list = memoRepository.getListDesc();
    return list.stream().map(memo -> entityToDto(memo)).toList();
  }

  @Override
  public List<MemoDTO> getMemoPage(int page) {
    Pageable pageable = PageRequest.of(page-1, 10, Sort.by("mno").descending());
    Page<Memo> result = memoRepository.findAll(pageable);
    return result.stream().map(memo -> entityToDto(memo)).toList();
  }

  @Override
  public void registMemo(MemoDTO memoDTO) {
    memoRepository.save(dtoToEntity(memoDTO));
  }
}
