package com.example.ex8.service;

import com.example.ex8.dto.NoteDTO;
import com.example.ex8.entity.Note;
import com.example.ex8.repository.NoteRespository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.weaver.ast.Not;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class NoteServiceImpl implements NoteService {
  private final NoteRespository noteRespository;

  @Override
  public Long register(NoteDTO noteDTO) {
    return noteRespository.save(dtoToEntity(noteDTO)).getNum();
  }

  @Override
  public NoteDTO get(Long num) {
    Optional<Note> result = noteRespository.getWithWriter(num);
    return result.isPresent()? entityToDTO(result.get()):null;
  }

  @Override
  public void modify(NoteDTO noteDTO) {
    // 변경하고자 하는 entity를 먼저 찾은 후 변경
    Optional<Note> result = noteRespository.findById(noteDTO.getNum());
    if (result.isPresent()) {
      Note note = result.get();
      note.changeTitle(noteDTO.getTitle());
      note.changeContent(noteDTO.getContent());
      noteRespository.save(note);
    }
  }

  @Override
  public void remove(NoteDTO noteDTO) {
    noteRespository.deleteById(noteDTO.getNum());
  }

  @Override
  public List<NoteDTO> getAllWithWriter(String writerEmail) {
    List<Note> list = noteRespository.getList(writerEmail);
    // List<Note> -> List<NoteDTO>
    return list.stream().map(note -> entityToDTO(note)).collect(Collectors.toList());
  }
}
