package com.finalproject.shelter.presentation.controller.view.board;

import com.finalproject.shelter.domain.model.entity.noticationDomain.Answer;
import com.finalproject.shelter.domain.model.entity.noticationDomain.Board;
import com.finalproject.shelter.business.service.logic.AnswerLogicService;
import com.finalproject.shelter.business.service.logic.BoardLogicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/board/view")
public class BoardViewPageController {
    @Autowired
    private BoardLogicService boardLogicService;
    @Autowired
    private AnswerLogicService answerLogicService;

    @GetMapping("")
    public String listview(
            @RequestParam(value = "id", required = false, defaultValue = "0") String id,
            Model model) {
        Board eachBoard = boardLogicService.readBoardview(id);

        modelAdd(eachBoard, model,"Answer");
        modelAdd(answerLogicService.readUser(eachBoard), model,"eachboard");

        modelAdds(answerLogicService.readAnswer(id),model,"Answers");
        modelAdds(boardLogicService.bestweekview(getCategoryId(eachBoard)),model,"weekview");
        modelAdds(boardLogicService.bestmonthview(getCategoryId(eachBoard)),model,"monthview");


        return "pages/view";
    }
    @PostMapping("/answer")
    public String postAnswer(@Valid Answer answer, BindingResult bindingResult, Model model) {
        String id = String.valueOf(answer.getBoard().getId());
        if (bindingResult.hasErrors()) {
            modelAdd(answer.getBoard(),model,"eachboard");
            modelAdd(answer,model,"Answer");

            modelAdds(answerLogicService.readAnswer(id),model,"Answers");
            return "pages/view";
        }
        Answer answer1 = answerLogicService.save(answer);
        return "redirect:/board/view?id=" + answer1.getBoard().getId();
    }
    @GetMapping("/answer/delete")
    public String deleteAnswer(
            @RequestParam(value = "id", required = false, defaultValue = "0") String id,
            @RequestParam(value = "boardid", required = false, defaultValue = "0") String boardid) {
        answerLogicService.delete(id);
        return "redirect:/board/view?id=" + boardid;
    }

    private String getCategoryId(Board board){
        String categoryId = String.valueOf(board.getCategory().getId());
        return categoryId;
    }

    private void modelAdd(Object obj, Model model, String str){
        model.addAttribute(str,obj);
    }
    private void modelAdds(List<?> objs, Model model, String str){
        model.addAttribute(str,objs);
    }
}