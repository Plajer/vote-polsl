package pl.plajer.votepolsl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import pl.plajer.votepolsl.data.dto.FetchSelectableOptionDto;
import pl.plajer.votepolsl.data.dto.PostRequestVoteDto;
import pl.plajer.votepolsl.service.OptionService;

import javax.validation.Valid;

import java.util.List;

/**
 * @author Plajer
 * <p>
 * Created at 08.10.2022
 */
@RestController
@CrossOrigin
public class OptionController {

  private final OptionService optionService;

  @Autowired
  public OptionController(OptionService optionService) {
    this.optionService = optionService;
  }

  @GetMapping("v1/options")
  public List<FetchSelectableOptionDto> getAllOptions() {
    return optionService.getAllOptions();
  }

  @PostMapping("v1/request")
  public ResponseEntity postRequest(@Valid @RequestBody PostRequestVoteDto dto) {
    return optionService.postRequestOptions(dto);
  }

}
