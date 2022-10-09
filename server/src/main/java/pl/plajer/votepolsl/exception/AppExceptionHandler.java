package pl.plajer.votepolsl.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Plajer
 * <p>
 * Created at 08.10.2022
 */
@RestControllerAdvice
public class AppExceptionHandler {

  @ExceptionHandler(RestException.class)
  public ResponseEntity handleException(RestException ex) {
    return ResponseEntity.status(ex.getHttpStatus()).body(new RestApiError(ex.getHttpStatus(), ex.getMessage()));
  }

}
