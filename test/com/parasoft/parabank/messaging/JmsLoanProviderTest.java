package com.parasoft.parabank.messaging;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.oxm.Marshaller;

import com.parasoft.parabank.domain.LoanRequest;
import com.parasoft.parabank.domain.LoanResponse;
import com.parasoft.parabank.test.util.AbstractParaBankDataSourceTest;

@SuppressWarnings("deprecation")
public class JmsLoanProviderTest extends AbstractParaBankDataSourceTest {
    private JmsLoanProvider jmsLoanProvider;
    private Marshaller marshaller;
    private JmsTemplate jmsTemplate;
    
    public void setJmsLoanProvider(JmsLoanProvider jmsLoanProvider) {
        this.jmsLoanProvider = jmsLoanProvider;
    }
    
    public void setMarshaller(Marshaller marshaller) {
        this.marshaller = marshaller;
    }
    
    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
                
        ConnectionFactory connectionFactory = 
            new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory);
        jmsLoanProvider.setJmsTemplate(jmsTemplate);
    }

    public void testRequestLoan() {
        jmsTemplate.send("queue.test.response", new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                LoanResponse loanResponse = new LoanResponse();
                loanResponse.setApproved(true);
                TextMessage message = session.createTextMessage();
                message.setText(MarshalUtil.marshal(marshaller, loanResponse));
                return message;
            }            
        });
        LoanResponse loanResponse = jmsLoanProvider.requestLoan(new LoanRequest());
        assertTrue(loanResponse.isApproved());
    }
    
    public void testRequestLoanException() {
        TextMessage message = new ActiveMQTextMessage() {
            @Override
            public String getText() throws JMSException {
                throw new JMSException(null);
            }
        };
        LoanResponse loanResponse = jmsLoanProvider.processResponse(message);
        assertNull(loanResponse);
    }
    
    public void testRequestLoanTimeout() {
        jmsTemplate.setReceiveTimeout(1);
        LoanResponse loanResponse = jmsLoanProvider.requestLoan(new LoanRequest());
        assertFalse(loanResponse.isApproved());
        assertNotNull(loanResponse.getResponseDate());
        assertEquals("error.timeout", loanResponse.getMessage());
    }
}
