package com.finalproject.shelter.business.service;

import com.finalproject.shelter.ShelterApplicationTests;
import com.finalproject.shelter.domain.model.entity.noticationDomain.Answer;
import com.finalproject.shelter.domain.repository.AnswerRepository;
import com.finalproject.shelter.domain.repository.BoardRepository;
import com.finalproject.shelter.business.service.logic.AnswerLogicService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@Transactional
@Slf4j
public class AnswerLogicServiceTest extends ShelterApplicationTests {

    @Autowired
    private AnswerLogicService answerLogicService;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private BoardRepository boardRepository;

    @DisplayName("http링크에 href 태그 삽입 테스트")
    @Test
    public void readAnswer(){

        Answer answer = Answer.builder()
                .nickname("test1234")
                .answerText("네이버 주소 : http://www.naver.com123fewfa sdfewgwe")
                .board(boardRepository.getOne(3L))
                .build();

        Answer newanswer = answerRepository.save(answer);
        Assertions.assertFalse(newanswer.getAnswerText().contains("<a style"));

        List<Answer> answerList = answerLogicService.readAnswer("3");
        Assertions.assertFalse(answerList.isEmpty());

        if(answerList!=null){
            answerList.stream().forEach(select->{
                Assertions.assertTrue(select.getAnswerText().contains("<a style='color:skyblue' href="+"http://www.naver.com123fewfa"+">http://www.naver.com123fewfa</a>"));
            });
        }
    }

    @DisplayName("태그 지우고 데이터베이스 저장 확인 테스트")
    @Test
    public void save(){

        Answer answer = Answer.builder()
                .nickname("aaww")
                .answerText("<br><a><li><span><textarea><wwwwwwwwwwww>sdfdsjklfe")
                .build();

        Answer saveanswer = answerLogicService.save(answer);

        Assertions.assertFalse(saveanswer.getAnswerText().contains("<br>"));
        Assertions.assertFalse(saveanswer.getAnswerText().contains("<li>"));
        Assertions.assertFalse(saveanswer.getAnswerText().contains("<a>"));
        Assertions.assertFalse(saveanswer.getAnswerText().contains("<span>"));
        Assertions.assertFalse(saveanswer.getAnswerText().contains("<textarea>"));
        Assertions.assertFalse(saveanswer.getAnswerText().contains("<wwwwwwwwwwww>"));

        assertThat(saveanswer.getNickname()).isEqualTo("aaww");
        assertThat(saveanswer.getAnswerText()).isEqualTo("<>안에 한글만 사용할수 있습니다.<>안에 한글만 사용할수 있습니다.<>안에 한글만 사용할수 있습니다.<>안에 한글만 사용할수 있습니다.<>안에 한글만 사용할수 있습니다.<>안에 한글만 사용할수 있습니다.sdfdsjklfe");
    }

    @DisplayName("댓글 id로 조회 후 삭제 확인 테스트")
    @Test
    public void delete(){
        answerLogicService.delete("5");

        Optional<Answer> answer = answerRepository.findAnswerById(5L);
        Assertions.assertTrue(answer.isEmpty());
    }

}
