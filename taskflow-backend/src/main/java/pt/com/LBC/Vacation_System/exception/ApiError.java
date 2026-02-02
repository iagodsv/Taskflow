package pt.com.LBC.Vacation_System.exception;

import java.time.OffsetDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estrutura padrão de erro retornada pela API")
public class ApiError {
  @Schema(description = "Data e hora do erro (UTC)", example = "2026-02-01T23:55:00Z")
  private OffsetDateTime timestamp;

  @Schema(description = "Código HTTP", example = "404")
  private int status;

  @Schema(description = "Nome do status HTTP", example = "Not Found")
  private String error;

  @Schema(description = "Mensagem detalhada do erro")
  private String message;

  @Schema(description = "Caminho do endpoint", example = "/taskflow/api/resource/1")
  private String path;

  public ApiError() {
  }

  public ApiError(OffsetDateTime timestamp, int status, String error, String message, String path) {
    this.timestamp = timestamp;
    this.status = status;
    this.error = error;
    this.message = message;
    this.path = path;
  }

  public OffsetDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(OffsetDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }
}
