package com.finalproject.shelter.business.service.logic;

import com.finalproject.shelter.domain.model.entity.noticationDomain.Answer;
import com.finalproject.shelter.domain.model.entity.noticationDomain.Board;
import com.finalproject.shelter.domain.model.entity.noticationDomain.Category;
import com.finalproject.shelter.domain.model.entity.userDomain.Account;
import com.finalproject.shelter.domain.repository.AccountRepository;
import com.finalproject.shelter.domain.repository.AnswerRepository;
import com.finalproject.shelter.domain.repository.BoardRepository;
import com.finalproject.shelter.domain.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
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

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private AccountRepository accountRepository;

    private Board newboard;
    private Long ids;
    private Board board1;

    public Page<Board> findCategoryIdTitleContents(String id, String select, String searchText, Pageable pageable){

        if (select.equals("title")){
            Page<Board> boards = boardRepository.findBoardByCategoryIdAndTitleContainingAndContentsContainingOrderByRegisteredAtDescIdDesc
                    (Long.parseLong(id),searchText,"",pageable);
            if (boards!=null){
                return boards;
            }
        }else{
            Page<Board> boards = boardRepository.findBoardByCategoryIdAndTitleContainingAndContentsContainingOrderByRegisteredAtDescIdDesc
                    (Long.parseLong(id),"",searchText,pageable);
            if (boards!=null){
                return boards;
            }
        }

        return null;
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
            board1=Board.builder().build();
        }
        board.ifPresent(select->{
            board1 = select;
        });
        return board1;
    }
    public Board newuserboard(Board board){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Account account = accountRepository.findByUsername(username);
        Board board1 = Board.builder()
                .nickname(account.getIdentity())
                .user(account)
                .category(board.getCategory())
                .build();
        return board1;
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
    public List<Board> bestweekview(String id){
        List<Board> weekview = boardRepository.findTop5ByCategoryIdAndRegisteredAtBetweenOrderByViewBoardDesc
                (Long.parseLong(id), LocalDate.now().minusDays(7),LocalDate.now());
        if (weekview!=null){
            return weekview;
        }else {
            log.error("week data is empty");
            return null;
        }
    }
    public List<Board> bestmonthview(String id){
        List<Board> monthview = boardRepository.findTop5ByCategoryIdAndRegisteredAtBetweenOrderByViewBoardDesc
                (Long.parseLong(id), LocalDate.now().minusMonths(1),LocalDate.now());
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
            selectboard.setNickname(board.getUser().getIdentity());
            newboard = boardRepository.save(selectboard);
        }
        return newboard;
    }
}
