package it.uniroma3.siw.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.repository.ArtistRepository;
import jakarta.transaction.Transactional;

@Service
public class ArtistService {

	@Autowired
	private ArtistRepository artistRepository;

	@Transactional
	public Artist saveArtist(Artist artist) {
		return this.artistRepository.save(artist);
	}

	@Transactional
	public Artist getArtist(Long artist_id) {
		return this.artistRepository.findById(artist_id).get();
	}

	@Transactional
	public Iterable<Artist> getArtists() {
		return this.artistRepository.findAll();
	}

	@Transactional
	public Iterable<Artist> findActorsNotInMovie(Long movie_id){
		return this.artistRepository.findActorsNotInMovie(movie_id);
	}

	@Transactional
	public List<Artist> getArtistBySurname(String surname) {
		return this.artistRepository.findActorsBySurname(surname);
	}

	@Transactional
	public void deleteArtist(Long Artist_id) {
		this.artistRepository.deleteById(Artist_id);
	}

	@Transactional
	public boolean isPresetByNameAndSurname(String name, String surname) {
		return this.artistRepository.existsByNameAndSurname(name, surname);
	}
	
	@Transactional
	public boolean isPresetByid(Long id) {
		return id != null && this.artistRepository.existsById(id);
	}
}
