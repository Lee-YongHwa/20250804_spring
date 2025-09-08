package com.example.ex8.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Log4j2
public class ApiCheckFilter extends OncePerRequestFilter {
  private String pattern;
  private AntPathMatcher antPathMatcher;

  public ApiCheckFilter(String pattern) {
    this.pattern = pattern; antPathMatcher = new AntPathMatcher();
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
    log.info("Request URI: " + request.getRequestURI());
    log.info("ApiCheckFilter.......................");
    if (antPathMatcher.match(request.getContextPath()+pattern, request.getRequestURI())) {
      log.info("matched......................");

    }
    filterChain.doFilter(request, response);
  }
}
