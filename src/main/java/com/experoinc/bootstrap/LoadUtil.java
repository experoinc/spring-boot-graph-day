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

package com.experoinc.bootstrap;

import com.experoinc.client.CreatePersonDto;
import com.experoinc.client.PersonDto;
import com.experoinc.client.SetParentsDto;
import com.experoinc.controller.PersonController;
import com.experoinc.domain.Gender;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * @author Chris Pounds
 */
@Component
public class LoadUtil {

    private final PersonController personController;

    @Autowired
    public LoadUtil(PersonController personController) {
        this.personController = personController;
    }

    public void clearPersons() {
        personController.deleteAllPersons();
    }

    public PersonDto createFemale(String firstName, String lastName, LocalDate birthDate) {
        return createPerson(firstName, lastName, birthDate, Gender.FEMALE);
    }

    public PersonDto createMale(String firstName, String lastName, LocalDate birthDate) {
        return createPerson(firstName, lastName, birthDate, Gender.MALE);
    }

    public PersonDto createSon(PersonDto father, PersonDto mother, String firstName) {
        return createChild(father, mother, firstName, Gender.MALE);
    }

    public PersonDto createDaughter(PersonDto father, PersonDto mother, String firstName) {
        return createChild(father, mother, firstName, Gender.FEMALE);
    }

    private PersonDto createPerson(String firstName, String lastName, LocalDate birthDate, Gender gender) {
        CreatePersonDto dto = basePerson(firstName, lastName, birthDate);
        dto.setGender(gender);

        return doCreateRequest(dto);
    }

    private PersonDto createChild(PersonDto father, PersonDto mother, String firstName, Gender gender) {
        CreatePersonDto dto = baseChild(father, mother, firstName);
        dto.setGender(gender);

        PersonDto child = doCreateRequest(dto);
        doSetParents(child, father, mother);

        return child;
    }

    private PersonDto doCreateRequest(CreatePersonDto dto) {
        return personController.createPerson(dto);
    }

    private void doSetParents(PersonDto child, PersonDto father, PersonDto mother) {
        SetParentsDto dto = new SetParentsDto();
        dto.setFatherId(father.getId());
        dto.setMotherId(mother.getId());

        personController.setParents(child.getId(), dto);
    }

    private CreatePersonDto basePerson(String firstName, String lastName, LocalDate birthDate) {
        CreatePersonDto dto = new CreatePersonDto();
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setBirthDate(birthDate);

        return dto;
    }

    private CreatePersonDto baseChild(PersonDto father, PersonDto mother, String firstName) {
        CreatePersonDto dto = new CreatePersonDto();
        dto.setFirstName(firstName);
        dto.setLastName(father.getLastName());
        dto.setBirthDate(childBirth(father.getBirthDate(), mother.getBirthDate()));

        return dto;
    }

    // kids will always be 18-35 years younger than youngest parent
    private LocalDate childBirth(LocalDate fatherBirthDate, LocalDate motherBirthDate) {
        LocalDate min = fatherBirthDate.isAfter(motherBirthDate) ? fatherBirthDate : motherBirthDate;

        return min.plusYears(RandomUtils.nextInt(18, 35))
                .plusMonths(RandomUtils.nextInt(0, 12))
                .plusDays(RandomUtils.nextInt(0, 28));
    }
}
