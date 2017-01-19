package xxx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CustomerTest {
	private static EntityManagerFactory emf;
	private static EntityManager em;
	private static EntityTransaction tx;
	@BeforeClass
	public static void initEntityManager() throws Exception {
		emf = Persistence.createEntityManagerFactory("chapter04PUTest");
		em = emf.createEntityManager();
	}
	@AfterClass
	public static void closeEntityManager() throws SQLException {
		if ( em != null ){
			em.close();
		}
		if ( emf != null ) {
			emf.close();
		}
	}
	@Before 
	public void initTransaction(){
		tx = em.getTransaction();
	}

	@Test
	public void test_code_04_09() throws Exception {
		Customer customer = new Customer("Antony","Balla","tballa@mail.com");
		Address address = new Address("Ritherdon Rd", "London", "ROE", "UK");

		/*
		 * EntityManager.persit()を呼び出すことにより,
		 * customer,addressはエンティティマネージャの管理対象となる.
		 */
		tx.begin();
		em.persist(customer);
		em.persist(address);
		tx.commit();

		assertNotNull(customer.getId());
		assertNotNull(address.getId());
	}
	@Test
	public void test_code_04_10() throws Exception {
		Customer customer = new Customer("Antony","Balla","tballa@mail.com");
		//
		tx.begin();
		em.persist(customer);
		tx.commit();
		//
		Long id = customer.getId();
		Customer c = em.find(Customer.class,id);
		if ( c != null ) {
			// オブジェクトを処理する
		}
		else {
			fail();
		}
	}
	@Test
	public void test_code_04_11() throws Exception {
		Customer customer = new Customer("Antony","Balla","tballa@mail.com");
		//
		tx.begin();
		em.persist(customer);
		tx.commit();
		//
		try {
			Long id = customer.getId();
			Customer c = em.getReference(Customer.class,id);
		}
		catch(EntityNotFoundException e){
			// エンティティが見つからない
			fail();
		}
	}
	@Test
	public void test_code_04_12() throws Exception {
		Customer customer = new Customer("Antony","Balla","tballa@mail.com");
		Address address = new Address("Ritherdon Rd", "London", "ROE", "UK");
		customer.setAddress(address);
		//
		tx.begin();
		em.persist(customer);
		/*
		 * Customer.addressの外部キー制約に違反するため、
		 * ここでのEntityManager.flush()の呼出は例外(IllegalStateException)が発生する.
		 */
		//em.flush();
		em.persist(address);
		tx.commit();
		//
		tx.begin();
		em.remove(customer);
		tx.commit();

		// データベースからデータは削除されるが
		// オブジェクトにはアクセス可能
		assertNotNull(customer);
		assertNotNull(address);

		Customer c = em.find(Customer.class,customer.getId());
		assertNull(c);
		//
		Address a = em.find(Address.class,address.getId());
		/*
		 * Custmer.addressがorphanRemoval=trueの場合、
		 * custmerが削除されるとaddressも削除される.
		 */
		//assertNotNull(a);
		assertNull(a);
	}
	@Test
	public void test_code_04_14() throws Exception {
		Customer customer = new Customer("Antony","Balla","tballa@mail.com");
		//
		tx.begin();
		em.persist(customer);
		tx.commit();
		//

		Customer c = em.find(Customer.class,customer.getId());
		assertEquals("Antony",c.getFirstName());
		//
		c.setFirstName("William");
		assertEquals("William",c.getFirstName());
		/*
		 * EntityManager.refresh()を呼び出すことにより、
		 * c.firstNameはデータベースに格納された値("Antony")に戻る
		 */
		em.refresh(c);
		assertEquals("Antony",c.getFirstName());
	}
	@Test
	public void test_code_04_15() throws Exception {
		Customer customer = new Customer("Antony","Balla","tballa@mail.com");
		//
		tx.begin();
		em.persist(customer);
		tx.commit();
		//
		assertTrue(em.contains(customer));

		tx.begin();
		em.remove(customer);
		tx.commit();
		//
		/*
		 * EntityManager.remove()を呼び出したことにより、
		 * customerはデータベース及び永続性コンテキストから削除されたため、
		 * EntityManager.contains()はfalseを返す.
		 */
		assertFalse(em.contains(customer));
	}
	@Test
	public void test_code_04_16() throws Exception {
		Customer customer = new Customer("Antony","Balla","tballa@mail.com");
		//
		tx.begin();
		em.persist(customer);
		tx.commit();
		//
		assertTrue(em.contains(customer));

		/*
		 * EntityManager.detach()を呼び出すことにより、
		 * customerはエンティティマネージャの管理対象外となる.
		 */
		em.detach(customer);
		/*
		 * customerはエンティティマネージャの管理対象外のため、
		 * EntityManager.contains()はfalseを返す
		 */
		assertFalse(em.contains(customer));
	}
	@Test
	public void test_code_04_17() throws Exception {
		Customer customer = new Customer("Antony","Balla","tballa@mail.com");
		//
		tx.begin();
		em.persist(customer);
		tx.commit();

		/*
		 * EntityManager.clear()を呼び出すことにより,
		 * 全ての管理対象エンティティを分離し,永続性コンテキストを空にする.
		 */
		em.clear();

		/*
		 * EntityManager.clear()を呼び出したため,
		 * customerはデータベースと同期されない.
		 * つまり,customerのfirstNameを変更しても,データベースの値は変わらない.
		 */
		customer.setFirstName("William");
		Customer c = em.find(Customer.class,customer.getId());
		assertEquals("Antony",c.getFirstName());
		
		/*
		 * EntityManager.merge()を呼び出すことにより,
		 * customerは再びエンティティマネージャの管理対象となる.
		 */
		tx.begin();
		em.merge(customer);
		tx.commit();

		Customer c2 = em.find(Customer.class,customer.getId());
		assertEquals("William",c2.getFirstName());
	}
	@Test
	public void test_code_04_18() throws Exception {
		Customer customer = new Customer("Antony","Balla","tballa@mail.com");
		//
		tx.begin();
		em.persist(customer);

		/*
		 * EntityManager.persist()を呼び出したため,
		 * customerはエンティティマネージャの管理対象となっている.
		 */
		customer.setFirstName("William");

		tx.commit();

		Customer c = em.find(Customer.class,customer.getId());
		assertEquals("William",c.getFirstName());
	}
	@Test
	public void test_code_04_19() throws Exception {
		Customer customer = new Customer("Antony","Balla","tballa@mail.com");
		Address address = new Address("Ritherdon Rd", "London", "ROE", "UK");

		/*
		 * カスケードが有効になっていない場合,
		 * custer,addressそれぞれのオブジェクトを個別に永続化する必要がある.
		 */
		tx.begin();
		em.persist(customer);
		em.persist(address);
		tx.commit();

		/*
		 * カスケードが有効になっている場合,
		 * customerを永続化することで,addressも同時に永続化される.
		 */
//		tx.begin();
//		em.persist(customer);
//		tx.commit();
	}
	@Test
	public void test_code_04_24() throws Exception {
		Customer customer = new Customer("Antony","Balla","tballa@mail.com");

		tx.begin();
		em.persist(customer);
		tx.commit();

		/*
		 * EntityManagerFactory.getCache() を使用することで,
		 * キャッシュを取得する.
		 */
		Cache cache = emf.getCache();

		/*
		 * Customerがキャッシュ対象の場合,
		 * Cache.contains()はtrueを返す.
		 */
		assertTrue(cache.contains(Customer.class, customer.getId()));

		/*
		 * Cache.evict() を使用することで,
		 * 対象のクラスをキャッシュから削除する.
		 */
		cache.evict(Customer.class);

		/*
		 * Customerがキャッシュから削除された為,
		 * Cache.contains()はfalseを返す.
		 */
		assertFalse(cache.contains(Customer.class, customer.getId()));
	}
}
