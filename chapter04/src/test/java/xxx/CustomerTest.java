package xxx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.List;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

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
	/**
	 * 動的なクエリは毎回JPQLの解析が発生する.
	 */
	@Test
	public void test_code_query() throws Exception {
		{
			tx.begin();
			Query query = em.createQuery("DELETE FROM Customer c");
			query.executeUpdate();
			Customer customer = new Customer("Vincent","Balla","tballa@mail.com");
			em.persist(customer);
			tx.commit();
		}
		{
			/*
			 * 全てのCustomerを取得する(1) 
			 * EntityManager.createQuery(String strQuery)
			 */
			Query query = em.createQuery("SELECT c FROM Customer c");
			List<?> customers = query.getResultList();
			assertEquals(1,customers.size());
		}
		{
			/*
			 * 全てのCustomerを取得する(2) 
			 * EntityManager.createQuery(String strQuery,Class resultClass)
			 */
			TypedQuery<Customer> query = em.createQuery("SELECT c FROM Customer c", Customer.class);
			List<Customer> customers = query.getResultList();
			assertEquals(1,customers.size());
		}
		{
			/*
			 * 動的なクエリ(1)
			 */
			String jpqlQuery = "SELECT c FROM Customer c";
			jpqlQuery += " WHERE c.firstName = 'Vincent'";
			Query query = em.createQuery(jpqlQuery);
			List<?> customers = query.getResultList();
		}
		{
			/*
			 * 動的なクエリ(2)
			 * パラメータ使用(1)
			 */
			String jpqlQuery = "SELECT c FROM Customer c";
			jpqlQuery += " WHERE c.firstName = :fname";
			Query query = em.createQuery(jpqlQuery);
			query.setParameter("fname","Vincent");
			List<?> customers = query.getResultList();
		}
		{
			/*
			 * 動的なクエリ(3)
			 * パラメータ使用(2)
			 */
			String jpqlQuery = "SELECT c FROM Customer c";
			jpqlQuery += " WHERE c.firstName = ?1";
			Query query = em.createQuery(jpqlQuery);
			query.setParameter(1,"Vincent");
			List<?> customers = query.getResultList();
		}
		{
			/*
			 * 動的なクエリ(4)
			 * ページ制御
			 */
			TypedQuery<Customer> query = em.createQuery("SELECT c FROM Customer c", Customer.class);
			query.setMaxResults(10);
			List<Customer> customers = query.getResultList();
		}
	}

	@Test
	public void test_code_static_query() throws Exception {
		{
			tx.begin();
			Query query = em.createQuery("DELETE FROM Customer c");
			query.executeUpdate();
			Customer customer = new Customer("Vincent","Balla","tballa@mail.com");
			em.persist(customer);
			tx.commit();
		}
		{
			/*
			 * 名前付きクエリ(1)
			 */
			Query query = em.createNamedQuery(Customer.FIND_ALL);
			List<?> customers = query.getResultList();
			assertEquals(1,customers.size());
		}
		{
			/*
			 * 名前付きクエリ(1-1)
			 * EntityManager.createNamedQuery()に誤ったクエリ名を渡すと,
			 * IllegalArgumentExceptionが発生する.
			 */
			try{
				Query query = em.createNamedQuery("not_defined_name");
				List<?> customers = query.getResultList();
				assertEquals(1,customers.size());
			}
			catch(Exception e){
				assertTrue((e instanceof IllegalArgumentException));
			}
		}
		{
			/*
			 * 名前付きクエリ(2)
			 */
			TypedQuery<Customer> query = em.createNamedQuery(Customer.FIND_ALL, Customer.class);
			List<Customer> customers = query.getResultList();
			assertEquals(1,customers.size());
		}
		{
			/*
			 * 名前付きクエリ(3)
			 * パラメタ使用
			 */
			Query query = em.createNamedQuery("findWithParam");
			query.setParameter("fname","Vincent");
			query.setMaxResults(3);
			List<?> customers = query.getResultList();
			assertEquals(1,customers.size());
		}
	}
	@Test
	public void test_code_native_query() throws Exception {
		{
			tx.begin();
			Query query = em.createQuery("DELETE FROM Customer c");
			query.executeUpdate();
			Customer customer = new Customer("Vincent","Balla","tballa@mail.com");
			em.persist(customer);
			tx.commit();
		}
		{
			/*
			 * ネイティブクエリ(動的)
			 */
			Query query = em.createNativeQuery("SELECT * FROM customer",Customer.class);
			List<?> customers = query.getResultList();
			assertEquals(1,customers.size());
		}
	}
	@Test
	public void test_code_native_static_query() throws Exception {
		{
			tx.begin();
			Query query = em.createQuery("DELETE FROM Customer c");
			query.executeUpdate();
			Customer customer = new Customer("Vincent","Balla","tballa@mail.com");
			em.persist(customer);
			tx.commit();
		}
		{
			/*
			 * ネイティブクエリ(名前付き)
			 */
			Query query = em.createNamedQuery(Customer.FIND_ALL2,Customer.class);
			List<?> customers = query.getResultList();
			assertEquals(1,customers.size());
		}
	}
	@Test
	public void test_code_criteria_query() throws Exception {
		/*
		 * CriteriaAPIを使用した問い合わせ.
		 * (JPA 2.0より)
		 */
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Customer> criteriaQuery = builder.createQuery(Customer.class);
		Root<Customer> c = criteriaQuery.from(Customer.class);
		criteriaQuery.select(c).where(builder.equal(c.get("firstName"),"Vincent"));
		//
		Query query = em.createQuery(criteriaQuery);
		List<?> customers = query.getResultList();
		assertEquals(1,customers.size());
	}


}
