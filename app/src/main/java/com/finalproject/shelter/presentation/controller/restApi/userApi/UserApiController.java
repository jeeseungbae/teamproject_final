package com.finalproject.shelter.presentation.controller.restApi.userApi;

import com.finalproject.shelter.business.service.logic.AccountLogicService;
import com.finalproject.shelter.domain.model.entity.userDomain.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
class UserApiController {

    @Autowired
    private AccountLogicService accountLogicService;

    @GetMapping("/users")
    List<Account> all() {
        return accountLogicService.findAll();
    }

    @PostMapping("/Accounts")
    Account newAccount(@RequestBody Account account) {
        return accountLogicService.save(account);
    }

    @GetMapping("/Accounts/{id}")
    Account findById(@PathVariable Long id) {
        return accountLogicService.findById(id);
    }

    @PutMapping("/Accounts/{id}")
    Account replaceAccount(@RequestBody Account account, @PathVariable Long id) {
        return accountLogicService.replace(account,id);
    }

    @DeleteMapping("/user/{id}")
    void deleteUser(@PathVariable Long id) {
        accountLogicService.delete(id);
    }
}
