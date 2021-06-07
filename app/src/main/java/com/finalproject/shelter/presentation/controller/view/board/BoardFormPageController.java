package com.finalproject.shelter.presentation.controller.view.board;

import com.finalproject.shelter.domain.model.entity.noticationDomain.Board;
import com.finalproject.shelter.domain.repository.AccountRepository;
import com.finalproject.shelter.business.service.logic.BoardLogicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;

@Controller
@RequestMapping("/board/form")
@Slf4j
public class BoardFormPageController {

    @Autowired
    private BoardLogicService boardLogicService;

    @GetMapping("/write")
    public String create(
            @RequestParam(value = "categoryid", defaultValue = "") String categoryid,
            Model model) {
        Board board = boardLogicService.readCategory(categoryid);
        model.addAttribute("eachboard", board);
        model.addAttribute("board", boardLogicService.newuserboard(board));
        return "pages/form";
    }
    @GetMapping("/modify")
    public String modify(
            @RequestParam(value = "boardid", defaultValue = "") String boardid,
            Model model) {
        model.addAttribute("eachboard", boardLogicService.readBoard(boardid));
        model.addAttribute("board", boardLogicService.readBoard(boardid));
        return "pages/form";
    }


    @PostMapping("")
    public String save(@Valid Board board, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("eachboard", board);
            model.addAttribute("board", board);
            return "pages/form";
        }
        return "redirect:/board/view?id=" + boardLogicService.postservice(board).getId();
    }
}
