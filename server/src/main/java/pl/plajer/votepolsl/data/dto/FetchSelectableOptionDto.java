package pl.plajer.votepolsl.data.dto;

import lombok.Data;

import pl.plajer.votepolsl.data.SelectableOption;

/**
 * @author Plajer
 * <p>
 * Created at 08.10.2022
 */
@Data
public class FetchSelectableOptionDto {

  private long id;
  private String name;

  public FetchSelectableOptionDto fromEntity(SelectableOption option) {
    this.id = option.getId();
    this.name = option.getName();
    return this;
  }

}
