package xxx;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class Main {
	public static void main(String[] args) {
		//
		Book book = new Book();
		book.setId(1234L);
		book.setTitle("The Hitchhiker's Guide to the Galaxy");
		book.setPrice(12.5F);
		book.setDescription("Science fiction created by Douglas Adams.");
		book.setIsbn("1-84023-742-2");
		book.setNbOfPage(354);
		book.setIllustrations(false);

		//
		EntityManagerFactory emf = 
			Persistence.createEntityManagerFactory("chapter04PU");
		//
		EntityManager em = emf.createEntityManager();
		//
		EntityTransaction tx = em.getTransaction();

		//
		tx.begin();
		em.persist(book);
		tx.commit();

		//
		book = em.find(Book.class, 1234L);

		System.out.println(book);

		em.close();
		emf.close();
	}
}

