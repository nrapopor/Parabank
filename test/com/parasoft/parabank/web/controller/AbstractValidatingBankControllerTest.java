package com.parasoft.parabank.web.controller;

import org.springframework.validation.BindException;

abstract class AbstractValidatingBankControllerTest<T extends AbstractValidatingBankController>
extends AbstractBankControllerTest<T> {
    protected final void assertError(Object form, String ... fields) throws Exception {
        BindException errors = new BindException(form, "");
        controller.onBindAndValidate(request, form, errors);
        assertEquals(fields.length, errors.getErrorCount());
        for (String field : fields) {
            assertNotNull("Did not get expected error for field: " + field, 
                    errors.getFieldError(field));
        }
    }
}
