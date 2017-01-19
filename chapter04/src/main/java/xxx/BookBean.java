package xxx;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class BookBean {
	@PersistenceContext(unitName="chapter04PU")
	private EntityManager em;

	public void createBook(){
		//
		Book book = new Book();
		book.setId(1234L);
		book.setTitle("The Hitchhiker's Guide to the Galaxy");
		book.setPrice(12.5F);
		book.setDescription("Science fiction crated by Douglas Adams.");
		book.setIsbn("1-84023-742-2");
		book.setNbOfPage(354);
		book.setIllustrations(false);

		em.persist(book);

		book = em.find(Book.class, 1234L);

		System.out.println(book);
	}
}
