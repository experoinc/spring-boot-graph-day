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

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * @author Chris Pounds
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties("janusGraph")
public class JanusGraphProperties {

    private String hosts;

    private int port = 8182;

    private String type = "remote";

    public Set<String> getHosts() {
        return hosts == null ? null : ImmutableSet.copyOf(Splitter.on(",").split(hosts));
    }
}
