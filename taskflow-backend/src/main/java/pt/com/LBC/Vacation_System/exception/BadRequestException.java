package pt.com.LBC.Vacation_System.exception;

public class BadRequestException extends RuntimeException {
  public BadRequestException(String message) { super(message); }
}
