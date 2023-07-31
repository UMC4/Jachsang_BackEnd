package com.example.demo.src.mail;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.mail.model.PostMailReq;
import com.example.demo.src.user.UserProvider;
import com.example.demo.src.user.UserService;
import com.example.demo.utils.ValidationRegex;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("")
public class MailController {

    @Autowired
    private final MailService mailService;

    public MailController(MailService mailService){
        this.mailService=mailService;
    }

    @ResponseBody
    @PostMapping("/mailValidation")
    public BaseResponse<String> mailAuthentication (@RequestBody PostMailReq postMailreq) throws BaseException
    {

        if(postMailreq.getEmail() == null){
            return  new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_EMAIL);
        }

        if(!ValidationRegex.isRegexEmail(postMailreq.getEmail())){
            return  new BaseResponse<>(BaseResponseStatus.POST_USERS_INVALID_EMAIL);
        }

        try{

            String verifyCodeId = mailService.sendCertificationMail(postMailreq.getEmail());

            return new BaseResponse<>(verifyCodeId);
        }
        catch (BaseException baseException){
            return  new BaseResponse<>(baseException.getStatus());
        }

    }
}
