package com.alten.hercules.dao.mission;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alten.hercules.model.mission.MissionSheet;
import com.alten.hercules.model.mission.MissionSheetId;

public interface MissionSheetDAO extends JpaRepository<MissionSheet, MissionSheetId>{}