package com.ryro.springbatch.input.databaseReader;

import java.util.Date;

/**
 * @Who: Ryan Roberts
 * @When: 2019-04-16 3:44 PM
 */

public class Customer {
    private final long id;
    private final String firstName;
    private final String lastName;
    private final Date birthdate;

    public Customer(long id, String firstName, String lastName, Date birthdate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthdate = birthdate;
    }

    @Override
    public String toString() {
        return "Customer {" +
                "id="+id+
                ", firstname=\'"+firstName+ '\'' +
                ", lastname=\'"+lastName+ '\'' +
                ", birthdate=\'"+birthdate+ '\'' +
                '}';
    }
}
