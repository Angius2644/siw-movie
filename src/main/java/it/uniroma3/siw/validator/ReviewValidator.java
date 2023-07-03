package it.uniroma3.siw.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.service.MovieService;
import it.uniroma3.siw.service.ReviewService;

@Component
public class ReviewValidator implements Validator {

	@Autowired
	private ReviewService reviewService;

	@Autowired
	private MovieService movieService;

	@Override
	public void validate(Object o, Errors errors) {
		Review review = (Review)o;
		if(review.getId() != null) {
			Review oldReview = this.reviewService.getReview(review.getId());

			if(review.getId() != null && oldReview != null
					&& oldReview.getTitle().equals(review.getTitle())
					&& oldReview.getStarRate().equals(review.getStarRate())
					&& oldReview.getDescription().equals(review.getDescription())) {
				errors.reject("review.equals");
			}
		}

		if(review.getId() == null && this.reviewService.isReviewFromMovieAndCredentials(review.getMovie().getId(), review.getCredentials().getId())) {
			errors.reject("review.duplicate");
		}

		if (review.getCredentials() == null || review.getMovie() == null) {
			errors.reject("review.invalidfk");
		}

		if(review.getMovie() != null && !this.movieService.isMoviePresent(review.getMovie().getId())) {
			errors.reject("review.movieNotValid");
		}

		if(review.getId() != null && !this.reviewService.isReviewPresent(review.getId())) {
			errors.reject("review.idNotValid");
		}

	}



	@Override
	public boolean supports(Class<?> aClass) {
		return Review.class.equals(aClass);
	}
}
