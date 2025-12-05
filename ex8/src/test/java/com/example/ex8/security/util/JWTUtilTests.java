package com.example.ex8.security.util;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JWTUtilTests {

  private JWTUtil jwtUtil;

  @BeforeEach
  public void testBefore() {
    jwtUtil = new JWTUtil();
  }

  @Test
  public void testEncode() throws Exception {
    String email = "user95@a.a";
    String str = jwtUtil.generateToken(email);
    System.out.println(">>>" + str);
  }

  @Test
  public void testValidate() throws Exception {
    String str = jwtUtil.generateToken("user95@a.a");
    Thread.sleep(5000);
    Exception exception = assertThrows(ExpiredJwtException.class, ()->{
      jwtUtil.validateAndExtract(str);
    });

    System.out.println(exception.getMessage());
  }
}