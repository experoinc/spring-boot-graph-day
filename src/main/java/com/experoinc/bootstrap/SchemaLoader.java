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

import org.apache.commons.io.IOUtils;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author Chris Pounds
 */
@Component
class SchemaLoader {

    @Autowired(required = false)
    private Client client;

    @Value("classpath:schema/schema.groovy")
    private Resource schemaResource;

    public void load() throws Exception {

        if(client == null) {
            return;
        }

        List<Resource> resources = Arrays.asList(schemaResource);

        for(Resource resource : resources) {
            try(InputStream is = resource.getInputStream()) {
                String schema = IOUtils.toString(is);
                client.submit(schema).all().get();
            }
        }
    }
}
