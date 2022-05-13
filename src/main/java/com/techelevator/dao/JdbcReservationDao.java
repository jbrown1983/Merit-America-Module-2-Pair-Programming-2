package com.techelevator.dao;

import com.techelevator.model.Reservation;
import com.techelevator.model.Site;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcReservationDao implements ReservationDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcReservationDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public int createReservation(int siteId, String name, LocalDate fromDate, LocalDate toDate) {
        String sqlCreateReservation =
                "INSERT INTO reservation (create_date, site_id, name, from_date, to_date) " +
                "VALUES ((SELECT now()), ?, ?, ?, ?) RETURNING reservation_id;";

        Integer reservationId = jdbcTemplate.queryForObject(sqlCreateReservation, Integer.class,
                siteId, name, fromDate, toDate);

        return reservationId;
    }

    @Override
    public List<Reservation> getUpcomingReservations(int parkId) {
        List<Reservation> reservations = new ArrayList<>();
// (SELECT now() + INTERVAL '30 day')
        String sqlGetUpcomingReservations =
                "SELECT *\n" +
                "FROM reservation\n" +
                "JOIN site USING (site_id)\n" +
                "JOIN campground USING (campground_id)\n" +
                "WHERE park_id = ?\n" + // WILDCARD
                "AND from_date BETWEEN (SELECT NOW())\n" +
                "AND (SELECT now() + INTERVAL '30 day')\n" +
                "ORDER BY from_date;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetUpcomingReservations, parkId);

        while (results.next()) {
            reservations.add(mapRowToReservation(results));
        }

        return reservations;
    }

    private Reservation mapRowToReservation(SqlRowSet results) {
        Reservation r = new Reservation();
        r.setReservationId(results.getInt("reservation_id"));
        r.setSiteId(results.getInt("site_id"));
        r.setName(results.getString("name"));
        r.setFromDate(results.getDate("from_date").toLocalDate());
        r.setToDate(results.getDate("to_date").toLocalDate());
        r.setCreateDate(results.getDate("create_date").toLocalDate());
        return r;
    }


}
