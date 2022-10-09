package pl.plajer.votepolsl.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * @author Plajer
 * <p>
 * Created at 08.10.2022
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RequestedOption {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private long id;

  private int priority;
  @ManyToOne(fetch = FetchType.LAZY)
  private SelectableOption selectableOption;
  @ManyToOne(fetch = FetchType.LAZY)
  private User user;

}
