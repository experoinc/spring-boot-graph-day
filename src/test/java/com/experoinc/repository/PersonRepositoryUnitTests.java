package com.experoinc.repository;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author cpounds.ctr
 */
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:config/unit.properties")
@ContextConfiguration(locations = "classpath:context/unit-context.xml")
public class PersonRepositoryUnitTests extends AbstractPersonRepositoryTests {
}
