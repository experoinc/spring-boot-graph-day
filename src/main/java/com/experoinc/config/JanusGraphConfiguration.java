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

package com.experoinc.config;

import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.MessageSerializer;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.driver.ser.GryoMessageSerializerV1d0;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph;
import org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Chris Pounds
 */
@Configuration
@ConditionalOnProperty(name = "janusGraph.type", havingValue = "remote")
public class JanusGraphConfiguration {

    @Bean
    public MessageSerializer messageSerializer() {
        Map<String, Object> config = new HashMap<>();
        config.put("ioRegistries", Collections.singletonList(JanusGraphIoRegistry.class.getName()));
        GryoMessageSerializerV1d0 serializer = new GryoMessageSerializerV1d0();
        serializer.configure(config, null);

        return serializer;
    }

    @Bean
    public Cluster janusCluster(
            MessageSerializer messageSerializer,
            JanusGraphProperties properties) {

        Cluster.Builder clusterBuilder = Cluster.build()
                .port(properties.getPort())
                .serializer(messageSerializer);

        for (String contactPoint : properties.getHosts()) {
            clusterBuilder.addContactPoint(contactPoint);
        }

        return clusterBuilder.create();
    }

    @Bean
    public Client gremlinClient(Cluster cluster) {
        return cluster.connect();
    }

    @Bean(name = "remoteTraversalSource")
    public GraphTraversalSource traversalSource(Cluster cluster) {
        return EmptyGraph.instance()
                .traversal()
                .withRemote(DriverRemoteConnection.using(cluster));
    }
}
