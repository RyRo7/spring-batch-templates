package com.ryro.springbatch.input.flatfilereader;

/**
 * @Who: Ryan Roberts
 * @When: 2019-04-16 3:44 PM
 */

public class FlatFileCustomer {
    private final long id;
    private final String firstName;
    private final String lastName;
    private final String birthdate;

    public FlatFileCustomer(long id, String firstName, String lastName, String birthdate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthdate = birthdate;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthdate=" + birthdate +
                '}';
    }
}
