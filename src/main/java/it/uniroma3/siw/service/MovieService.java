package it.uniroma3.siw.service;

import java.time.Year;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import jakarta.transaction.Transactional;

@Service
public class MovieService {

	@Autowired
	private MovieRepository movieRepository;

	@Autowired
	private ReviewRepository reviewRepository;

	@Transactional
	public Movie saveMovie(Movie movie) {
		return this.movieRepository.save(movie);
	}

	@Transactional
	public Movie getMovie(Long movie_id) {
		return this.movieRepository.findById(movie_id).get();
	}

	@Transactional
	public boolean isMoviePresent(Long movie_id) {
		return movie_id != null && this.movieRepository.existsById(movie_id);
	}

	@Transactional
	public List<Review> getMovieReviews(Long movie_id) {
		return this.reviewRepository.findByMovie_idOrderByIdDesc(movie_id);
	}

	@Transactional
	public Iterable<Movie> getMovies() {
		return this.movieRepository.findAll();
	}

	@Transactional
	public Iterable<Movie> getMoviesByYear(Year year) {
		return this.movieRepository.findByYear(year);
	}

	@Transactional
	public boolean isPresetByTitleAndYear(String title, Year year) {
		return this.movieRepository.existsByTitleAndYear(title, year);
	}

	@Transactional
	public void deleteMovieById(Long movie_id) {
		this.movieRepository.deleteById(movie_id);
	}
}
