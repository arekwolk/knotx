/*
 * Knot.x - Reactive microservice assembler
 *
 * Copyright (C) 2016 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.knotx.template.service;

import com.cognifide.knotx.template.TemplateEngineConfiguration;

import org.jsoup.nodes.Attribute;

import java.util.Map;

public class ServiceEntry {
    private String relatedAttribute;
    private String serviceUri;
    private TemplateEngineConfiguration.ServiceMetadata serviceMetadata;
    private Map<String, Object> serviceResult;

    private ServiceEntry() {
        //Hidden constructors
    }

    public static ServiceEntry of(Attribute serviceAttribute) {
        ServiceEntry entry = new ServiceEntry();
        entry.relatedAttribute = serviceAttribute.getKey();
        entry.serviceUri = serviceAttribute.getValue();
        return entry;
    }

    public ServiceEntry setResult(Map<String, Object> serviceResult) {
        this.serviceResult = serviceResult;
        return this;
    }

    public String getRelatedAttribute() {
        return relatedAttribute;
    }

    public String getServiceUri() {
        return serviceUri;
    }

    public Map<String, Object> getServiceResult() {
        return serviceResult;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceEntry that = (ServiceEntry) o;

        return serviceUri.equals(that.serviceUri);

    }

    @Override
    public int hashCode() {
        return serviceUri.hashCode();
    }

    public ServiceEntry setServiceMetadata(TemplateEngineConfiguration.ServiceMetadata serviceMetadata) {
        this.serviceMetadata = serviceMetadata;
        return this;
    }

    public String getDomain() {
        return serviceMetadata.getDomain();
    }

    public Integer getPort() {
        return serviceMetadata.getPort();
    }
}