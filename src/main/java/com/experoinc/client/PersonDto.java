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

package com.experoinc.client;

import com.experoinc.domain.Gender;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author Chris Pounds
 */
@Data
public class PersonDto {
    private long id;
    private LocalDate birthDate;
    private String firstName;
    private String lastName;
    private Gender gender;
}
