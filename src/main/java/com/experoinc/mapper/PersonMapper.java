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

package com.experoinc.mapper;

import com.experoinc.client.CreatePersonDto;
import com.experoinc.client.PersonDto;
import com.experoinc.domain.Person;

/**
 * @author Chris Pounds
 */
public final class PersonMapper {
    private PersonMapper() { }

    public static Person toPerson(CreatePersonDto dto) {
        return Person.builder()
                .birthDate(dto.getBirthDate())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .gender(dto.getGender())
                .build();
    }

    public static PersonDto toPersonDto(Person person) {
        PersonDto dto = new PersonDto();
        dto.setId(person.getId());
        dto.setBirthDate(person.getBirthDate());
        dto.setFirstName(person.getFirstName());
        dto.setLastName(person.getLastName());
        dto.setGender(person.getGender());

        return dto;
    }
}
