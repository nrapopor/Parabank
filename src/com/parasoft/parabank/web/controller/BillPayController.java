package com.parasoft.parabank.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.MessageSource;
import org.springframework.validation.BindException;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import com.parasoft.parabank.domain.Account;
import com.parasoft.parabank.domain.Customer;
import com.parasoft.parabank.domain.Payee;
import com.parasoft.parabank.web.UserSession;
import com.parasoft.parabank.web.form.BillPayForm;

/**
 * Controller for online bill pay
 */
@SuppressWarnings("deprecation")
public class BillPayController extends AbstractValidatingBankController {
    private MessageSource messageSource;
    
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
    
    @Override
    protected Object formBackingObject(HttpServletRequest request)
            throws Exception {
        return new BillPayForm();
    }
    
    @Override
    protected Map<String, Object> referenceData(HttpServletRequest request) throws Exception {       
        UserSession userSession = (UserSession)WebUtils.getRequiredSessionAttribute(request, "userSession");
        
        Customer customer = userSession.getCustomer();
        List<Account> accounts = bankManager.getAccountsForCustomer(customer);
        
        List<Integer> accountIds = new ArrayList<Integer>();
        for (Account account : accounts) {
            accountIds.add(account.getId());
        }
        
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("accounts", accountIds);
        
        return model;
    }
    
    @Override
    protected void onBindAndValidate(HttpServletRequest request,
            Object command, BindException errors) throws Exception {
        BillPayForm billPayForm = (BillPayForm)command;
        Payee payee = billPayForm.getPayee();
        
        try {
            errors.pushNestedPath("payee");
            getValidator().validate(payee, errors);
        } finally {
            errors.popNestedPath();
        }
        
        ValidationUtils.rejectIfEmpty(errors, "amount", "error.amount.empty");
        
        if (payee.getAccountNumber() != null && 
                !payee.getAccountNumber().equals(billPayForm.getVerifyAccount())) {
            errors.rejectValue("verifyAccount", "error.account.number.mismatch");
        }
    }
    
    @Override
    protected ModelAndView onSubmit(HttpServletRequest request,
            HttpServletResponse response, Object command, BindException errors)
            throws Exception {
        BillPayForm billPayForm = (BillPayForm)command;
        
        bankManager.withdraw(billPayForm.getFromAccountId(), 
                billPayForm.getAmount(), 
                messageSource.getMessage("bill.payment.to", 
                        new Object[] { billPayForm.getPayee().getName() }, 
                        request.getLocale()));
        
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("payeeName", billPayForm.getPayee().getName());
        model.put("amount", billPayForm.getAmount());
        model.put("fromAccountId", billPayForm.getFromAccountId());
        
        return new ModelAndView("billpayConfirm", "model", model);
    }
}
