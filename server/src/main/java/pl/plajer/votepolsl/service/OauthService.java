package pl.plajer.votepolsl.service;

import pl.plajer.votepolsl.data.dto.PostAuthAccessDto;

/**
 * @author Plajer
 * <p>
 * Created at 08.10.2022
 */
public interface OauthService {

  String requestToken();

  String requestData(PostAuthAccessDto dto);

}
