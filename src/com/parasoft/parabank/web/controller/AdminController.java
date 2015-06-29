package com.parasoft.parabank.web.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.servlet.ModelAndView;

import com.parasoft.parabank.domain.logic.AdminManager;
import com.parasoft.parabank.domain.logic.impl.ConfigurableLoanProvider;
import com.parasoft.parabank.web.form.AdminForm;

/**
 * Controller for modifying ParaBank parameters, servers, and behavior
 */
@SuppressWarnings("deprecation")
public class AdminController extends AbstractValidatingBankController {
	private AdminManager adminManager;
	private ConfigurableLoanProvider loanProvider;
	private ConfigurableLoanProvider loanProcessor;
	private static final Log log = LogFactory.getLog(DatabaseController.class);

	public void setAdminManager(AdminManager adminManager) {
		this.adminManager = adminManager;
	}

	public void setLoanProvider(ConfigurableLoanProvider loanProvider) {
		this.loanProvider = loanProvider;
	}

	public void setLoanProcessor(ConfigurableLoanProvider loanProcessor) {
		this.loanProcessor = loanProcessor;
	}

	@Override
	protected Map<String, Object> referenceData(HttpServletRequest request)
			throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();

		model.put("isJmsRunning", adminManager.isJmsListenerRunning());
		model.put("loanProviders", loanProvider.getProviderNames());
		model.put("loanProcessors", loanProcessor.getProviderNames());

		return model;
	}

	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		AdminForm form = new AdminForm();
		form.setParameters(adminManager.getParameters());
		log.info("form parameters = " + form.getParameters().entrySet());
		return form;
	}

	@Override
	protected void onBindAndValidate(HttpServletRequest request,
			Object command, BindException errors) throws Exception {
		ValidationUtils.rejectIfEmpty(errors, "initialBalance",
				"error.initial.balance.required");
		ValidationUtils.rejectIfEmpty(errors, "minimumBalance",
				"error.minimum.balance.required");
		ValidationUtils.rejectIfEmpty(errors, "loanProcessorThreshold",
				"error.loan.processor.threshold.required");
	}

	@Override
	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {
		AdminForm form = (AdminForm) command;
          
		ModelAndView modelAndView = super.showForm(request, response, errors);
		
		for (Entry<String, String> entry : form.getParameters().entrySet()) {
						adminManager.setParameter(entry.getKey(), entry.getValue());
				log.info("Using regular JDBC connection");
			}

		

		modelAndView.addObject("message", "settings.saved");

		return modelAndView;
	}
}
