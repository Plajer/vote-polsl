package pl.plajer.votepolsl.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

/**
 * @author Plajer
 * <p>
 * Created at 08.10.2022
 */
@Getter
@AllArgsConstructor
public class RestApiError {

  private HttpStatus status;
  private List<String> errors;

  public RestApiError(HttpStatus status, String error) {
    this.status = status;
    this.errors = Collections.singletonList(error);
  }

}
