package com.example.mybatis_ex.repository;

import com.example.mybatis_ex.entity.Members;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembersRepository extends JpaRepository<Members, Long> {
}
