package com.example.demo.src.mail;
import com.example.demo.src.mail.model.PostMailReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.demo.src.mail.RegisterMail;
@RestController
@RequestMapping(value = "/api/mail")
public class MailServiceRestController {

    @Autowired
    RegisterMail registerMail;

    //127.0.0.1:8080/ROOT/api/mail/confirm.json?email
    @PostMapping("/mailValidation")
    public String mailConfirm(@RequestBody PostMailReq postMailReq) throws Exception{
        String code = registerMail.sendSimpleMessage(postMailReq.getEmail());
        System.out.println("사용자에게 발송한 인증코드 ==> " + code);

        return code;
    }

}