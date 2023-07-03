package it.uniroma3.siw.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.UserService;

@Component
public class UserValidator implements Validator{

	@Autowired
	private UserService userService;

	@Override
	public boolean supports(Class<?> aClass) {
		return userService.getClass().equals(aClass);
	}

	@Override
	public void validate(Object o, Errors errors) {
		User user = (User)o;

		if(this.userService.isEmailAlreadyInUse(user.getEmail())) {
			errors.reject("user.emailAlreadyInUse");
		}
		if(this.userService.isUserAlreadyRegistred(user.getName(), user.getSurname())) {
			errors.reject("user.duplicate");
		}
	}

}
