package com.mhealth.admin.sms;

import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Slf4j
@Service
public class TwilioSMSService implements SendMessages{

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String phoneNumber;

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }

    @Override
    public boolean supports(SMSAggregator smsAggregator) {
        return smsAggregator.equals(SMSAggregator.TWILIO);
    }

    @Override
    public void sendSms(String msisdn, String message) {
        sendTwilioSMS(msisdn, message);
    }

    public void sendTwilioSMS(String to, String msg) {
        Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(phoneNumber),
                msg
        ).create();

        log.info("Twilio sms sent: {}, msisdn: {}", msg, to);
    }
}
