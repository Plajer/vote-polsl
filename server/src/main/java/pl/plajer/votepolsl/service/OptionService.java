package pl.plajer.votepolsl.service;

import org.springframework.http.ResponseEntity;

import pl.plajer.votepolsl.data.dto.FetchSelectableOptionDto;
import pl.plajer.votepolsl.data.dto.PostRequestVoteDto;

import java.util.List;

/**
 * @author Plajer
 * <p>
 * Created at 08.10.2022
 */
public interface OptionService {

  List<FetchSelectableOptionDto> getAllOptions();

  ResponseEntity postRequestOptions(PostRequestVoteDto dto);

  ResponseEntity getGenerateResults(String password);

}
