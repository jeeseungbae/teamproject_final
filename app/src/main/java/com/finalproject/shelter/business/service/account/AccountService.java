package com.finalproject.shelter.business.service.account;

import com.finalproject.shelter.presentation.config.AppProperties;
import com.finalproject.shelter.business.mail.EmailMessage;
import com.finalproject.shelter.business.mail.EmailService;
import com.finalproject.shelter.domain.model.entity.userDomain.Account;
import com.finalproject.shelter.domain.model.entity.userDomain.Role;
import com.finalproject.shelter.domain.model.entity.userDomain.UserAccount;
import com.finalproject.shelter.domain.repository.AccountRepository;
import com.finalproject.shelter.business.settings.form.password.PasswordForm;
import com.finalproject.shelter.business.settings.form.signup.SignUpForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
@Slf4j
public class AccountService implements UserDetailsService {

    @Autowired
    private final AccountRepository accountRepository;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final ModelMapper modelMapper;

    @Autowired
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Transactional
    public Account processNewAccount(@Valid SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        sendSignUpConfirmEmail(newAccount);
        return newAccount;
    }
    public Account saveNewAccount(@Valid SignUpForm signUpForm) {
        signUpForm.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
        Account account = modelMapper.map(signUpForm, Account.class);
        account.generateEmailCheckToken();
        account.setIdentity(account.getIdentity());
        account.setEmail(account.getEmail());
        account.setNickname("aaaa");
        account.setKakaoId(1L);
        account.setLoginFailCount(0);
        account.setCreatedAt(LocalDate.now());
        account.setUncreatedAt(LocalDate.now());
        account.setUpdatedAt(LocalDate.now());
        account.generateEmailCheckToken();
        account.setLastLoginAt(LocalDate.now());
        account.setUpdatedBy("");
        account.setEnabled(true);
        Role role = new Role();
        role.setId(1l);
        account.getRoles().add(role);
        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public void sendSignUpConfirmEmail(Account newAccount) {
        Context context = new Context();
        context.setVariable("link", "/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());
        context.setVariable("identity", newAccount.getIdentity());
        context.setVariable("linkName", "이메일 인증하기");
        context.setVariable("message", "스터디올래 서비스를 사용하려면 링크를 클릭하세요.");
        context.setVariable("host", appProperties.getHost());
        EmailMessage emailMessage = EmailMessage.builder()
                .to(newAccount.getEmail())
                .subject("스터디올래, 회원 가입 인증")
                .message("message")
                .build();
        emailService.sendEmail(emailMessage);
        newAccount.setEmailCheckTokenGeneratedAt(LocalDateTime.now());
    }
    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(token);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            throw new UsernameNotFoundException(username);
        }
        return new UserAccount(account);
    }
    public void completeSignUp(Account account) {
        account.completeSignUp();
        login(account);
    }
    public void updatePassword(Account account, PasswordForm passwordForm) {
        account.setPassword(passwordEncoder.encode(passwordForm.getNewPassword()));
        accountRepository.save(account);
    }
    public void deleteAccount(Account account) {
        account.setEnabled(false);
        account.setPassword("deletedaccount");
        accountRepository.save(account);
    }
    public void updateIdentity(Account account, String identity) {
        account.setIdentity(identity);
        accountRepository.save(account);
        login(account);
    }
}