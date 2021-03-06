package uk.ac.ebi.spot.goci.service;

/**
 * Created by cinzia on 15/12/2016.
 */

import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.StudyTrackingView;
import uk.ac.ebi.spot.goci.model.WeeklyTracking;
import uk.ac.ebi.spot.goci.repository.WeeklyTrackingRepository;

import java.sql.Date;
import java.util.*;

@Service
public class WeeklyTrackingService {
    private WeeklyTrackingRepository weeklyTrackingRepository;

    @Autowired
    public WeeklyTrackingService (WeeklyTrackingRepository weeklyTrackingRepository) {
        this.weeklyTrackingRepository = weeklyTrackingRepository;
    }


    public WeeklyTracking createWeeklyTracking(StudyTrackingView item, Study study,
                                               Date eventDate,  Calendar eventCalendarDate,
                                               String status) {
        WeeklyTracking weeklyTracking = new WeeklyTracking();
        weeklyTracking.setPubmedId(item.getPubmedId());
        weeklyTracking.setStudy(study);
        weeklyTracking.setEventDate(eventDate);
        // The week is from On Sunday to on Sat. JMorales.
        eventCalendarDate.add(Calendar.DATE, 1);
        weeklyTracking.setWeek(eventCalendarDate.get(Calendar.WEEK_OF_YEAR));
        weeklyTracking.setYear(eventCalendarDate.get(Calendar.YEAR));
        weeklyTracking.setStatus(status);

        return weeklyTracking;
    }


    public WeeklyTracking createAndSaveWeeklyTracking(StudyTrackingView item, Study study,
                                                      Date eventDate, Calendar eventCalendarDate,
                                                      String status) {
        WeeklyTracking newWeeklyTracking = createWeeklyTracking(item, study, eventDate, eventCalendarDate, status);
        weeklyTrackingRepository.save(newWeeklyTracking);

        return newWeeklyTracking;
    }

    public void save(WeeklyTracking weeklyTracking) {
        weeklyTrackingRepository.save(weeklyTracking);
    }

    public ArrayList<Object[]> getMinYearWeek() {return this.weeklyTrackingRepository.getMinYearWeek();}

    public List<Object> findAllWeekStatsByStatus() { return this.weeklyTrackingRepository.findAllWeekStatsByStatus();}

    public List<Object> findAllWeekStatsReport() { return this.weeklyTrackingRepository.findAllWeekStatsReport();}

    public List<WeeklyTracking> findByStatusAndYearAndWeek(String status, Integer year, Integer week) {
        return weeklyTrackingRepository.findByStatusAndYearAndWeek(status,year,week);
    }

    public HashSet<Long> findStudyByStatusAndYearAndWeek(String status, Integer year, Integer week) {
        return weeklyTrackingRepository.findStudyByStatusAndYearAndWeek(status,year,week);
    }

    public void deleteAll() { weeklyTrackingRepository.deleteAll();}

    public List<WeeklyTracking> findAll() { return weeklyTrackingRepository.findAll(); }

    public List<WeeklyTracking> find2016QueueLevel1() { return weeklyTrackingRepository.find2016QueueLevel1(); }

    public List<WeeklyTracking> find2016QueueLevel2() { return weeklyTrackingRepository.find2016QueueLevel2(); }


    public void deleteByStudy(Study study) {
        // Before we delete the study get its associated ancestry
        List<WeeklyTracking> weeklyTrackingAttachedToStudy = weeklyTrackingRepository.findByStudy(study);

        for (WeeklyTracking weeklyTracking : weeklyTrackingAttachedToStudy) {
            weeklyTrackingRepository.delete(weeklyTracking);
        }
    }


}
