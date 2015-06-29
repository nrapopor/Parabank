package com.parasoft.parabank.messaging;

import java.util.Date;

import javax.jms.JMSException;
import javax.jms.Message;
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
 * Message client for generating and sending loan requests over JMS
 */
public class JmsLoanProvider implements LoanProvider {
    private static final Log log = LogFactory.getLog(JmsLoanProvider.class);
    
    private JmsTemplate jmsTemplate;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;
    private String requestDesinationName;
    private String responseDestinationName;
    
    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }
    
    public void setMarshaller(Marshaller marshaller) {
        this.marshaller = marshaller;
    }
    
    public void setUnmarshaller(Unmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
    }
    
    public void setRequestDestinationName(String requestDesinationName) {
        this.requestDesinationName = requestDesinationName;
    }
    
    public void setResponseDestinationName(String responseDestinationName) {
        this.responseDestinationName = responseDestinationName;
    }
    
    public LoanResponse requestLoan(final LoanRequest loanRequest) {
        jmsTemplate.send(requestDesinationName, new MessageCreator() {            
            public Message createMessage(Session session) throws JMSException {
                String xml = MarshalUtil.marshal(marshaller, loanRequest);
                return session.createTextMessage(xml);
            }
        });
        Message message = jmsTemplate.receive(responseDestinationName);
        return processResponse(message);
    }
    
    LoanResponse processResponse(Message message) {
        if (message != null) {
            try {
                String text = ((TextMessage)message).getText();
                Object obj = MarshalUtil.unmarshal(unmarshaller, text);
                return (LoanResponse)obj;
            } catch (JMSException e) {
                log.error(e);
            }
        } else {
            log.error("Did not receive response message within timeout period of " + jmsTemplate.getReceiveTimeout() + " ms");
            LoanResponse response = new LoanResponse();
            response.setResponseDate(new Date());
            response.setApproved(false);
            response.setMessage("error.timeout");
            return response;
        }
        return null;
    }
}
