package com.finalproject.shelter.business.service.logic;

import com.finalproject.shelter.domain.model.entity.noticationDomain.Answer;
import com.finalproject.shelter.domain.model.entity.noticationDomain.Board;
import com.finalproject.shelter.domain.model.entity.noticationDomain.Category;
import com.finalproject.shelter.domain.model.entity.userDomain.Account;
import com.finalproject.shelter.domain.repository.AccountRepository;
import com.finalproject.shelter.domain.repository.AnswerRepository;
import com.finalproject.shelter.domain.repository.BoardRepository;
import com.finalproject.shelter.domain.repository.CategoryRepository;
import com.finalproject.shelter.presentation.settingform.SearchData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class BoardLogicService {

    private final BoardRepository boardRepository;

    private final CategoryRepository categoryRepository;

    private final AnswerRepository answerRepository;

    private final AccountRepository accountRepository;

    private Board newboard;
    private Long ids;
    private Board board1;

    public BoardLogicService(BoardRepository boardRepository, CategoryRepository categoryRepository, AnswerRepository answerRepository, AccountRepository accountRepository) {
        this.boardRepository = boardRepository;
        this.categoryRepository = categoryRepository;
        this.answerRepository = answerRepository;
        this.accountRepository = accountRepository;
    }

    public Page<Board> findTitle(SearchData searchData){
        return boardRepository.findBoardByCategoryIdAndTitleContainingAndContentsContainingOrderByRegisteredAtDescIdDesc
                (searchData.getId(),searchData.getSearchText(),"",searchData.getPageable());
    }
    public Page<Board> findContents(SearchData searchData){
        return boardRepository.findBoardByCategoryIdAndTitleContainingAndContentsContainingOrderByRegisteredAtDescIdDesc
                (searchData.getId(),"",searchData.getSearchText(),searchData.getPageable());
    }
    public Page<Board> findCategorys(String id, Pageable pageable){
        return boardRepository.findBoardByCategoryId(Long.parseLong(id),pageable);
    }

    public Board readCategory(String id){

        List<Board> board = boardRepository.findBoardByCategoryId(Long.parseLong(id));
        board1 = Board.builder().build();

        if(board.isEmpty()){
            Optional<Category> category1 = categoryRepository.findById(Long.parseLong(id));
            if (category1.isPresent()){
                log.info("new board");
                category1.ifPresent(select->{
                    board1.setCategory(select);
            });}else{
                log.error("category is empty");
                return null;
            }
        }else {
            log.info("old board");
            board1= board.get(0);
        }
        return board1;
    }
    public Board readBoard(String id){
        Optional<Board> board = boardRepository.findBoardById(Long.parseLong(id));
        if (board.isEmpty()){
            log.error("id is empty");
            board1=Board.builder()
                    .build();
        }
        board.ifPresent(select->{
            board1 = select;
        });
        return board1;
    }
    public Board newUserBoard(Category category){
        Account account = accountRepository.findByUsername(userName());

        Board board1 = Board.builder()
                .nickname(account.getIdentity())
                .user(account)
                .category(category)
                .build();

        return board1;
    }
    private String userName(){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        return authentication.getName();
    }

    public Board readBoardview(String id){
        Optional<Board> board = boardRepository.findBoardById(Long.parseLong(id));
        if (board.isEmpty()){
            log.error("board is empty");
            board1=Board.builder().build();
        }
        board.ifPresent(select->{
            select.setViewBoard(select.getViewBoard()+1);
            board1 = boardRepository.save(select);
            String REGEX = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

            Pattern pattern = Pattern.compile(REGEX);
            Matcher matcher = pattern.matcher(select.getContents());
            while (matcher.find()) {
                select.setContents(select.getContents().replace(matcher.group(),"<a style='color:skyblue' href="+matcher.group()+">"+matcher.group()+"</a>"));
            }
        });
        return board1;
    }
    public List<Board> bestWeekView(Long id){
        List<Board> weekview = boardRepository.findTop5ByCategoryIdAndRegisteredAtBetweenOrderByViewBoardDesc
                (id, LocalDate.now().minusDays(7),LocalDate.now());
        if (weekview!=null){
            return weekview;
        }else {
            log.error("week data is empty");
            return null;
        }
    }
    public List<Board> bestMonthView(Long id){
        List<Board> monthview = boardRepository.findTop5ByCategoryIdAndRegisteredAtBetweenOrderByViewBoardDesc
                (id, LocalDate.now().minusMonths(1),LocalDate.now());
        if (monthview!=null){
            return monthview;
        }else {
            log.error("month data is empty");
            return null;
        }
    }
    public String deleteid(String id){
        Optional<Board> board = boardRepository.findBoardById(Long.parseLong(id));
        if (board.isEmpty()){
            log.error("board is empty");
            ids=null;
        }
        board.ifPresent(select->{
            newboard = select;
            ids = select.getCategory().getId();
            boardRepository.delete(select);
            List<Answer> answer = answerRepository.findAnswerByBoardId(newboard.getId());
            if (answer!=null){
                answer.stream().forEach(select1->{
                    answerRepository.delete(select1);
                });
            }
        });
        return String.valueOf(ids);
    }
    public Board postservice(Board board){
        Optional<Board> postboard = boardRepository.findBoardById(board.getId());
        String regex = "<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>";
        String tagRemove=board.getContents().replaceAll(regex, "(<> 안에 한글만 사용할수 있습니다!)");
        board.setContents(tagRemove);
        postboard.ifPresent(select->{
            log.info("modify");
            select.setTitle(board.getTitle())
                    .setContents(board.getContents());
            newboard = boardRepository.save(select);
        });
        if (postboard.isEmpty()){
            log.info("new board write");
            Board selectboard = board;
            selectboard.setNickname(board.getUser()
                    .getIdentity());
            newboard = boardRepository.save(selectboard);
        }
        return newboard;
    }
}
