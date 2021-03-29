package com.finalproject.shelter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finalproject.shelter.ShelterApplicationTests;
import com.finalproject.shelter.controller.page.board.BoardFormPageController;
import com.finalproject.shelter.model.entity.Account;
import com.finalproject.shelter.model.entity.Board;
import com.finalproject.shelter.repository.AccountRepository;
import com.finalproject.shelter.service.AccountService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
public class BoardFormPageControllerTest extends ShelterApplicationTests {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void login() throws Exception {
        Optional<Account> account = accountRepository.findById(122L);

        account.ifPresent(select->{
            accountService.login(select);
        });
    }

    @DisplayName("form 작성 연결 테스트")
    @Test
    public void writeview() throws Exception {

        mockMvc.perform(get("/board/form?name=write&categoryid=1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("pages/form"))
                .andExpect(model().hasNoErrors())
                .andDo(print());

        mockMvc.perform(get("/board/form?boardid=6&name=modify&categoryid=1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("pages/form"))
                .andExpect(model().hasNoErrors())
                .andDo(print());
    }
}
