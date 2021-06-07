package com.finalproject.shelter.business.service;

import com.finalproject.shelter.ShelterApplicationTests;
import com.finalproject.shelter.domain.model.entity.noticationDomain.Answer;
import com.finalproject.shelter.domain.model.entity.noticationDomain.Board;
import com.finalproject.shelter.domain.repository.AnswerRepository;
import com.finalproject.shelter.domain.repository.BoardRepository;
import com.finalproject.shelter.domain.repository.CategoryRepository;
import com.finalproject.shelter.business.service.logic.BoardLogicService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@Slf4j
public class BoardLogicServiceTest extends ShelterApplicationTests {

    @Autowired
    private BoardLogicService boardLogicService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @DisplayName("내용,제목포함 카테고리 검색 테스트")
    @Test
    public void findCategoryfindTitleContents(){
        Pageable pageable = PageRequest.of(0,10);

        Page<Board> board = boardLogicService.findCategoryIdTitleContents("21","title","박종천",pageable);
        Assertions.assertFalse(board.isEmpty());

        board.stream().forEach(select->{
            Assertions.assertTrue(select.getTitle().contains("박종천"));
            assertThat(select.getCategory().getId()).isEqualTo(21L);
        });


        Page<Board> board1 = boardLogicService.findCategoryIdTitleContents("21","Contents","스피커",pageable);
        Assertions.assertFalse(board1.isEmpty());

        board1.stream().forEach(select->{
            Assertions.assertTrue(select.getContents().contains("스피커"));
            assertThat(select.getCategory().getId()).isEqualTo(21L);
        });

    }

    @DisplayName("레코드 categoryId 조회 테스트")
    @Test
    public void readcategory(){

        Board board = boardLogicService.readCategory("1");
        assertThat(board.getCategory().getId()).isEqualTo(1L);

        Board board1 = boardLogicService.readCategory("100");
        Assertions.assertTrue(board1==null);
    }

    @DisplayName("레코드 boardid 조회 테스트")
    @Test
    public void readboard(){
        Board board = boardLogicService.readBoard("3");
        assertThat(board.getId()).isEqualTo(3L);

        Board board1 = boardLogicService.readBoard("1000");
        assertThat(board1).isEqualTo(new Board());
    }



    @DisplayName("board Id 조회후 Contents테그에 링크 수정 테스트")
    @Test
    public void readBoardview(){

        Board board = boardLogicService.readBoardview("8");
        assertThat(board.getId()).isEqualTo(8L);

        if(board.getContents().contains("http")||board.getContents().contains("https")){
            Assertions.assertTrue(board.getContents().contains("<a style"));
            log.info(board.getContents());
        }else{
            Assertions.assertFalse(board.getContents().contains("<a style"));
        }


        Board board1 = boardLogicService.readBoardview("1000");
        assertThat(board1).isEqualTo(new Board());

    }

    @DisplayName("주간 인기 리스트 조회 테스트")
    @Test
    public void bestweekview(){

        for (int i=1; i<7; i++){
            Board board1= Board.builder()
                    .title("test"+i)
                    .contents("testq"+i)
                    .nickname("aaa")
                    .category(categoryRepository.getOne(1L))
                    .build();
            boardRepository.save(board1);
        }

        List<Board> board = boardLogicService.bestweekview("1");
        Assertions.assertFalse(board.isEmpty());
        assertThat(board.stream().count()).isEqualTo(5);
        for (int i=1; i<board.stream().count(); i++){
            Assertions.assertTrue(board.get(i-1).getViewBoard()>=board.get(i).getViewBoard());
        }

        if(board!=null){
            board.stream().forEach(select->{
                assertThat(select.getRegisteredAt()).isBetween(LocalDate.now().minusDays(7),LocalDate.now());
            });
        }


    }

    @DisplayName("월간 인기 리스트 조회 테스트")
    @Test
    public void bestmonthview(){

        for (int i=1; i<7; i++){
            Board board1= Board.builder()
                    .title("test"+i)
                    .contents("testq"+i)
                    .nickname("aaa")
                    .category(categoryRepository.getOne(1L))
                    .build();
            boardRepository.save(board1);
        }

        List<Board> board = boardLogicService.bestmonthview("1");
        Assertions.assertFalse(board.isEmpty());

        assertThat(board.stream().count()).isEqualTo(5);
        for (int i=1; i<board.stream().count(); i++){
            Assertions.assertTrue(board.get(i-1).getViewBoard()>=board.get(i).getViewBoard());
        }

        if(board!=null){
            board.stream().forEach(select->{
                assertThat(select.getRegisteredAt()).isBetween(LocalDate.now().minusMonths(1),LocalDate.now());
            });
        }

    }

    @DisplayName("게시판,댓글 레코드 삭제 테스트")
    @Test
    public void deleteid(){
        String ids = boardLogicService.deleteid("6");
        Optional<Board> board = boardRepository.findBoardById(Long.parseLong(ids));
        Assertions.assertTrue(board.isEmpty());

        List<Answer> answer = answerRepository.findAnswerByBoardId(6L);
        Assertions.assertTrue(answer.isEmpty());

        String loseid = boardLogicService.deleteid("1000");
        assertThat(loseid).isEqualTo("null");
    }

    @DisplayName("레코드 태그내용 삭제 테스트")
    @Test
    public void postservice(){

        Board board1= Board.builder()
                .id(100L)
                .title("test")
                .contents("<br>testq<a></a>")
                .nickname("aaa")
                .category(categoryRepository.getOne(1L))
                .build();

        boardRepository.save(board1);
        Optional<Board> board = boardRepository.findBoardById(100L);

        board.ifPresent(select->{

            Assertions.assertTrue(select.getContents().contains("<br>testq<li></a>"));

            Board newboard = boardLogicService.postservice(select);

            Assertions.assertFalse(newboard.getContents().contains("<br>"));
            Assertions.assertFalse(newboard.getContents().contains("<li>"));
            Assertions.assertFalse(newboard.getContents().contains("</a>"));
            assertThat(newboard.getContents()).isEqualTo("testq");

            Assertions.assertFalse(select.getContents().equals(newboard.getContents()));
        });
    }
}
