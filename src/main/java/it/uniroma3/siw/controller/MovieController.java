package it.uniroma3.siw.controller;

import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.MovieService;
import it.uniroma3.siw.service.ReviewService;
import it.uniroma3.siw.validator.MovieValidator;
import jakarta.validation.Valid;

@Controller
public class MovieController {

	@Autowired
	private MovieService movieService;

	@Autowired
	private ArtistService artistService;

	@Autowired
	private ReviewService reviewService;

	@Autowired
	private CredentialsService credentialsService;

	@Autowired
	private MovieValidator movieValidator;

	@GetMapping(value="/admin/formNewMovie")
	public String formNewMovie(Model model) {
		model.addAttribute("movie", new Movie());
		return "admin/formNewMovie.html";
	}

	@GetMapping(value="/admin/formUpdateMovie/{id}")
	public String formUpdateMovie(@PathVariable Long id, Model model) {
		if(this.movieService.isMoviePresent(id)) {
			model.addAttribute("movie", this.movieService.getMovie(id));
			return "admin/formUpdateMovie.html";
		}
		return "redirect:/admin/indexMovie";
	}

	@GetMapping(value="/admin/indexMovie")
	public String indexMovie() {
		return "admin/indexMovie.html";
	}

	@GetMapping(value="/admin/manageMovies")
	public String manageMovies(Model model) {
		model.addAttribute("movies", this.movieService.getMovies());
		return "admin/manageMovies.html";
	}

	@GetMapping(value="/admin/setDirectorToMovie/{directorId}/{movieId}")
	public String setDirectorToMovie(@PathVariable Long directorId, @PathVariable Long movieId, Model model) {

		if(this.movieService.isMoviePresent(movieId)) {
			Artist director = this.artistService.getArtist(directorId);
			Movie movie = this.movieService.getMovie(movieId);
			movie.setDirector(director);
			this.movieService.saveMovie(movie);

			return "redirect:/admin/formUpdateMovie/" + movieId;
		}
		return "redirect:/admin/indexMovie";
	}


	@GetMapping(value="/admin/addDirector/{id}")
	public String addDirector(@PathVariable Long id, Model model) {
		
		if(this.movieService.isMoviePresent(id)) {
			model.addAttribute("artists", this.artistService.getArtists());
			model.addAttribute("movie", this.movieService.getMovie(id));
			return "admin/directorsToAdd.html";
		}
		return "redirect:/admin/indexMovie";
	}

	@PostMapping("/admin/movie")
	public String newMovie(@Valid @ModelAttribute Movie movie, BindingResult bindingResult, Model model, @RequestParam("coverImageFile") MultipartFile coverImageFile) {

		movie.setTitle(StringUtils.capitalize(movie.getTitle()));

	    if (!coverImageFile.isEmpty()) {
	        try {
	            byte[] coverImageData = coverImageFile.getBytes();
	            movie.setCoverImage(coverImageData);
	        } catch (IOException e) {
	            // Qui posso gestire l'eccezione nel caso volessi
	            e.printStackTrace();
	        }
	    }

		this.movieValidator.validate(movie, bindingResult);

		if (!bindingResult.hasErrors()) {
			return "redirect:/admin/movie/" + this.movieService.saveMovie(movie).getId();
		}

		return "admin/formNewMovie.html";
	}

	@GetMapping("/movie/{id}")
	public String getMovie(@PathVariable Long id, Model model) {
		model.addAttribute("movie", this.movieService.getMovie(id));
		model.addAttribute("reviews", arrayElementToFirst(this.movieService.getMovieReviews(id), id));
		return "movie.html";
	}

	@GetMapping("admin/movie/{id}")
	public String getMovieAdmin(@PathVariable Long id, Model model) {

	    model.addAttribute("movie", this.movieService.getMovie(id));
		model.addAttribute("reviews", arrayElementToFirst(this.movieService.getMovieReviews(id), id));
		return "admin/adminMovie.html";
	}

	@GetMapping("/movie")
	public String getMovies(Model model) {
	    model.addAttribute("movies", this.movieService.getMovies());
		return "movies.html";
	}

	@GetMapping("admin/movie")
	public String getMoviesAdmin(Model model) {
	    model.addAttribute("movies", this.movieService.getMovies());
	    return "admin/adminMovies.html";
	}


	@GetMapping("/formSearchMovies")
	public String formSearchMovies() {
		return "formSearchMovies.html";
	}

	@PostMapping("/searchMovies")
	public String searchMovies(Model model, @RequestParam Year year, Authentication authentication) {
		
		if (!(year.isAfter(Year.of(1894)) && year.isBefore(Year.now().plusYears(1)))) {
			model.addAttribute("error", "Inserire un anno compreso tra 1895 e il corrente");
	        return "formSearchMovies.html";
	    }

		UserDetails userDetails = null;

	    if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) {
	        userDetails = (UserDetails) authentication.getPrincipal();
	    }

		model.addAttribute("movies", this.movieService.getMoviesByYear(year));

		if(userDetails != null && this.credentialsService.getCredentials(userDetails.getUsername()).getRole().equals("ADMIN")) {
			return "admin/foundMovies.html";
		}

