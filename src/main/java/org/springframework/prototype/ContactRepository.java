package org.springframework.prototype;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface ContactRepository extends JpaRepository<Contact, Long> {

	List<Contact> findByLastName(@Param("lastname") String lastname);
	
	List<Contact> findByFirstName(@Param("firstname") String firstname);
	
	List<Contact> findByFirstNameAndLastName(@Param("firstname") String firstName, @Param("lastname") String lastName);
	
	List<Contact> findByPostalCode(@Param("postalcode") String postalcode);

}
