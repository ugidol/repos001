package xxx;

import java.util.List;
import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.CollectionTable;
import javax.persistence.Entity;
import javax.persistence.ElementCollection;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

@Entity
@NamedQuery(name="findAllBooks", query="SELECT b FROM Book b")
public class Book {
	@Id
	@GeneratedValue
	private Long id;
	@Column(nullable=false)
	private String title;
	private Float price;
	@Column(length=2000)
	private String description;
	private String isbn;
	private Integer nbOfPage;
	private Boolean illustrations;
	@ElementCollection(fetch=FetchType.LAZY)
	@CollectionTable(name="Tag")
	@Column(name="Value")
	private List<String> tags = new ArrayList<String>();
	
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

	public Float getPrice() {
		return price;
	}
	public void setPrice(Float price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getIsbn() {
		return isbn;
	}
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public Integer getNbOfPage() {
		return nbOfPage;
	}
	public void setNbOfPage(Integer nbOfPage) {
		this.nbOfPage = nbOfPage;
	}

	public Boolean getIllustrations() {
		return illustrations;
	}
	public void setIllustrations(Boolean illustrations) {
		this.illustrations = illustrations;
	}

	public void addTag(String tag){
		this.tags.add(tag);
	}



}
