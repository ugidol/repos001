package xxx;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.sql.SQLException;
import java.util.List;

public class BookTest {
    private static EntityManagerFactory emf ;
    private static EntityManager em ;
    private static EntityTransaction tx ;

    @BeforeClass
    public static void initEntityManager() throws Exception {
        emf = Persistence.createEntityManagerFactory("chapter02PU");
        em =  emf.createEntityManager();
    }

    @AfterClass
    public static void closeEntityManager() throws SQLException {
        em.close();
        emf.close();
    }

    @Before
    public void initTransaction() {
        tx = em.getTransaction();
    }

    @Test
    public void shouldCreateABook() throws Exception {
        // Bookのインスタンスを生成する
        Book book = new Book();
        book.setTitle("title001");

        // データベースにBookのインスタンスを永続化する
        tx.begin();
        em.persist(book);
        tx.commit();

        assertNotNull("ID shoud not be null", book.getId());

        List<Book> books = 
            em.createNamedQuery("findAllBooks").getResultList();

        assertEquals(1, books.size());
    }
}
