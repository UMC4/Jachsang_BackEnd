package com.example.demo.src.mail;
import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;
public interface MailServiceInter {

    // 메일 내용 작성
    MimeMessage creatMessage(String to) throws MessagingException, UnsupportedEncodingException;

    // 랜덤 인증코드 생성
    String createKey();

    // 메일 발송
    String sendSimpleMessage(String to) throws Exception;

}
