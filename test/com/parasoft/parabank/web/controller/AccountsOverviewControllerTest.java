package com.parasoft.parabank.web.controller;

import java.util.List;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.ModelAndView;

import com.parasoft.parabank.domain.Account;
import com.parasoft.parabank.domain.Customer;
import com.parasoft.parabank.web.UserSession;

@SuppressWarnings("unchecked")
public class AccountsOverviewControllerTest
extends AbstractBankControllerTest<AccountsOverviewController> {
    public void testHandleRequest() throws Exception {
        assertUserAccounts(12212, 11);
        assertUserAccounts(12323, 1);
        assertUserAccounts(3, 0);
    }
    
    private void assertUserAccounts(int id, int expectedSize) throws Exception {
        MockHttpSession session = new MockHttpSession();
        Customer customer = new Customer();
        customer.setId(id);
        session.setAttribute("userSession", new UserSession(customer));
        request.setSession(session);
        
        ModelAndView mav = controller.handleRequest(request, response);
        List<Account> accounts = (List<Account>)getModelValue(mav, "accounts");
        assertEquals(expectedSize, accounts.size());
    }
}
