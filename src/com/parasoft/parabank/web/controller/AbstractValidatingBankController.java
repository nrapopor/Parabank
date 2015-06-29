package com.parasoft.parabank.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.BindException;

/**
 * Abstract controller that validates user input
 */
abstract class AbstractValidatingBankController extends AbstractBankController {
    @Override
    protected abstract void onBindAndValidate(HttpServletRequest request,
            Object command, BindException errors) throws Exception;
}
