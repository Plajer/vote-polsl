package pl.plajer.votepolsl.exception;

import org.springframework.http.HttpStatus;

/**
 * @author Plajer
 * <p>
 * Created at 08.10.2022
 */
public class RestException extends RuntimeException {

  private final HttpStatus httpStatus;

  public RestException(HttpStatus status) {
    super();
    this.httpStatus = status;
  }

  public RestException(HttpStatus status, String message) {
    super(message);
    this.httpStatus = status;
  }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }
}
