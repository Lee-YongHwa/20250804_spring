package com.example.ex4.service;

import com.example.ex4.dto.GuestbookDTO;
import com.example.ex4.dto.PageRequestDTO;
import com.example.ex4.dto.PageResultDTO;
import com.example.ex4.entity.Guestbook;

public interface GuestbookService {
  default Guestbook dtoToEntity(GuestbookDTO guestbookDTO) {
    return Guestbook.builder()
        .gno(guestbookDTO.getGno())
        .title(guestbookDTO.getTitle())
        .content(guestbookDTO.getContent())
        .writer(guestbookDTO.getWriter())
        .build();
  }
  default GuestbookDTO entityToDto(Guestbook guestbook) {
    return GuestbookDTO.builder()
        .gno(guestbook.getGno())
        .title(guestbook.getTitle())
        .content(guestbook.getContent())
        .writer(guestbook.getWriter())
        .regDate(guestbook.getRegDate())
        .modDate(guestbook.getModDate())
        .build();
  }

  Long register(GuestbookDTO guestbookDTO);
  PageResultDTO<GuestbookDTO, Guestbook> getList(PageRequestDTO pageRequestDTO);
  GuestbookDTO read(Long gno, PageRequestDTO pageRequestDTO);
  Long modify(GuestbookDTO guestbookDTO);
  Long remove(GuestbookDTO guestbookDTO);
}
