package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.service.MovieService;
import it.uniroma3.siw.validator.ArtistValidator;
import jakarta.validation.Valid;

@Controller
public class ArtistController {

	@Autowired
	private ArtistService artistService;

	@Autowired
	private MovieService movieService;

	@Autowired
	private ArtistValidator artistValidator;

	@GetMapping(value="/admin/formNewArtist")
	public String formNewArtist(Model model) {
		model.addAttribute("artist", new Artist());
		return "admin/formNewArtist.html";
	}

	@GetMapping(value="/admin/indexArtist")
	public String indexArtist() {
		return "admin/indexArtist.html";
	}

	@PostMapping("/admin/artist")
	public String newArtist(@Valid @ModelAttribute Artist artist, BindingResult bindingResult, Model model, @RequestParam("profileImageFile") MultipartFile profileImageFile) {

		artist = capitalizeArtist(artist);

		if (!profileImageFile.isEmpty()) {
	        try {
	            byte[] coverImageData = profileImageFile.getBytes();
	            artist.setImgOfPicture(coverImageData);
	        } catch (IOException e) {
	            // Qui posso gestire l'eccezione nel caso volessi
	            e.printStackTrace();
	        }
	    }

		this.artistValidator.validate(artist, bindingResult);

		if(!bindingResult.hasErrors()) {
			return "redirect:/artist/" + this.artistService.saveArtist(artist).getId();
		}
		return "admin/formNewArtist.html";
	}

	@GetMapping("/artist/{id}")
	public String getArtist(@PathVariable Long id, Model model) {
		model.addAttribute("artist", this.artistService.getArtist(id));
		model.addAttribute("directedMovies", this.artistService.getArtist(id).getDirectorOf());
		model.addAttribute("starredMovies", this.artistService.getArtist(id).getActorOf());
		return "artist.html";
	}

	@GetMapping("/artist")
	public String getArtists(Model model) {
		model.addAttribute("artists", this.artistService.getArtists());
		return "artists.html";
	}

	@PostMapping("/searchArtist")
	public String searchArtist(@RequestParam String surname, Model model) {
		surname = StringUtils.capitalize(surname);
		model.addAttribute("artists", this.artistService.getArtistBySurname(surname));
		return "foundArtists.html";
	}

	@GetMapping("/admin/manageArtists")
	public String manageArtists(Model model) {
		model.addAttribute("artists", this.artistService.getArtists());
		return "admin/manageArtists.html";
	}

	@GetMapping("/admin/deleteArtist/{Artist_id}")
	public String deleteArtist(@PathVariable Long Artist_id) {

		if(this.artistService.isPresetByid(Artist_id)) {
			List<Movie> directedList = this.artistService.getArtist(Artist_id).getDirectorOf();
			Set<Movie> actedList = this.artistService.getArtist(Artist_id).getActorOf();

			if(!directedList.isEmpty()) {
				for(Movie directedMovie: directedList) {
					directedMovie.setDirector(null);
					this.movieService.saveMovie(directedMovie);
				}
			}

			if(!actedList.isEmpty()) {
				for(Movie actedMovie: actedList) {
					actedMovie.getActors().remove(this.artistService.getArtist(Artist_id));
					this.movieService.saveMovie(actedMovie);
				}
			}

			this.artistService.deleteArtist(Artist_id);
		}
		
		return "redirect:/admin/indexArtist";
	}

	@GetMapping("/admin/formUpdateArtist/{Artist_id}")
	public String formUpdateArtist(@PathVariable Long Artist_id, Model model) {
		if(this.artistService.isPresetByid(Artist_id)) {
		model.addAttribute("artist", this.artistService.getArtist(Artist_id));
		model.addAttribute("directedMovies", this.artistService.getArtist(Artist_id).getDirectorOf());
		model.addAttribute("starredMovies", this.artistService.getArtist(Artist_id).getActorOf());
		return "admin/formUpdateArtist.html";
		}
		return "redirect:/admin/indexArtist";
	}

	@GetMapping("/admin/updateArtist/{Artist_id}")
	public String updateArtist(@PathVariable Long Artist_id, Model model) {

		if(this.artistService.isPresetByid(Artist_id)) {
		model.addAttribute("artist", this.artistService.getArtist(Artist_id));
		return "admin/updateArtist.html";
		}
		return "redirect:/admin/indexArtist";
	}

	@PostMapping("/admin/updateArtist/{artist_id}")
	public String updateArtist(@Valid @ModelAttribute Artist artist, BindingResult bindingResult, Model model, @PathVariable Long artist_id, @RequestParam("profileImageFile") MultipartFile profileImageFile) {

		if(this.artistService.isPresetByid(artist_id)) {
			artist.setId(artist_id);
			artist = capitalizeArtist(artist);

			if (!profileImageFile.isEmpty()) {
		        try {
		            byte[] coverImageData = profileImageFile.getBytes();
		            artist.setImgOfPicture(coverImageData);
		        } catch (IOException e) {
		            // Qui posso gestire l'eccezione nel caso volessi
		            e.printStackTrace();
		        }
		    } else {
				artist.setImgOfPicture(this.artistService.getArtist(artist_id).getImgOfPicture());
		    }


			this.artistValidator.validate(artist, bindingResult);

			if(!bindingResult.hasErrors()) {
				this.artistService.saveArtist(artist);

				return "redirect:/artist/" + artist_id;
			}
		}

		return "admin/updateArtist.html";
	}

	private Artist capitalizeArtist(Artist artist) {
		artist.setName(StringUtils.capitalize(artist.getName()));
		artist.setSurname(StringUtils.capitalize(artist.getSurname()));

		return artist;
	}
}
