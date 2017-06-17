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

package com.experoinc.controller;

import com.experoinc.client.CreatePersonDto;
import com.experoinc.client.PersonDto;
import com.experoinc.client.SetParentsDto;
import com.experoinc.domain.Person;
import com.experoinc.mapper.PersonMapper;
import com.experoinc.repository.PersonRepository;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Chris Pounds
 */
@RestController
@RequestMapping(
        value = "/persons",
        produces = { MediaType.APPLICATION_JSON_VALUE })
public class PersonController {

    private final PersonRepository repository;

    @Autowired
    PersonController(PersonRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(method = RequestMethod.POST)
    public PersonDto createPerson(@Validated @RequestBody CreatePersonDto dto) {
        Person person = repository.createPerson(PersonMapper.toPerson(dto));

        return PersonMapper.toPersonDto(person);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<PersonDto> findPersons(
            @RequestParam(value = "firstName", required = false)  String firstName,
            @RequestParam(value = "firstNamePrefix", required = false)  String firstNamePrefix)
    {

        List<Person> people;
        if(firstNamePrefix != null) {
            people = repository.findPersonsByPrefix(firstNamePrefix);
        }
        else {
            people = repository.findPersons(Optional.ofNullable(firstName));
        }

        return people.stream()
                .map(PersonMapper::toPersonDto)
                .collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteAllPersons() {
        repository.deleteAllPersons();
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{id:\\d+}")
    public PersonDto getPerson(@PathVariable("id") long personId) {
        return PersonMapper.toPersonDto(repository.findPerson(personId).get());
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/{id:\\d+}/parents")
    public Map<String, PersonDto> setParents(
            @PathVariable("id") long personId,
            @Validated @RequestBody SetParentsDto dto) {

        repository.setParents(personId, dto.getFatherId(), dto.getMotherId());

        return getParents(personId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{id:\\d+}/parents")
    public Map<String, PersonDto> getParents(@PathVariable("id") long personId) {

        return repository.findParents(personId)
                .map(m -> Maps.transformValues(m, PersonMapper::toPersonDto))
                .orElse(Collections.emptyMap());
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{id:\\d+}/grandparents")
    public Map<String, PersonDto> getGrandparents(@PathVariable("id") long personId) {

        return repository.findGrandParents(personId)
                .map(m -> Maps.transformValues(m, PersonMapper::toPersonDto))
                .orElse(Collections.emptyMap());
    }


    @RequestMapping(method = RequestMethod.GET, path = "/{id:\\d+}/siblings")
    public List<PersonDto> getSiblings(@PathVariable("id") long personId) {

        return repository.findSiblings(personId).stream()
                .map(PersonMapper::toPersonDto)
                .collect(Collectors.toList());
    }
}
