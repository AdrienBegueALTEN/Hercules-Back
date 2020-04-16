package com.alten.hercules.dao.consultant;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.alten.hercules.model.consultant.Consultant;


public interface ConsultantDAO extends JpaRepository<Consultant,Integer> {
	//add consultant => save new
	
	//update consultant => save old
	
	//delete consultant => delete
	
	//get all => findAll
	
	//get by id
	public Consultant findById(int id);
	
	//get manager by id => findManagerIdById
	@Query(value="SELECT c.id_manager FROM Consultant c WHERE c.id=?1",nativeQuery = true)
	public int findManagerById(int id);
	
	//search by name and/or firstname => findByLastnameOrFisrstname
	@Query("SELECT c FROM Consultant c WHERE c.firstname LIKE %?1% OR c.lastname LIKE %?1% ")
	public List<Consultant> findByLastnameOrFirstname(String key);
	
	public Consultant findByEmail(String email);
	
	//TODO public List<Mission> getMissionsOfConsultant(int id);
}