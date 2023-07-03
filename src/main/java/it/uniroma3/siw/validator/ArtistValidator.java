package it.uniroma3.siw.validator;

import java.time.Year;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.service.ArtistService;

@Component
public class ArtistValidator implements Validator{

	@Autowired
	private ArtistService artistService;

	@Override
	public void validate(Object o, Errors errors) {
		Artist artist = (Artist)o;
		if (artist.getId() == null && artist.getName()!=null && artist.getSurname()!=null
				&& this.artistService.isPresetByNameAndSurname(artist.getName(), artist.getSurname())) {
			errors.reject("artist.duplicate");
		}
		if(artist.getDateOfDeath() != null && artist.getDateOfBirth().equals(artist.getDateOfDeath())) {
			errors.reject("artist.sameBirthDeath");
		}
		if(artist.getDateOfDeath() != null && artist.getDateOfBirth() != null && artist.getDateOfDeath().isBefore(artist.getDateOfBirth())) {
			errors.reject("artist.DeathBeforeBorn");
		}
		if(artist.getDateOfBirth() != null && Year.now().minusYears(artist.getDateOfBirth().getYear()).getValue() < 15) {
			errors.reject("artist.yearOfLifeLessThen15");
		}
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return Movie.class.equals(aClass);
	}
}
