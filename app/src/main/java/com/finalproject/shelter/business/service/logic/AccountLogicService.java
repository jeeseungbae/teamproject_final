package com.finalproject.shelter.business.service.logic;

import com.finalproject.shelter.domain.model.entity.userDomain.Account;
import com.finalproject.shelter.domain.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountLogicService {

    @Autowired
    private AccountRepository accountRepository;

    public List<Account> findAll(){
        return accountRepository.findAll();
    }

    public Account save(Account account){
        return accountRepository.save(account);
    }

    public Account findById(Long id){
        return accountRepository.findById(id).orElseThrow(null);
    }

    public Account replace(Account account, Long id){
        return accountRepository.findById(id)
                .map(Account->accountRepository.save(Account))
                .orElseGet(()->{
                    account.setId(id);
                    return accountRepository.save(account);
                });
    }

    public void delete(Long id){
        accountRepository.deleteById(id);
    }

}
