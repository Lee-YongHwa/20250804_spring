package com.example.mybatis_ex.service;

import com.example.mybatis_ex.dto.MembersDTO;
import com.example.mybatis_ex.entity.Members;

import java.util.List;

public interface MembersService {
  MembersDTO getMembers(String email);

  Long registerMembers(MembersDTO membersDTO);

  List<MembersDTO> getMembersAll();

  Long updateMembers(MembersDTO membersDTO);

  Long deleteMembers(Long mno);

  default public Members dtoToEntity(MembersDTO membersDTO) {
    Members members = Members.builder()
        .mno(membersDTO.getMno())
        .email(membersDTO.getEmail())
        .pass(membersDTO.getPass())
        .name(membersDTO.getName())
        .build();
    return members;
  }

  default public MembersDTO entityToDto(Members members) {
    MembersDTO membersDTO = MembersDTO.builder()
        .mno(members.getMno())
        .email(members.getEmail())
        .pass(members.getPass())
        .name(members.getPass())
        .regDate(members.getRegDate())
        .modDate(members.getModDate())
        .build();
    return membersDTO;
  }
}
