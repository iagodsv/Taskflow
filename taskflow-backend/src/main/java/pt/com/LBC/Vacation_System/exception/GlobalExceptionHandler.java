package pt.com.LBC.Vacation_System.exception;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private ResponseEntity<ApiError> build(HttpStatus status, String message, HttpServletRequest req) {
    ApiError body = new ApiError(
        OffsetDateTime.now(),
        status.value(),
        status.getReasonPhrase(),
        message,
        req != null ? req.getRequestURI() : null);
    return ResponseEntity.status(status).body(body);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ApiError> handleNotFound(NotFoundException ex, HttpServletRequest req) {
    return build(HttpStatus.NOT_FOUND, ex.getMessage(), req);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex, HttpServletRequest req) {
    return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiError> handleBusiness(BusinessException ex, HttpServletRequest req) {
    return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), req);
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<ApiError> handleForbidden(ForbiddenException ex, HttpServletRequest req) {
    return build(HttpStatus.FORBIDDEN, ex.getMessage(), req);
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ApiError> handleUnauthorized(UnauthorizedException ex, HttpServletRequest req) {
    return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), req);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
    String msg = ex.getBindingResult().getAllErrors().stream()
        .map(err -> err.getDefaultMessage())
        .findFirst()
        .orElse("Dados inválidos");
    return build(HttpStatus.BAD_REQUEST, msg, req);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiError> handleConstraint(ConstraintViolationException ex, HttpServletRequest req) {
    return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
    return build(HttpStatus.FORBIDDEN, "Acesso negado", req);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ApiError> handleAuth(AuthenticationException ex, HttpServletRequest req) {
    return build(HttpStatus.UNAUTHORIZED, "Não autenticado", req);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req, WebRequest webReq) {
    return build(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", req);
  }
}
