package com.experoinc.repository;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author cpounds.ctr
 */
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:config/integration.properties")
@ContextConfiguration(locations = "classpath:context/integration-context.xml")
public class PersonRepositoryIntegrationTests extends AbstractPersonRepositoryTests {
}
