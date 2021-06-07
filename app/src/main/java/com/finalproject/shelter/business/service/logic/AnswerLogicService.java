package com.finalproject.shelter.business.service.logic;

import com.finalproject.shelter.domain.model.entity.noticationDomain.Answer;
import com.finalproject.shelter.domain.model.entity.noticationDomain.Board;
import com.finalproject.shelter.domain.repository.AccountRepository;
import com.finalproject.shelter.domain.repository.AnswerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class AnswerLogicService {

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private AccountRepository accountRepository;

    public List<Answer> readAnswer(String id) {
        List<Answer> answers = answerRepository.findAnswerByBoardId(Long.parseLong(id));
        findTag(answers);
        return answers;
    }
    public Answer readUser(Board eachboard) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return read(username,eachboard);
    }
    public Answer save(Answer answer) {
        String regex = "<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>";
        String tagRemove = answer.getAnswerText().replaceAll(regex, "<>안에 한글만 사용할수 있습니다.");
        answer.setAnswerText(tagRemove);
        return answerRepository.save(saveAnswer(answer));
    }
    public void delete(String id) {
        Optional<Answer> answer = answerRepository.findAnswerById(Long.parseLong(id));
        answer.ifPresent(select -> answerRepository.delete(select));
    }

    private void findTag(List<Answer> answers){
        answers.stream().forEach(select -> {
            String REGEX = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
            initPattern(REGEX,select);
        });
    }
    private void initPattern(String regex,Answer answer){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(answer.getAnswerText());
        findMatcher(matcher,answer);
    }
    private void findMatcher(Matcher matcher,Answer answer){
        while (matcher.find()) {
            answer.setAnswerText(answer.getAnswerText().replace(matcher.group(), "<a style='color:skyblue' href=" + matcher.group() + ">" + matcher.group() + "</a>"));
        }
    }
    private Answer read(String username,Board eachboard){
        Answer answer = Answer.builder()
                .nickname(accountRepository.findByUsername(username).getIdentity())
                .board(eachboard)
                .build();
        return answer;
    }
    private Answer saveAnswer(Answer answer){
        Answer saveAnswer = Answer.builder()
                .nickname(answer.getNickname())
                .answerText(answer.getAnswerText())
                .board(answer.getBoard())
                .build();
        return saveAnswer;
    }

}
