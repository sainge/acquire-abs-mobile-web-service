package gov.abs.mobiledev.resteasy;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "response")
public class ResponseDTO {
	String addressId;
	String q1;
	String q2;
	String q3;
	byte[] photo;
	//PersonDTO[] persons;
	
	@XmlElement
	public String getAddressId() {
		return addressId;
	}

	public void setAddressId(String addressId) {
		this.addressId = addressId;
	}

	@XmlElement
	public String getQ1() {
		return q1;
	}

	public void setQ1(String q1) {
		this.q1 = q1;
	}

	@XmlElement
	public String getQ2() {
		return q2;
	}

	public void setQ2(String q2) {
		this.q2 = q2;
	}

	@XmlElement
	public String getQ3() {
		return q3;
	}

	public void setQ3(String q3) {
		this.q3 = q3;
	}

	@XmlElement
	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}
	
	//public PersonDTO[] getPersons() {
	//	return persons;
	//}

	//public void setPerson(PersonDTO[] persons) {
	//	this.persons = persons;
	//}
	
}