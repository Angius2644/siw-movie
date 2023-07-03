package it.uniroma3.siw.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.repository.ReviewRepository;
import jakarta.transaction.Transactional;

@Service
public class ReviewService {

	@Autowired
	private ReviewRepository reviewRepository;

    @Transactional
    public Review newReview(Review review) {
    	return this.reviewRepository.save(review);
    }

    @Transactional
    public Review getReviewFromMovieAndCredentials(Long Movie_id, Long Credentials_id) {
    	return this.reviewRepository.findByMovie_idAndCredentials_id(Movie_id, Credentials_id);
    }

    @Transactional
    public boolean isReviewFromMovieAndCredentials(Long Movie_id, Long Credentials_id) {
    	return this.reviewRepository.existsByMovie_idAndCredentials_id(Movie_id, Credentials_id);
    }

    @Transactional
    public Review getReview(Long review_id) {
    	return this.reviewRepository.findById(review_id).get();
    }

    @Transactional
    public boolean isReviewPresent(Long review_id) {
    	return review_id != null && this.reviewRepository.existsById(review_id);
    }

    @Transactional
    public void deleteReview(Long review_id) {

    	this.reviewRepository.deleteById(review_id);
    }

    @Transactional
    public void deleteAllReviews(List<Review> listToRemove) {

    	this.reviewRepository.deleteAll(listToRemove);
    }

}
