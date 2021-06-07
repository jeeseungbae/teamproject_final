package com.finalproject.shelter.presentation.controller;

import com.finalproject.shelter.ShelterApplicationTests;
import com.finalproject.shelter.domain.model.entity.userDomain.Account;
import com.finalproject.shelter.domain.model.entity.noticationDomain.Board;
import com.finalproject.shelter.domain.repository.AccountRepository;
import com.finalproject.shelter.domain.repository.BoardRepository;
import com.finalproject.shelter.business.service.account.AccountService;
import com.finalproject.shelter.business.service.logic.BoardLogicService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@WebAppConfiguration
@Slf4j
public class BoardFormPageControllerTest extends ShelterApplicationTests {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BoardLogicService boardLogicService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void login() throws Exception {
        log.info("before : 로그인 시작");
        Optional<Account> account = accountRepository.findById(122L);

        account.ifPresent(select->{
            accountService.login(select);
        });
    }

    @DisplayName("form 작성 및 수정 Controller 테스트")
    @Test
    public void writeview() throws Exception {

        Board board = boardLogicService.readCategory("1");
        Board board1 = boardLogicService.newuserboard(board);

        mockMvc.perform(get("/board/form?name=write&categoryid=1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("pages/form"))
                .andExpect(model().attribute("board",board1))
                .andExpect(model().attribute("eachboard",board));

        mockMvc.perform(get("/board/form?boardid=6&name=modify&categoryid=1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("pages/form"))
                .andExpect(model().attribute("eachboard",board))
                .andExpect(model().attribute("board",board));

        mockMvc.perform(get("/board/form?boardid=6&name=sfsefag&categoryid=1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main"));

    }


    @DisplayName("Post board저장 테스트")
    @Test
    public void postform() throws Exception {

        MultiValueMap<String,String> content = new LinkedMultiValueMap<>();
        content.add("title","Titletest");
        content.add("nickname","asdfw");
        content.add("user","76");
        content.add("category","1");
        content.add("contents","lkfjseisjflesselkfjliagjoi4ewghwoigilasdgj");

        mockMvc.perform(post("/board/form")
                .params(content)
                .with(csrf()))
                .andExpect(status().is3xxRedirection());

        Optional<Board> board = boardRepository.findBoardByTitle("Titletest");
        Assertions.assertTrue(board.isPresent());
        board.ifPresent(select->{
            assertThat(select.getTitle()).isEqualTo("Titletest");
            assertThat(select.getNickname()).isNotEqualTo("asdfw");
            assertThat(select.getUser().getId()).isEqualTo(76L);
            assertThat(select.getCategory().getId()).isEqualTo(1L);
        });


    }
}
