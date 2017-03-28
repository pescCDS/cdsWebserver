/*
 * Copyright (c) 2017. California Community Colleges Technology Center
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.pesc.api.model;

/**
 * Created by james on 3/27/17.
 */
public class UsageDashboardDTO {
    private Integer totalEndpointQueries;
    private Integer totalPublicKeyQueries;
    private Integer emptyResultEndpointQueries;
    private Integer emptyResultPublicKeyQueries;

    public Integer getTotalEndpointQueries() {
        return totalEndpointQueries;
    }

    public void setTotalEndpointQueries(Integer totalEndpointQueries) {
        this.totalEndpointQueries = totalEndpointQueries;
    }

    public Integer getTotalPublicKeyQueries() {
        return totalPublicKeyQueries;
    }

    public void setTotalPublicKeyQueries(Integer totalPublicKeyQueries) {
        this.totalPublicKeyQueries = totalPublicKeyQueries;
    }

    public Integer getEmptyResultEndpointQueries() {
        return emptyResultEndpointQueries;
    }

    public void setEmptyResultEndpointQueries(Integer emptyResultEndpointQueries) {
        this.emptyResultEndpointQueries = emptyResultEndpointQueries;
    }

    public Integer getEmptyResultPublicKeyQueries() {
        return emptyResultPublicKeyQueries;
    }

    public void setEmptyResultPublicKeyQueries(Integer emptyResultPublicKeyQueries) {
        this.emptyResultPublicKeyQueries = emptyResultPublicKeyQueries;
    }
}
