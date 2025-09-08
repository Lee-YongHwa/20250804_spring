package com.example.ex6.controller;

import com.example.ex6.dto.UploadResultDTO;
import com.example.ex6.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@Log4j2
@RequiredArgsConstructor
public class UploadController {
  private final MovieService movieService;

  @RequestMapping("/uploadAjax")
  public ResponseEntity<List<UploadResultDTO>> uploadFile(MultipartFile[] uploadFiles) {
    // 전송결과를 담기 위한 객체
    List<UploadResultDTO> resultDTOList = new ArrayList<>();
    for (MultipartFile uploadFile : uploadFiles) {
      //이미지 파일만 업로드 처리
      if (!uploadFile.getContentType().startsWith("image")) {
        log.warn("This file is not image type.");
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
      }
      String originalName = uploadFile.getOriginalFilename(); //경로포함파일명
      String fileName = originalName.substring(originalName.lastIndexOf("\\") + 1);
      log.info("fileName: " + fileName); //실제파일명만 출력

      // 저장될 경로 생성
      String folderPath = makeFolder();

      // UUID
      String uuid = UUID.randomUUID().toString();

      // 서버에 실제 저장될 경로와 파일명 전체를 담은 변수 선언
      String saveName = uploadPath + File.separator + folderPath
          + File.separator + uuid + "_" + fileName;
      Path savePath = Paths.get(saveName);

      try {
        uploadFile.transferTo(savePath);
        String thumbnailSaveName = uploadPath + File.separator + folderPath
            + File.separator + "s_" + uuid + "_" + fileName;
        File thumbnailFile = new File(thumbnailSaveName);
        Thumbnailator.createThumbnail(savePath.toFile(), thumbnailFile, 100, 100);
        resultDTOList.add(new UploadResultDTO(fileName, uuid, folderPath));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return new ResponseEntity<>(resultDTOList, HttpStatus.OK);
  }

  @GetMapping("/display")
  public ResponseEntity<byte[]> getFile(String fileName, String size) {
    ResponseEntity<byte[]> result = null;
    try {
      String srcFileName = URLDecoder.decode(fileName, "UTF-8");
      File file = new File(uploadPath + File.separator + srcFileName);
      if (size != null && size.equals("1")) {
        log.info(">>", file.getName());
        // 미리보기 할 때 링크에 size=1로 설정하여 섬네일명에서 s_ 를 제거하고 가져옴
        file = new File(file.getParent(), file.getName().substring(2));
      }
      HttpHeaders headers = new HttpHeaders(); //브라우저에 전송할때 Header 필요
      headers.add("Content-Type", Files.probeContentType(file.toPath())); // 파일 타입 설정
      result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), headers, HttpStatus.OK);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return result;
  }

  @PostMapping("/removeFile")
  public ResponseEntity<Boolean> removeFile(String fileName, String uuid) {
    log.info(">>>"+fileName);
    String srcFileName = null;

    if (uuid != null) movieService.removeMovieImagebyUUID(uuid);

    try {
      srcFileName = URLDecoder.decode(fileName, "UTF-8"); //정확한 소스파일명
      File file = new File(uploadPath+File.separator+srcFileName);//해당되는 파일선택
      boolean result = file.delete(); // 첫번째 원본 파일 지움.
      File thumbnail = new File(file.getParent(), "s_" + file.getName());
      result = thumbnail.delete() && result; // 두번째 썸내일 파일 지움
      return new ResponseEntity<>(result,
          result ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  @Value("${com.example.upload.path}")
  private String uploadPath;

  private String makeFolder() {
    String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    String folderPath = str.replace("/", File.separator);//각 운영체제의 파일구분자로 대체
    File uploadPathFolder = new File(uploadPath, folderPath);
    if (!uploadPathFolder.exists()) uploadPathFolder.mkdirs();
    return folderPath;
  }
}
