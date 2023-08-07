package com.example.demo.src.mail;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.mail.model.PostMailReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.demo.src.mail.RegisterMail;
@RestController
@RequestMapping(value = "/app/mail")
public class MailServiceRestController {

    @Autowired
    RegisterMail registerMail;

    //127.0.0.1:8080/ROOT/api/mail/confirm.json?email
    @PostMapping("/Validation")
    public BaseResponse<String> mailConfirm(@RequestParam(name = "email") String email) throws Exception{
        try{
            String code = registerMail.sendSimpleMessage(email);
            System.out.println("사용자에게 발송한 인증코드 ==> " + code);

            return new BaseResponse<>(code);
        }catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
    }
}
}