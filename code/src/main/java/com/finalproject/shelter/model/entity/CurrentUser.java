package com.finalproject.shelter.model.entity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
//@PreAuthorize("HasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : account")
// 익명 사용자 일 경우 Authentication Principal이 anonymousUser 라는 스트링값이다.
public @interface CurrentUser {

}
