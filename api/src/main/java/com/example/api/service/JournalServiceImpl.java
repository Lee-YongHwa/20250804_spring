package com.example.api.service;

import com.example.api.dto.JournalDTO;
import com.example.api.dto.MembersDTO;
import com.example.api.dto.PageRequestDTO;
import com.example.api.dto.PageResultDTO;
import com.example.api.entity.Journal;
import com.example.api.entity.Members;
import com.example.api.entity.Photos;
import com.example.api.repository.CommentsRepository;
import com.example.api.repository.JournalRepository;
import com.example.api.repository.PhotosRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URLDecoder;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Log4j2
public class JournalServiceImpl implements JournalService {
  private final JournalRepository journalRepository;
  private final PhotosRepository photosRepository;
  private final CommentsRepository commentsRepository;

  @Value("${com.example.upload.path}")
  private String uploadPath;

  @Override
  public PageResultDTO<JournalDTO, Object[]> getList(PageRequestDTO pageRequestDTO) {
    Pageable pageable = pageRequestDTO.getPageable(Sort.by("jno").descending());
    Page<Object[]> result = journalRepository.searchPage(pageRequestDTO.getType(),
        pageRequestDTO.getKeyword(), pageable);
    Function<Object[], JournalDTO> fn = new Function<Object[], JournalDTO>() {
      @Override
      public JournalDTO apply(Object[] objects) {
        log.info(">>>>"+Arrays.toString(objects));
        return entityToDTO(
            (Journal) objects[0],
            (List<Photos>) (Arrays.asList((Photos)objects[1])),
            (Members) objects[2],
            (Long) objects[3],
            (Long) objects[4]
        );
      }
    };
    return new PageResultDTO<>(result, fn);
  }

  @Override
  public Long register(JournalDTO journalDTO) {
    Map<String, Object> entityMap = dtoToEntity(journalDTO);
    Journal journal = (Journal) entityMap.get("journal");
    journalRepository.save(journal);
    List<Photos> photosList = (List<Photos>) entityMap.get("photosList");
    photosList.forEach(photos -> photosRepository.save(photos));
    return journal.getJno();
  }

  @Override
  public JournalDTO get(Long jno) {
    List<Object[]> result = journalRepository.getJournalWithAll(jno);
    // j, p, m, sum(coalesce(c.likes, 0)), count(distinct c)
    Journal journal = (Journal) result.get(0)[0];
    List<Photos> photosList = new ArrayList<>();
    result.forEach(objects -> {
      photosList.add((Photos) objects[1]);
    });
    Members members = (Members) result.get(0)[2];
    Long likes = (Long) result.get(0)[3];
    Long commentCnt = (Long) result.get(0)[4];
    return entityToDTO(journal, photosList, members, likes, commentCnt);
  }

  @Transactional
  @Override
  public void modify(JournalDTO journalDTO) {
    Optional<Journal> result = journalRepository.findById(journalDTO.getJno());
    if (result.isPresent()) {
      journalDTO.setMembersDTO(MembersDTO.builder().mid(result.get().getMembers().getMid()).build());
      Map<String, Object> entityMap = dtoToEntity(journalDTO);
      Journal journal = (Journal) entityMap.get("journal");
      journal.changeTitle(journalDTO.getTitle());
      journal.changeContent(journalDTO.getContent());
      journalRepository.save(journal);

      List<Photos> newPhotosList = (List<Photos>) entityMap.get("photosList");//from client(new)
      List<Photos> oldPhotosList = photosRepository.findByJno(journal.getJno());//from db(old)

      if (newPhotosList == null || newPhotosList.size() == 0) {
        // 수정창에서 이미지 모두를 지웠을 때
        photosRepository.deleteByJno(journal.getJno());
        for (int i = 0; i < oldPhotosList.size(); i++) {
          Photos oldPhotos = oldPhotosList.get(i);
          String fileName = oldPhotos.getPath() + File.separator
              + oldPhotos.getUuid() + "_" + oldPhotos.getPhotosName();
          deleteFile(fileName);
        }
      } else { // newPhotosList에 일부 변화 발생
        newPhotosList.forEach(photos -> {
          boolean result1 = false;
          for (int i = 0; i < oldPhotosList.size(); i++) {
            result1 = oldPhotosList.get(i).getUuid().equals(photos.getUuid());
            if (result1) break;
          }
          if (!result1) photosRepository.save(photos);
        });
        oldPhotosList.forEach(oldPhotos -> {
          boolean result1 = false;
          for (int i = 0; i < newPhotosList.size(); i++) {
            result1 = newPhotosList.get(i).getUuid().equals(oldPhotos.getUuid());
            if (result1) break;
          }
          if (!result1) {
            photosRepository.deleteByUuid(oldPhotos.getUuid());
            String fileName = oldPhotos.getPath() + File.separator
                + oldPhotos.getUuid() + "_" + oldPhotos.getPhotosName();
            deleteFile(fileName);
          }
        });
      }
    }
  }

  private void deleteFile(String fileName) {
    // 실제 파일도 지우기
    String searchFilename = null;
    try {
      searchFilename = URLDecoder.decode(fileName, "UTF-8");
      File file = new File(uploadPath + File.separator + searchFilename);
      file.delete();
      new File(file.getParent(), "s_" + file.getName()).delete();
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  @Transactional
  @Override
  public List<String> removeWithCommentsAndPhotos(Long jno) {
    List<Photos> list = photosRepository.findByJno(jno);
    List<String> result = new ArrayList<>();
    list.forEach(new Consumer<Photos>() {
      @Override
      public void accept(Photos p) {
        result.add(p.getPath() + File.separator + p.getUuid() + "_" + p.getPhotosName());
      }
    });
    photosRepository.deleteByJno(jno);
    commentsRepository.deleteByJno(jno);
    journalRepository.deleteById(jno);
    return result;
  }

  @Override
  public void removePhotosByUUID(String uuid) {
    photosRepository.deleteByUuid(uuid);
  }
}
