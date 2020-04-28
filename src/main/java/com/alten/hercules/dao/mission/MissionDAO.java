package com.alten.hercules.dao.mission;

import org.springframework.data.jpa.repository.JpaRepository;
import com.alten.hercules.model.mission.Mission;

public interface MissionDAO extends JpaRepository<Mission, Long>{}