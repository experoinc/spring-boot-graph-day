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

package com.experoinc.health;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

/**
 * @author Chris Pounds
 */
@Component
public class JanusGraphHealthIndicator extends AbstractHealthIndicator {

    private final GraphTraversalSource g;

    @Autowired
    public JanusGraphHealthIndicator(GraphTraversalSource g)
    {
        this.g = g;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder)
            throws Exception {

        if (hasConnection()) {
            builder.up();
        } else {
            builder.down();
        }
    }

    private boolean hasConnection() {
        return g.V().hasNext();
    }
}
