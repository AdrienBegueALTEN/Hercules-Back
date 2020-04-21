package com.alten.hercules.dao.consultant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alten.hercules.model.consultant.Consultant;

@Repository
public interface ConsultantDAO extends JpaRepository<Consultant,Integer> {

	public boolean existsByEmail(String email);
	public Iterable<Consultant> findByReleaseDateIsNull();
	
}