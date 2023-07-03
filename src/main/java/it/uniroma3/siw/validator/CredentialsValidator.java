package it.uniroma3.siw.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.service.CredentialsService;

@Component
public class CredentialsValidator implements Validator{

	@Autowired
	private CredentialsService credentialsService;

	@Override
	public void validate(Object o, Errors errors) {
		Credentials credentials = (Credentials)o;
		if(this.credentialsService.usernameAlreadyInUse(credentials.getUsername())) {
			errors.reject("credentials.usernameAlreadyInUse");
		}
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return credentialsService.getClass().equals(aClass);
	}
}
