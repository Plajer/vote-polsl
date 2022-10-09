package pl.plajer.votepolsl.data.dto;

import lombok.Data;

/**
 * @author Plajer
 * <p>
 * Created at 09.10.2022
 */
@Data
public class PostAuthAccessDto {

  private String code;
  private String authUrl;

}
