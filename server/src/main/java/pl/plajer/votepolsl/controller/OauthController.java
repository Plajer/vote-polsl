package pl.plajer.votepolsl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import pl.plajer.votepolsl.data.dto.PostAuthAccessDto;
import pl.plajer.votepolsl.service.OauthService;

import javax.validation.Valid;

/**
 * @author Plajer
 * <p>
 * Created at 08.10.2022
 */
@RestController
@CrossOrigin
public class OauthController {

  private final OauthService oauthService;

  @Autowired
  public OauthController(OauthService oauthService) {
    this.oauthService = oauthService;
  }

  @GetMapping("v1/oauth/request")
  public String getOauthToken() {
    return oauthService.requestToken();
  }

  @PostMapping("v1/oauth/access")
  public String getOauthToken(@Valid @RequestBody PostAuthAccessDto dto) {
    return oauthService.requestData(dto);
  }

}
