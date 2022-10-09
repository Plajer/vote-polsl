package pl.plajer.votepolsl.data.dto;

import lombok.Data;

import java.util.List;

/**
 * @author Plajer
 * <p>
 * Created at 08.10.2022
 */
@Data
public class PostRequestVoteDto {

  private long indexId;
  private List<RequestDto> requestedOptions;

  @Data
  public static class RequestDto {

    private int priority;
    private long id;

  }

}
