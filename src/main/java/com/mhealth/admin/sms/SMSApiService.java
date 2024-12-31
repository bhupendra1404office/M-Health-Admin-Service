package com.mhealth.admin.sms;


import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class SMSApiService {

    private final List<SendMessages> sortedProcessors;

    public SMSApiService(List<SendMessages> sortedProcessors) {
        this.sortedProcessors = sortedProcessors;
    }

    public SendMessages getMatchedProcessor(SMSAggregator smsAggregator) {
        for (SendMessages processor : sortedProcessors) {
            if(processor.supports(smsAggregator)) {
                return processor;
            }
        }
        return null;
    }

    public void sendMessage(String msisdn, String message, String country){
        SendMessages matchedProcessor = getMatchedProcessor(getSMSAggregator(country));
        matchedProcessor.sendSms(msisdn, message);
    }

    //Use this method if the contact number is of international doctor
    public void sendMessage(String msisdn, String message, SMSAggregator smsAggregator){
        SendMessages matchedProcessor = getMatchedProcessor(smsAggregator);
        matchedProcessor.sendSms(msisdn, message);
    }


    public SMSAggregator getSMSAggregator(String country) {
        HashMap<String, SMSAggregator> map = new HashMap<>();
        map.put("KE", SMSAggregator.SDP);
        map.put("SO", SMSAggregator.KANNEL);
        return map.get(country);
    }
}
