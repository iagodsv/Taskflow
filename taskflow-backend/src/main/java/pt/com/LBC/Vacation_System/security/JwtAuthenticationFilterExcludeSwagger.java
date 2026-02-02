package pt.com.LBC.Vacation_System.security;

import java.io.IOException;
import java.util.List;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilterExcludeSwagger extends OncePerRequestFilter {
  private final JwtAuthenticationFilter delegate;
  private static final List<String> SWAGGER_PATHS = List.of(
      "/swagger-ui",
      "/swagger-ui/",
      "/swagger-ui.html",
      "/v3/api-docs",
      "/webjars/",
      "/swagger-resources/");

  public JwtAuthenticationFilterExcludeSwagger(JwtAuthenticationFilter delegate) {
    this.delegate = delegate;

  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String path = request.getServletPath();
    boolean isSwagger = SWAGGER_PATHS.stream().anyMatch(path::startsWith);
    if (isSwagger) {
      filterChain.doFilter(request, response);
    } else {
      delegate.doFilter(request, response, filterChain);
    }
  }
}