		return "foundMovies.html";
	}

	@GetMapping("/admin/updateActors/{id}")
	public String updateActors(@PathVariable Long id, Model model) {

		if(this.movieService.isMoviePresent(id)) {
			List<Artist> actorsToAdd = this.actorsToAdd(id);
			model.addAttribute("actorsToAdd", actorsToAdd);
			model.addAttribute("movie", this.movieService.getMovie(id));

			return "admin/actorsToAdd.html";
		}
		return "redirect:/admin/indexMovie";
	}

	@GetMapping(value="/admin/addActorToMovie/{actorId}/{movieId}")
	public String addActorToMovie(@PathVariable Long actorId, @PathVariable Long movieId, Model model) {
		
		if(this.movieService.isMoviePresent(movieId)) {
			Movie movie = this.movieService.getMovie(movieId);
			Artist actor = this.artistService.getArtist(actorId);
			Set<Artist> actors = movie.getActors();
			actors.add(actor);
			this.movieService.saveMovie(movie);

			List<Artist> actorsToAdd = actorsToAdd(movieId);

			model.addAttribute("movie", movie);
			model.addAttribute("actorsToAdd", actorsToAdd);

			return "admin/actorsToAdd.html";
		}
		return "redirect:/admin/indexMovie";
	}

	@GetMapping(value="/admin/removeActorFromMovie/{actorId}/{movieId}")
	public String removeActorFromMovie(@PathVariable Long actorId, @PathVariable Long movieId, Model model) {
		
		if(this.movieService.isMoviePresent(movieId)) {
			Movie movie = this.movieService.getMovie(movieId);
			Artist actor = this.artistService.getArtist(actorId);
			Set<Artist> actors = movie.getActors();
			actors.remove(actor);
			this.movieService.saveMovie(movie);

			List<Artist> actorsToAdd = actorsToAdd(movieId);

			model.addAttribute("movie", movie);
			model.addAttribute("actorsToAdd", actorsToAdd);

			return "admin/actorsToAdd.html";
		}
		return "redirect:/admin/indexMovie";
		
	}

	@GetMapping(value = "/admin/deleteMovie/{movie_id}")
	public String deleteMovie(@PathVariable Long movie_id) {
		
		if(this.movieService.isMoviePresent(movie_id)) {
			Movie movieToDelete = this.movieService.getMovie(movie_id);
			Artist director = movieToDelete.getDirector();
			if(director != null) {
				movieToDelete.getDirector().getDirectorOf().remove(movieToDelete);
				this.artistService.saveArtist(movieToDelete.getDirector());
			}

			Set<Artist> movieArtists = movieToDelete.getActors();
			if(movieArtists != null && !movieArtists.isEmpty()) {
				for(Artist movieArtist: movieArtists) {
					movieArtist.getActorOf().remove(movieToDelete);
					this.artistService.saveArtist(movieArtist);
				}
			}
			this.movieService.deleteMovieById(movie_id);
		}

		return "redirect:/admin/indexMovie";
	}

	@GetMapping("/admin/updateMovie/{movie_id}")
	public String updateMovie(@PathVariable Long movie_id, Model model) {
		if(this.movieService.isMoviePresent(movie_id)) {
			model.addAttribute("movie", this.movieService.getMovie(movie_id));
			return "admin/updateMovie.html";
		}
		return "redirect:/admin/indexMovie";
	}

	@PostMapping("/admin/updateMovie/{movie_id}")
	public String updateArtist(@Valid @ModelAttribute Movie movie, BindingResult bindingResult, Model model, @PathVariable Long movie_id, @RequestParam("coverImageFile") MultipartFile coverImageFile) {

		if(this.movieService.isMoviePresent(movie_id)) {
			Movie oldMovie = this.movieService.getMovie(movie_id);

			movie.setId(movie_id);
			movie.setActors(oldMovie.getActors());
			movie.setDirector(oldMovie.getDirector());
			movie.setReviews(oldMovie.getReviews());

			movie.setTitle(StringUtils.capitalize(movie.getTitle()));

		    if (!coverImageFile.isEmpty()) {
		        try {
		            byte[] coverImageData = coverImageFile.getBytes();
		            movie.setCoverImage(coverImageData);
		        } catch (IOException e) {
		            // Qui posso gestire l'eccezione nel caso volessi
		            e.printStackTrace();
		        }
		    } else {
		    	movie.setCoverImage(oldMovie.getCoverImage());
		    }

			this.movieValidator.validate(movie, bindingResult);

			if (!bindingResult.hasErrors()) {
				this.movieService.saveMovie(movie);

				return "redirect:/admin/movie/" + movie_id;
			}
		}

		return "admin/updateMovie.html";
	}

	private List<Artist> actorsToAdd(Long movieId) {
		List<Artist> actorsToAdd = new ArrayList<>();

		for (Artist a : this.artistService.findActorsNotInMovie(movieId)) {
			actorsToAdd.add(a);
		}
		return actorsToAdd;
	}

	private List<Review> arrayElementToFirst(List<Review> list, Long movie_id) {

	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

	    if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) {
	        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

	        if (list != null && !list.isEmpty()) {
	            Review userReview = this.reviewService.getReviewFromMovieAndCredentials(movie_id,
	                    this.credentialsService.getCredentials(userDetails.getUsername()).getId());

	            int reviewIndex = list.indexOf(userReview);

	            if (reviewIndex != -1) {
	                Review review = list.get(reviewIndex);
	                list.remove(review);
	                list.add(0, review);
	            }
	        }
	    }

	    return list;
	}

}
