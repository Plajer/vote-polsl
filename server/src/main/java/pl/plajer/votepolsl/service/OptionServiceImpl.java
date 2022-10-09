package pl.plajer.votepolsl.service;

import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.mapping.Selectable;
import org.hibernate.sql.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import pl.plajer.votepolsl.data.RequestedOption;
import pl.plajer.votepolsl.data.SelectableOption;
import pl.plajer.votepolsl.data.User;
import pl.plajer.votepolsl.data.dto.FetchSelectableOptionDto;
import pl.plajer.votepolsl.data.dto.PostRequestVoteDto;
import pl.plajer.votepolsl.exception.RestException;
import pl.plajer.votepolsl.repository.OptionRepository;
import pl.plajer.votepolsl.repository.UserRepository;
import pl.plajer.votepolsl.util.DiscordWebhook;

import javax.swing.text.html.Option;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author Plajer
 * <p>
 * Created at 08.10.2022
 */
@Service
public class OptionServiceImpl implements OptionService {

  private final UserRepository userRepository;
  private final OptionRepository optionRepository;

  @Autowired
  public OptionServiceImpl(UserRepository userRepository, OptionRepository optionRepository) {
    this.userRepository = userRepository;
    this.optionRepository = optionRepository;
  }

  @Override
  public List<FetchSelectableOptionDto> getAllOptions() {
    return optionRepository.findAll().stream().map(o -> new FetchSelectableOptionDto().fromEntity(o)).collect(Collectors.toList());
  }

  @Override
  public ResponseEntity postRequestOptions(PostRequestVoteDto dto) {
    Optional<User> optional = userRepository.findByIndexId(dto.getIndexId());
    if(!optional.isPresent()) {
      throw new RestException(HttpStatus.BAD_REQUEST, "Zaloguj sie wpierw!");
    }
    User user = optional.get();
    if(!user.getRequestedOptions().isEmpty()) {
      throw new RestException(HttpStatus.BAD_REQUEST, "Wybrales juz swoje opcje!");
    }
    fillOptions(user, dto);

    CompletableFuture.runAsync(() -> {
      DiscordWebhook webhook = new DiscordWebhook(System.getenv("WEBHOOK_URL"));
      webhook.setUsername("Nowy request");
      StringBuilder builder = new StringBuilder();
      for(RequestedOption requestedOption : user.getRequestedOptions()) {
        builder.append(requestedOption.getSelectableOption().getName()).append(" (PRIORYTET ").append(requestedOption.getPriority()).append(")\n");
      }
      DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
      embed.setDescription("**Index:** " + user.getIndexId() + "\n**Opcje:**\n" + builder.toString());
      webhook.addEmbed(embed);
      try {
        webhook.execute();
      } catch(IOException e) {
        e.printStackTrace();
      }
    });
    return ResponseEntity.ok().build();
  }

  private void fillOptions(User user, PostRequestVoteDto dto) {
    List<RequestedOption> options = new ArrayList<>();
    dto.getRequestedOptions().forEach(req -> {
      Optional<SelectableOption> optional = optionRepository.findById(req.getId());
      if(optional.isPresent()) {
        RequestedOption requestedOption = new RequestedOption();
        requestedOption.setUser(user);
        requestedOption.setSelectableOption(optional.get());
        requestedOption.setPriority(req.getPriority());
        if(options.stream().anyMatch(o -> o.getPriority() == req.getPriority())) {
          throw new RestException(HttpStatus.BAD_REQUEST, "Błędne polecenie");
        }
        options.add(requestedOption);
      }
    });
    user.setRequestedOptions(options);
    userRepository.save(user);
  }

  //todo
  @Override
  public ResponseEntity getGenerateResults(String password) {
    if(!System.getenv("RESULTS_PASSWORD").equals(password)) {
      throw new RestException(HttpStatus.BAD_REQUEST, "");
    }
    List<User> users = userRepository.findAll();
    List<SelectableOption> options = optionRepository.findAll();
    //todo
    return ResponseEntity.ok().build();
  }
}
