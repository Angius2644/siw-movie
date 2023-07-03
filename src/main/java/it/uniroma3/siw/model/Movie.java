package it.uniroma3.siw.model;

import java.time.Year;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import it.uniroma3.siw.validator.ValidYear;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Movie {


		@Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        @NotBlank
        private String title;

        @ValidYear
        private Year year;

        private byte[] coverImage;

        @ManyToOne
        private Artist director;

        @ManyToMany
        private Set<Artist> actors;

        @OneToMany(mappedBy = "movie", cascade = {CascadeType.REMOVE})
        private List<Review> reviews;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }

        public Year getYear() {
            return year;
        }

        public void setYear(Year year) {
            this.year = year;
        }

        public Artist getDirector() {
            return director;
        }

        public void setDirector(Artist director) {
            this.director = director;
        }

        public Set<Artist> getActors() {
            return actors;
        }

        public void setActors(Set<Artist> actors) {
            this.actors = actors;
        }

        @Override
        public int hashCode() {
            return Objects.hash(title, year);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if ((obj == null) || (getClass() != obj.getClass()))
                return false;
            Movie other = (Movie) obj;
            return Objects.equals(title, other.title) && year.equals(other.year);
        }

		public List<Review> getReviews() {
			return reviews;
		}

		public void setReviews(List<Review> reviews) {
			this.reviews = reviews;
		}

		public byte[] getCoverImage() {
			return coverImage;
		}

		public void setCoverImage(byte[] coverImage) {
			this.coverImage = coverImage;
		}

	    public String getCoverImageBase64() {
	        if (coverImage != null && coverImage.length > 0) {
	            return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(coverImage);
	        }
	        return null;
	    }

    }
