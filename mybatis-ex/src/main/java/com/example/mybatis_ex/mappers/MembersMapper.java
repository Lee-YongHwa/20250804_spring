package com.example.mybatis_ex.mappers;

import com.example.mybatis_ex.dto.MembersDTO;
import com.example.mybatis_ex.entity.Members;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;

@Mapper
@MapperScan(basePackages = {"com.example.mybatis_ex.mappers"})
public interface MembersMapper {
  public Members getMember(String email);

  Long insertMembers(Members members);

  Long updateMembers(Members members);

  Long deleteMembers(Long mno);

  List<Members> getAllMembers();

}
