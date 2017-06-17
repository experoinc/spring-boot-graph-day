/*
 * Copyright 2017 Expero, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.experoinc.repository;

import com.experoinc.domain.Gender;
import com.experoinc.domain.Person;
import com.experoinc.repository.ModelConstants.PERSON;
import com.experoinc.repository.ModelConstants.RELATIONS;
import com.google.common.collect.ImmutableMap;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Column;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Chris Pounds
 */
@Repository
public class PersonRepository {

    private final GraphTraversalSource g;
    private final Client client;

    @Autowired
    public PersonRepository(
            GraphTraversalSource g,
            @Autowired(required = false) Client client) {
        this.g = g;
        this.client = client;
    }

    public Person createPerson(Person person) {
        Map<String, Object> values = g.addV(PERSON.LABEL)
                .property(PERSON.BIRTH_DATE, person.getBirthDate().toEpochDay())
                .property(PERSON.FIRST_NAME, person.getFirstName())
                .property(PERSON.LAST_NAME, person.getLastName())
                .property(PERSON.GENDER, person.getGender().toString())
                .valueMap(true)
                .next();

        return toPerson(values);
    }

    public List<Person> findPersons(Optional<String> firstName) {

        GraphTraversal<?, ?> traversal = g.V().hasLabel(PERSON.LABEL);

        if(firstName.isPresent()) {
            traversal.has(PERSON.FIRST_NAME, firstName.get());
        }

        return traversal.valueMap(true)
                .toStream()
                .map(this::toPerson)
                .collect(Collectors.toList());
    }

    public List<Person> findPersonsByPrefix(String firstNamePrefix) {

        String query = String.format(
                "g.V().hasLabel('%s').has('%s', Text.textContainsPrefix('%s')).valueMap(true)",
                PERSON.LABEL,
                PERSON.FIRST_NAME,
                firstNamePrefix);

        try {
            List<Result> results = client.submit(query).all().get();

            List<Person> people = new ArrayList<>(results.size());
            for(Result result : results) {
                people.add(toPerson(result.get(Map.class)));
            }

            return people;
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void deleteAllPersons() {
        g.V().hasLabel(PERSON.LABEL).drop().iterate();
    }

    public void setParents(long personId, long fatherId, long motherId) {

        g.V(personId).hasLabel(PERSON.LABEL).as("person")
                .V(fatherId).hasLabel(PERSON.LABEL)
                .addE(RELATIONS.FATHER).to("person")
                .V(motherId).hasLabel(PERSON.LABEL)
                .addE(RELATIONS.MOTHER).to("person")
                .next();
    }

    public Optional<Person> findPerson(long personId) {
        return g.V(personId).hasLabel(PERSON.LABEL).valueMap(true)
                .tryNext()
                .map(this::toPerson);
    }

    @SuppressWarnings("unchecked")
    public Optional<Map<String, Person>> findParents(long personId) {
        return g.V(personId).hasLabel(PERSON.LABEL)
                .match(
                        __.as("p").in(RELATIONS.MOTHER).as("mother"),
                        __.as("p").in(RELATIONS.FATHER).as("father")
                )
                .select("mother", "father")
                .by(__.valueMap(true))
                .tryNext()
                .map(parents -> {
                    Map father = (Map) parents.get("father");
                    Map mother = (Map) parents.get("mother");

                    return ImmutableMap.of("mother", toPerson(mother), "father", toPerson(father));
                });
    }

    @SuppressWarnings("unchecked")
    public Optional<Map<String, Person>> findGrandParents(long personId) {
        return g.V(personId).hasLabel(PERSON.LABEL)
                .match(
                        __.as("p").in(RELATIONS.MOTHER).in(RELATIONS.MOTHER).as("mgm"),
                        __.as("p").in(RELATIONS.MOTHER).in(RELATIONS.FATHER).as("mgf"),
                        __.as("p").in(RELATIONS.FATHER).in(RELATIONS.MOTHER).as("pgm"),
                        __.as("p").in(RELATIONS.FATHER).in(RELATIONS.FATHER).as("pgf")
                )
                .select("mgm", "mgf", "pgm", "pgf")
                .by(__.valueMap(true))
                .tryNext()
                .map(gps -> {
                    Map maternalGm = (Map) gps.get("mgm");
                    Map maternalGf = (Map) gps.get("mgf");
                    Map paternalGm = (Map) gps.get("pgm");
                    Map paternalGf = (Map) gps.get("pgf");

                    return ImmutableMap.of(
                            "maternalGrandmother", toPerson(maternalGm),
                            "maternalGrandfather", toPerson(maternalGf),
                            "paternalGrandmother", toPerson(paternalGm),
                            "paternalGrandfather", toPerson(paternalGf));
                });
    }

    @SuppressWarnings("unchecked")
    public List<Person> findSiblings(long personId) {
        return g.V(personId).hasLabel(PERSON.LABEL)
                .union(
                        __.as("p").in(RELATIONS.MOTHER).as("m").out(RELATIONS.MOTHER),
                        __.as("p").in(RELATIONS.FATHER).as("f").out(RELATIONS.FATHER)
                )
                .groupCount()
                .unfold()
                .where(__.and(
                        __.select(Column.values).is(P.gt(1)),
                        __.select(Column.keys).id().is(P.neq(personId))))
                .select(Column.keys)
                .dedup()
                .valueMap(true)
                .toStream()
                .map(this::toPerson)
                .collect(Collectors.toList());
    }

    private Person toPerson(Map<String, Object> valueMap) {

        VertexValuesWrapper wrapper = VertexValuesWrapper.fromValueMap(valueMap);

        return Person.builder()
                .id(wrapper.getVertexId().orElse(0L))
                .birthDate(LocalDate.ofEpochDay((wrapper.getLongValue(PERSON.BIRTH_DATE))))
                .firstName(wrapper.getStringValue(PERSON.FIRST_NAME))
                .lastName(wrapper.getStringValue(PERSON.LAST_NAME))
                .gender(Gender.valueOf(wrapper.getStringValue(PERSON.GENDER)))
                .build();
    }
}
