package com.parasoft.parabank.messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;

import com.parasoft.parabank.domain.LoanRequest;
import com.parasoft.parabank.domain.LoanResponse;
import com.parasoft.parabank.domain.logic.LoanProvider;

/**
 * Message listener that delegates to a loan processor to handle incoming loan
 * requests over JMS
 */
public class JmsLoanProcessor implements MessageListener {
    private static final Log log = LogFactory.getLog(JmsLoanProcessor.class);
    
    private JmsTemplate jmsTemplate;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;
    private LoanProvider loanProcessor;
    private String destinationName;
    private String loanProviderName;
    
    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }
    
    public void setMarshaller(Marshaller marshaller) {
        this.marshaller = marshaller;
    }
    
    public void setUnmarshaller(Unmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
    }
    
    public void setLoanProcessor(LoanProvider loanProcessor) {
        this.loanProcessor = loanProcessor;
    }
    
    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }
    
    public void setLoanProviderName(String loanProviderName) {
        this.loanProviderName = loanProviderName;
    }
    
    public void onMessage(Message message) {
        try {
            String text = ((TextMessage)message).getText();
            Object obj = MarshalUtil.unmarshal(unmarshaller, text);
            final LoanRequest loanRequest = (LoanRequest)obj;
            
            jmsTemplate.send(destinationName, new MessageCreator() {
                public Message createMessage(Session session) throws JMSException {
                    return session.createTextMessage(getLoanResponseMessage(loanRequest));
                }
            });
        } catch (JMSException e) {
            log.error(e);
        }
    }
    
    private String getLoanResponseMessage(LoanRequest loanRequest) {
        LoanResponse response = loanProcessor.requestLoan(loanRequest);
        response.setLoanProviderName(loanProviderName);
        
        return MarshalUtil.marshal(marshaller, response);
    }
}
