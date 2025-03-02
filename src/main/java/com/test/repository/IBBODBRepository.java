package com.test.repository;

import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class IBBODBRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public IBBODBRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public String getProcessId() {
        String sql ="SELECT nextval('processId_Seq')";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        return namedParameterJdbcTemplate.queryForObject(sql, mapSqlParameterSource, String.class);
    }
}
