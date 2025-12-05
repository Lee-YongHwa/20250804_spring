package com.example.mybatis_ex.service;

import com.example.mybatis_ex.dto.MembersDTO;
import com.example.mybatis_ex.entity.Members;
import com.example.mybatis_ex.mappers.MembersMapper;
import com.example.mybatis_ex.repository.MembersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class MembersServiceImpl implements MembersService {
  private final MembersRepository membersRepository;
  private final MembersMapper membersMapper;

  @Override
  public MembersDTO getMembers(String email) {
    Members members = membersMapper.getMember(email);
    return entityToDto(members);
  }

  @Override
  public Long registerMembers(MembersDTO membersDTO) {
    //return membersRepository.save(dtoToEntity(membersDTO)).getMno();
    return membersMapper.insertMembers(dtoToEntity(membersDTO)).longValue();
  }

  @Override
  public List<MembersDTO> getMembersAll() {
    List<Members> list = membersMapper.getAllMembers();
    list.forEach(dto -> System.out.println(">>"+dto));
    List<MembersDTO> result = list.stream().map(
        members -> entityToDto(members)).collect(Collectors.toList());
    result.forEach(dto -> System.out.println(dto));
    return result;
  }

  @Override
  public Long updateMembers(MembersDTO membersDTO) {
    return membersMapper.updateMembers(dtoToEntity(membersDTO));
  }

  @Override
  public Long deleteMembers(Long mno) {
    return membersMapper.deleteMembers(mno);
  }
}
