package com.example.ex7.config;

import com.example.ex7.security.handler.CustomAccessDeniedHandler;
import com.example.ex7.security.handler.CustomAuthenticationFailureHandler;
import com.example.ex7.security.handler.CustomLoginSuccessHandler;
import com.example.ex7.security.handler.CustomLogoutSuccessHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2ClientConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity // security 에 대한 설정을 가능하게 함.
@EnableMethodSecurity(prePostEnabled = true) // 메서드 단위 보안 설정, security 6.x+
public class SecurityConfig {
  // Security에 대한 설정시 메서드의 리턴타입이 중요 ⭐
  // Security는 인증(Authentication)과 권한(Authority)으로 나누고 조합을 할 수 있음.

  // permitAll 할 내용을 복수개 설정하기 위한 문자 배열 선언
  private static final String[] AUTH_WHITELIST = {
      "/", "/auth/login", "/auth/accessDenied"
      , "/auth/authenticationFailure"
  };

  @Bean // SecurityFilterChain 설정시 모든 시큐리티 설정은 직접 지정해줘야 한다.
  protected SecurityFilterChain config(HttpSecurity httpSecurity) throws Exception {

    // csrf(Cross-Site Request Forgery) 교차 사이트 요청 위조
    httpSecurity.csrf(new Customizer<CsrfConfigurer<HttpSecurity>>() {
      @Override
      public void customize(CsrfConfigurer<HttpSecurity> httpSecurityCsrfConfigurer) {
        //httpSecurityCsrfConfigurer.disable();  // CSR방식에서는 disable 반드시 적용
      }
    });

    // authorizeHttpRequests():: http 요청 별로 인증 처리
    httpSecurity.authorizeHttpRequests(new Customizer<AuthorizeHttpRequestsConfigurer<org.springframework.security.config.annotation.web.builders.HttpSecurity>.AuthorizationManagerRequestMatcherRegistry>() {
      @Override
      public void customize(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {

        // 인증(Authentication) 시작 ====================
        // anyRequest(): 어떠한 요청에도 (이후에 requestMatchers()로 개별로 지정불가)
        //auth.anyRequest().permitAll();    // permitAll(): 모두 수용
        //auth.anyRequest().denyAll();      // 모두 거부
        //auth.anyRequest().authenticated();// authenticated():모두 인증

        // requestMatchers("url"): 해당 url에 대해서 개별 인증 설정
        //auth.requestMatchers("/").permitAll(); // 해당 주소 모두 허용
        //auth.requestMatchers("/sample/all").authenticated();// 해당 주소 인증 필요
        //auth.requestMatchers("/sample/all").permitAll(); // 해당 주소 모두 허용
        auth.requestMatchers(AUTH_WHITELIST).permitAll(); // 건별로 하기 번거로울 때
        auth.requestMatchers("/auth/modify").authenticated();
        auth.requestMatchers("/auth/logout").authenticated();
        //auth.requestMatchers("/logout/**").permitAll(); // logout 처리 페이지 지정

        //auth.requestMatchers("/sample/**").authenticated(); // 필터 주소 인증
        //auth.requestMatchers("/sample/all").permitAll(); // 개별 주소 허용
        //auth.requestMatchers("/sample/manager").permitAll(); // 개별 주소 허용
        //auth.requestMatchers("/sample/admin").permitAll(); // 개별 주소 허용

        // 인증(Authentication) 끝 ======================

        // hasRole(): 권한 여부에 대해서(인증과 권한은 2가지 중에 하나만 적용하여 사용)
        auth.requestMatchers("/sample/all").access(
            new WebExpressionAuthorizationManager(
                "hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')"
            )
        );
        auth.requestMatchers("/sample/manager").access( // 권한 복수일때
            new WebExpressionAuthorizationManager("hasRole('MANAGER') or hasRole('ADMIN')"));
        auth.requestMatchers("/sample/admin").hasRole("ADMIN");

      }
    });

    // security에서 제공되는 로그인 Default 적용시, 별도의 컨트롤러와 인증권한 불필요
    //httpSecurity.formLogin(Customizer.withDefaults());

    // login 커스터마이징 할 때
    httpSecurity.formLogin(httpSecurityFormLoginConfigurer -> {
      httpSecurityFormLoginConfigurer
          //.loginPage("/auth/login") // 커스텀 로그인 페이지 주소, 컨트롤러 등록 필요
          .loginProcessingUrl("/auth/loginSuccess") // 로그인을 처리할 주소, 컨트롤러 등록 불필요
          //.successHandler((req, res, auth) -> res.sendRedirect(req.getContextPath()))
          .successHandler(getAuthenticationSuccessHandler())
          .failureHandler(getAuthenticationFailureHandler())
      ;
    });

    // security에서 제공되는 로그아웃 Default 적용시, 별도의 컨트롤러와 인증권한 불필요
    httpSecurity.logout(Customizer.withDefaults());

    // logout 커스터마이징 할 때
    // 1) 로그아웃 페이지 작성(컨트롤러 등록)
    // 2) 작성된 로그아웃 페이지 이동후 form에 action="/logout" 설정
    // 3) <input type="hidden" name="_csrf" value="..."> 추가
    // 4) submit 버튼으로 post 전송
    /*httpSecurity.logout(new Customizer<LogoutConfigurer<HttpSecurity>>() {
      @Override
      public void customize(LogoutConfigurer<HttpSecurity> httpSecurityLogoutConfigurer) {
        httpSecurityLogoutConfigurer
            // 로그아웃 처리 지정, 로그아웃 페이지 이동은 컨트롤러에 처리
            .logoutUrl("/logout") // 현주소와 form action 주소는 동일해야함.
            .deleteCookies("JSESSIONID") // 쿠키 제거
            .invalidateHttpSession(true)  // 세션 제거
            .clearAuthentication(true)    // 인증 정보 제거
            .logoutSuccessUrl("/")
            //.logoutSuccessHandler((req,res,auth) -> res.sendRedirect("/login?logout"))
            //.logoutSuccessHandler(getCustomLogoutSuccessHandler())
        ;  // 로그아웃 성공 후 이동페이지 지정
            // logoutSuccessUrl과 logoutSuccessHandler 동시 지정시 logoutSuccessHandler 우선
      }
    });*/

    // 권한 부재로 접근 불가시 보여지는 페이지
    httpSecurity.exceptionHandling(new Customizer<ExceptionHandlingConfigurer<HttpSecurity>>() {
      @Override
      public void customize(ExceptionHandlingConfigurer<HttpSecurity> httpSecurityExceptionHandlingConfigurer) {
        httpSecurityExceptionHandlingConfigurer
            .accessDeniedHandler(getAccessDeniedHandler())
        //.accessDeniedPage("")
        ;
      }
    });

    httpSecurity.rememberMe(new Customizer<RememberMeConfigurer<HttpSecurity>>() {
      @Override
      public void customize(RememberMeConfigurer<HttpSecurity> httpSecurityRememberMeConfigurer) {
        httpSecurityRememberMeConfigurer.tokenValiditySeconds(60 * 60 * 24 * 7);//일주일
      }
    });

    // google social login
    httpSecurity.oauth2Login(new Customizer<OAuth2LoginConfigurer<HttpSecurity>>() {
      @Override
      public void customize(OAuth2LoginConfigurer<HttpSecurity> httpSecurityOAuth2LoginConfigurer) {
        httpSecurityOAuth2LoginConfigurer
            .successHandler(getAuthenticationSuccessHandler());
      }
    });

    return httpSecurity.build();
  } // SecurityFilterChain 끝

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationSuccessHandler getAuthenticationSuccessHandler() {
    return new CustomLoginSuccessHandler(passwordEncoder());
  }

  @Bean
  public LogoutSuccessHandler getCustomLogoutSuccessHandler() {
    return new CustomLogoutSuccessHandler();
  }

  @Bean
  public AuthenticationFailureHandler getAuthenticationFailureHandler() {
    return new CustomAuthenticationFailureHandler();
  }

  @Bean
  public AccessDeniedHandler getAccessDeniedHandler() {
    return new CustomAccessDeniedHandler();
  }


  // Security 설정 파일에 in-memory 방식으로 직접 지정
  /*@Bean
  public UserDetailsService userDetailsService(){
    UserDetails user1 = User.builder()
        .username("user1")
        .password("$2a$10$YPwRsyt5nQ9TCC05iFp8yO6g26Hdi1L3DT8hXUF8HszzCysim5VE6")
        .roles("USER")
        .build();
    UserDetails manager = User.builder()
        .username("manager")
        .password("$2a$10$YPwRsyt5nQ9TCC05iFp8yO6g26Hdi1L3DT8hXUF8HszzCysim5VE6")
        .roles("MANAGER")
        .build();
    UserDetails admin = User.builder()
        .username("admin")
        .password("$2a$10$YPwRsyt5nQ9TCC05iFp8yO6g26Hdi1L3DT8hXUF8HszzCysim5VE6")
        .roles("ADMIN", "MANAGER")
        .build();
    List<UserDetails> list = new ArrayList<>();
    list.add(user1);list.add(manager);list.add(admin);
    return new InMemoryUserDetailsManager(list);
  }*/
}
