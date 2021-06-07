package com.finalproject.shelter.presentation.controller.view.board;

import com.finalproject.shelter.domain.model.entity.noticationDomain.Board;
import com.finalproject.shelter.business.service.logic.BoardLogicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/board")
public class BoardPageController {

    @Autowired
    private BoardLogicService boardLogicService;

    @GetMapping("")
    public String readboards(@RequestParam(value = "id") String id,
                             @PageableDefault(size = 10) Pageable pageable,
                             @RequestParam(value = "select", required = false, defaultValue = "") String select,
                             @RequestParam(value = "searchText", required = false, defaultValue = "") String searchText,
                             Model model
    ) {
        Page<Board> boards = boardLogicService.findCategoryIdTitleContents(id, select, searchText, pageable);
        model.addAttribute("boards", boards);

        modelAdd(boardLogicService.readCategory(id),model,"eachboard");
        modelAdd(Math.max(1, boards.getPageable().getPageNumber() - 4),model,"startPage");
        modelAdd(Math.min(boards.getTotalPages(), boards.getPageable().getPageNumber() + 4),model,"endPage");

        modelAdds(boardLogicService.bestweekview(id),model,"weekview");
        modelAdds(boardLogicService.bestmonthview(id),model,"mothview");

        return "pages/list";
    }

    @GetMapping("/delete")
    public String deleteboard(@RequestParam(value = "id") String ids) {
        boardLogicService.deleteid(ids);
        return "redirect:/main";
    }

    private void modelAdd(Object obj, Model model, String str){
        model.addAttribute(str,obj);
    }
    private void modelAdds(List<?> objs, Model model, String str){
        model.addAttribute(str,objs);
    }
}