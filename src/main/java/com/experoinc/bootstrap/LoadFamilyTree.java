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

import com.experoinc.client.PersonDto;
import com.experoinc.config.BootstrapProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

/**
 * @author Chris Pounds
 */
@Slf4j
@Component
public class LoadFamilyTree {

    private final BootstrapProperties properties;
    private final GraphTraversalSource g;
    private final LoadUtil util;
    private final SchemaLoader schemaLoader;

    @Autowired
    public LoadFamilyTree(
            BootstrapProperties properties,
            GraphTraversalSource g,
            LoadUtil util,
            SchemaLoader schemaLoader) {

        this.properties = properties;
        this.g = g;
        this.util = util;
        this.schemaLoader = schemaLoader;
    }

    @PostConstruct
    private void onLoad() throws Exception
    {
        if(properties.getLoadSchema()) {
            schemaLoader.load();
        }
        if(!properties.getLoadData() || g.V().count().next() != 0) {
            return;
        }

        final LocalDate rootBirth = LocalDate.of(1850, 1, 1);

        try {
            util.clearPersons();
            // does
            PersonDto janeDoe = util.createFemale("Jane", "Doe", rootBirth);
            PersonDto johnDoe = util.createMale("John", "Doe", rootBirth);
            PersonDto joeDoe = util.createSon(johnDoe, janeDoe, "Joe");
            PersonDto jimDoe = util.createSon(johnDoe, janeDoe, "Jim");
            PersonDto joanneDoe = util.createDaughter(johnDoe, janeDoe, "Joanne");

            // smiths
            PersonDto alexaSmith = util.createFemale("Alexa", "Smith", rootBirth);
            PersonDto adamSmith = util.createMale("Adam", "Smith", rootBirth);
            PersonDto alexSmith = util.createSon(adamSmith, alexaSmith, "Alex");
            PersonDto abigaileSmith = util.createSon(adamSmith, alexaSmith, "Abigaile");
            PersonDto annSmith = util.createDaughter(adamSmith, alexaSmith, "Ann");

            // jones
            PersonDto claireJones = util.createFemale("Claire", "Jones", rootBirth);
            PersonDto caseyJones = util.createMale("Casey", "Jones", rootBirth);
            PersonDto clintJones = util.createSon(caseyJones, claireJones, "Clint");
            PersonDto carmenJones = util.createDaughter(caseyJones, claireJones, "Carmen");
            PersonDto chrisJones = util.createSon(caseyJones, claireJones, "Chris");

            // alex smith & joanne doe
            PersonDto amberSmith = util.createDaughter(alexSmith, joanneDoe, "Amber");
            PersonDto albertSmith = util.createSon(alexSmith, joanneDoe, "Albert");

            // clint jones & abigaileSmith
            PersonDto chadJones = util.createSon(clintJones, abigaileSmith, "Chad");
            PersonDto christinaJones = util.createDaughter(clintJones, abigaileSmith, "Christina");

            // jim doe & carmenJones
            PersonDto jackDoe = util.createSon(jimDoe, carmenJones, "Jack");
            PersonDto janetDoe = util.createDaughter(jimDoe, carmenJones, "Janet");

            // joe doe & ann smith
            PersonDto josephDoe = util.createSon(joeDoe, annSmith, "Joseph");
            PersonDto janessaDoe = util.createDaughter(joeDoe, annSmith, "Janessa");

            // chris jones & ann smith
            PersonDto calebJones = util.createSon(chrisJones, annSmith, "Caleb");
            PersonDto celineJones = util.createDaughter(chrisJones, annSmith, "Celine");
        }
        catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
