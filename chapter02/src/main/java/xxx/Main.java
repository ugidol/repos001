package xxx;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 */
public class Main {
    public static void main( String[] args ) {
        //
        Book book = new Book();
        book.setTitle("Title001");
        book.setPrice(12.5F);
        book.setDescription("description...");
        book.setIsbn("x-xxxxx-xxx-x");
        book.setNbOfPage(0);
        book.setIllustrations(false);
        //
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("chapter02PU");
        EntityManager em = emf.createEntityManager();
        //
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(book);
        tx.commit();
        //
        em.close();
        emf.close();
    }
}
