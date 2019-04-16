package com.ryro.springbatch.input.databaseReader;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Who: Ryan Roberts
 * @When: 2019-04-16 3:49 PM
 */

public class CustomerRowMapper implements RowMapper<Customer> {
    @Override
    public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Customer(
                rs.getLong("id"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getDate("birthdate")
                );
    }
}
