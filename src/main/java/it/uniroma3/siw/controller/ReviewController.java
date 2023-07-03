package it.uniroma3.siw.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.MovieService;
import it.uniroma3.siw.service.ReviewService;
import it.uniroma3.siw.validator.ReviewValidator;
import jakarta.validation.Valid;

@Controller
public class ReviewController {

	@Autowired
	private ReviewService reviewService;

	@Autowired
	private MovieService movieService;

	@Autowired
	private CredentialsService credentialsService;

	@Autowired
	private ReviewValidator reviewValidator;

	@PostMapping("/newReview")
	public String newReview(@Valid @ModelAttribute Review review, BindingResult bindingResult, Model model, @RequestParam("movie_id") Long movie_id) {

		Credentials credentials = this.credentialsService.getCredentials(userDetails().getUsername());

		review.setMovie(this.movieService.getMovie(movie_id));
		review.setCredentials(credentials);
		review.setCreationDate(LocalDate.now());
		this.reviewValidator.validate(review, bindingResult);
		if(!bindingResult.hasErrors()) {
			this.reviewService.newReview(review);

			if(credentials.getRole().equals("ADMIN")) {
				return "redirect:/admin/movie/" + movie_id;
			}
			return "redirect:/movie/" + movie_id;
		}

		model.addAttribute("movie", this.movieService.getMovie(movie_id));
		model.addAttribute("role", credentials.getRole());
		return "addReviewToMovie.html";
	}

	@GetMapping("/addReviewToMovie/{movie_id}")
	public String addReviewToMovie(Model model, @PathVariable Long movie_id) {

		if(this.movieService.isMoviePresent(movie_id)) {
			model.addAttribute("role", this.credentialsService.getRole(userDetails().getUsername()));

			model.addAttribute("movie", this.movieService.getMovie(movie_id));
			model.addAttribute("review", new Review());

			return "addReviewToMovie.html";
		}
		return "redirect:/";
	}

	@GetMapping("/updateReview/{review_id}/{movie_id}")
	public String updateReviewForm(Model model, @PathVariable Long review_id, @PathVariable("movie_id") Long movie_id) {

		if(this.reviewService.isReviewPresent(review_id)) {
			model.addAttribute("role", this.credentialsService.getRole(userDetails().getUsername()));

			model.addAttribute("review", this.reviewService.getReview(review_id));
			model.addAttribute("movie", this.movieService.getMovie(movie_id));
			return "updateReviewToMovie.html";
		}
		return "redirect:/";
	}

	@PostMapping("/updateReview/{review_id}/{movie_id}")
	public String updateReview(@Valid @ModelAttribute Review review, BindingResult bindingResult, Model model, @PathVariable("review_id") Long review_id ,@PathVariable("movie_id") Long movie_id) {

		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Credentials credentials = this.credentialsService.getCredentials(userDetails.getUsername());
		
		if(this.reviewService.isReviewPresent(review_id)) {

			review.setId(review_id);
			review.setMovie(this.movieService.getMovie(movie_id));
			review.setCredentials(credentials);
			review.setCreationDate(LocalDate.now());

			this.reviewValidator.validate(review, bindingResult);

			if(!bindingResult.hasErrors()) {
				this.reviewService.newReview(review);

				if(credentials.getRole().equals("ADMIN")) {
					return "redirect:/admin/movie/" + movie_id;
				}
					return "redirect:/movie/" + movie_id;

			}
		}

		model.addAttribute("movie", this.movieService.getMovie(movie_id));
		model.addAttribute("role", credentials.getRole());
		return "updateReviewToMovie.html";
	}

	@GetMapping("/admin/deleteReview/{review_id}/{movie_id}")
	public String deleteReviewAdmin(@PathVariable("review_id") Long review_id, @PathVariable("movie_id") Long movie_id, Model model) {

		this.reviewService.deleteReview(review_id);

		return "redirect:/admin/movie/" + movie_id;
	}

	@GetMapping("/admin/deleteAllReviewsFromMovie/{movie_id}")
	public String deleteAllReviewsFromMovie(@PathVariable("movie_id") Long movie_id) {

		this.reviewService.deleteAllReviews(this.movieService.getMovieReviews(movie_id));

		return "redirect:/admin/movie/" + movie_id;
	}

	UserDetails userDetails() {
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return userDetails;
	}
}
