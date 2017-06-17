package com.experoinc.repository;

import com.experoinc.domain.Person;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;

/**
 * @author Chris Pounds
 */
public abstract class AbstractPersonRepositoryTests {

    @Autowired PersonRepository repository;

    @Before
    public void setup() { }

    @Test
    public void testFindByName() {
        List<Person> people = repository.findPersons(Optional.of("Chris"));

        Assert.assertThat(people.size(), is(1));
    }
}
