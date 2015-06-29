package com.parasoft.parabank.messaging;

import java.io.IOException;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;

import com.parasoft.parabank.domain.LoanRequest;
import com.parasoft.parabank.test.util.AbstractParaBankDataSourceTest;

public class MarhsalUtilTest extends AbstractParaBankDataSourceTest {
    private static final String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><loanRequest xmlns:ns2=\"http://service.parabank.parasoft.com/\"><customerId>0</customerId></loanRequest>";
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;
    
    public void setMarshaller(Marshaller marshaller) {
        this.marshaller = marshaller;
    }
    
    public void setUnmarshaller(Unmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
    }
    
    public void testMarshal() {
        String xml = MarshalUtil.marshal(marshaller, new LoanRequest());
        assertEquals(XML, xml);
        
        xml = MarshalUtil.marshal(new Marshaller() {
            public boolean supports(Class<?> clazz) {
                return true;
            }
            
            public void marshal(Object graph, Result result)
            throws IOException, XmlMappingException {
                throw new IOException();
            }
        }, new LoanRequest());
        assertEquals(0, xml.length());
    }
    
    public void testUnmarshal() {
        Object obj = MarshalUtil.unmarshal(unmarshaller, XML);
        assertTrue(obj instanceof LoanRequest);
        
        obj = MarshalUtil.unmarshal(new Unmarshaller() {
            public boolean supports(Class<?> clazz) {
                return true;
            }
            
            public Object unmarshal(Source source)
            throws IOException, XmlMappingException {
                throw new IOException();
            }
        }, XML);
        assertNull(obj);
    }
}
