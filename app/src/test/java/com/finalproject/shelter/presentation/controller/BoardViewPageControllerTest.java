package com.finalproject.shelter.presentation.controller;

import com.finalproject.shelter.ShelterApplicationTests;
import com.finalproject.shelter.domain.model.entity.userDomain.Account;
import com.finalproject.shelter.domain.model.entity.noticationDomain.Answer;
import com.finalproject.shelter.domain.model.entity.noticationDomain.Board;
import com.finalproject.shelter.domain.repository.AccountRepository;
import com.finalproject.shelter.business.service.account.AccountService;
import com.finalproject.shelter.business.service.logic.AnswerLogicService;
import com.finalproject.shelter.business.service.logic.BoardLogicService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@Slf4j
@AutoConfigureMockMvc
@SpringBootTest
public class BoardViewPageControllerTest extends ShelterApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoardLogicService boardLogicService;

    @Autowired
    private AnswerLogicService answerLogicService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @BeforeEach
    public void login() throws Exception {
        Optional<Account> account = accountRepository.findById(122L);

        account.ifPresent(select->{
            accountService.login(select);
        });
    }

    @DisplayName("view 페이지 조회 확인 테스트")
    @Test
    public void listview() throws Exception {

        Board eachboard = boardLogicService.readBoardview("3");
        List<Answer> answerList = answerLogicService.readAnswer("3");
        List<Board> weekview = boardLogicService.bestweekview(String.valueOf(eachboard.getCategory().getId()));
        List<Board> monthview = boardLogicService.bestmonthview(String.valueOf(eachboard.getCategory().getId()));

        Answer answer = answerLogicService.writeuserinfo(eachboard,accountRepository); //Error

        mockMvc.perform(get("/board/view?id=3"))
                .andExpect(model().attribute("eachboard",eachboard))
                .andExpect(model().attribute("Answer",answer))
                .andExpect(model().attribute("Answers",answerList))
                .andExpect(model().attribute("weekview",weekview))
                .andExpect(model().attribute("monthview",monthview))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("pages/view"));
    }

}
