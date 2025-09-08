package com.example.ex8.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Log4j2
public class ApiCheckFilter extends OncePerRequestFilter {
  @Override
  protected void doFilterInternal (HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    log.info("ApiCheckFilter...........");
    log.info("ApiCheckFilter...........");
    log.info("ApiCheckFilter...........");
    filterChain.doFilter(request, response); // AOP?
  }
}
