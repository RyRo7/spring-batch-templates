package com.ryro.springbatch.input.flatfilereader;

import com.ryro.springbatch.input.pojo.Customer;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

/**
 * @Who: Ryan Roberts
 * @When: 2019-04-17 5:20 PM
 */

public class FlatFileCustomerFieldSetMapper implements FieldSetMapper<Customer> {


    @Override
    public Customer mapFieldSet(FieldSet fieldSet) throws BindException {
        return new Customer(
            fieldSet.readLong("id"),
            fieldSet.readString("firstName"),
            fieldSet.readString("lastName"),
            fieldSet.readDate("birthdate", "yyyy-MM-dd")

                //readRawString allows values with padding to be catered for (i.e. "MyValue    ")
        );
    }
}
