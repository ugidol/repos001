package xxx;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NamedNativeQuery;

@Entity
/*
 * @Cacheableアノテーションを使用することで,
 * パフォーマンスキャッシュ(レベル2キャッシュ)の対象となる.
 * キャッシュ方法はpersistence.xmlのshared-cache-mode属性で指定する.
 */
@Cacheable(true)
/*
 * 名前付きクエリ.
 */
@NamedQueries({
//	 @NamedQuery(name="findAll",query="select c from Customer c")
	/*
	 * 名前付きクエリはスコープ内でユニークでなければならない。
	 * また、文字列で定義した場合実行時例外が発生する可能性があるため,
	 * クエリ名を定数で定義する.
	 */
	 @NamedQuery(name=Customer.FIND_ALL,query="select c from Customer c")
	,@NamedQuery(name="findVincent",query="select c from Customer c where c.firstName='Vincent'")
	,@NamedQuery(name="findWithParam",query="select c from Customer c where c.firstName = :fname")
})
@NamedNativeQuery(name=Customer.FIND_ALL2,query="select * from Customer")
public class Customer {
	public static final String FIND_ALL = "Customer.findAll";
	public static final String FIND_ALL2 = "Customer.findAll2";
	@Id
	@GeneratedValue
	private Long id;
	private String firstName;
	private String lastName;
	private String email;

//	@OneToOne(fetch=FetchType.LAZY)
	/*
	 * orphanRemoval=trueの場合、
	 * addressが参照されなくなった場合にデータベースからも削除される.
	 */
	@OneToOne(fetch=FetchType.LAZY,orphanRemoval=true)
	/*
	 * cascade属性が設定されている場合,
	 * addressを永続化することでaddressも永続化される.
	 */
//	@OneToOne(
//		fetch=FetchType.LAZY,
//		orphanRemoval=true,
//		cascade={CascadeType.PERSIST,CascadeType.REMOVE})
	@JoinColumn(name="address_fk")
	private Address address;
	
	public Customer(){
	}
	
	public Customer(String firstName, String lastName, String email){
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
}
