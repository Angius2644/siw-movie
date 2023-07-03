package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Review;

public interface ReviewRepository extends CrudRepository<Review, Long> {

	public boolean existsByMovie_idAndCredentials_id(Long movie_id, Long credentials_id);

	public List<Review> findByMovie_idOrderByIdDesc(Long movie_id);

	public Review findByMovie_idAndCredentials_id(Long movie_id, Long credentials_id);
}
