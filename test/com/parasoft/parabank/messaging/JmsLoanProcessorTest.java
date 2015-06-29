package com.parasoft.parabank.messaging;

import java.io.StringReader;
import java.math.BigDecimal;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.transform.stream.StreamSource;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;

import com.parasoft.parabank.domain.LoanRequest;
import com.parasoft.parabank.domain.LoanResponse;
import com.parasoft.parabank.test.util.AbstractParaBankDataSourceTest;

@SuppressWarnings("deprecation")
public class JmsLoanProcessorTest extends AbstractParaBankDataSourceTest {
    private static final String TEST_PROVIDER = "Test Provider";
    
    private JmsLoanProcessor jmsLoanProcessor;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;
    private LoanRequest loanRequest;
    private JmsTemplate jmsTemplate;
    
    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        loanRequest = new LoanRequest();
        loanRequest.setAvailableFunds(new BigDecimal("1000.00"));
        loanRequest.setDownPayment(new BigDecimal("100.00"));
        loanRequest.setLoanAmount(new BigDecimal("5000.00"));
        
        ConnectionFactory connectionFactory = 
            new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory);
        jmsLoanProcessor.setJmsTemplate(jmsTemplate);
        jmsLoanProcessor.setLoanProviderName(TEST_PROVIDER);
    }
    
    public void setJmsLoanProcessor(JmsLoanProcessor jmsLoanProcessor) {
        this.jmsLoanProcessor = jmsLoanProcessor;
    }
    
    public void setMarshaller(Marshaller marshaller) {
        this.marshaller = marshaller;
    }
    
    public void setUnmarshaller(Unmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
    }
    
    public void testOnMessage() throws Exception {
        TextMessage message = new ActiveMQTextMessage();
        message.setText(MarshalUtil.marshal(marshaller, loanRequest));
        jmsLoanProcessor.onMessage(message);
        message = (TextMessage)jmsTemplate.receive("queue.test.response");
        Object obj = unmarshaller.unmarshal(new StreamSource(new StringReader(message.getText())));
        assertTrue(obj instanceof LoanResponse);
        LoanResponse response = (LoanResponse)obj;
        assertTrue(response.isApproved());
        assertNotNull(response.getResponseDate());
        assertEquals(TEST_PROVIDER, response.getLoanProviderName());
        
        message = new ActiveMQTextMessage() {
            public String getText() throws JMSException {
                throw new JMSException(null);
            }
        };
        jmsLoanProcessor.onMessage(message);
    }
}
