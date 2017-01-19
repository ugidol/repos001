package xxx;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Address {
	@Id
	@GeneratedValue
	private Long id;
	private String streetl;
	private String city;
	private String zipcode;
	private String country;

	public Address(){
	}
	
	public Address(String streetl, String city, String zipcode, String country){
		this.streetl = streetl;
		this.city = city;
		this.zipcode = zipcode;
		this.country = country;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getStreetl() {
		return streetl;
	}
	public void setStreetl(String streetl) {
		this.streetl = streetl;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
}
