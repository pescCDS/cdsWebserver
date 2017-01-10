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

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 4/27/16.
 */
@XmlRootElement(name = "Message")
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "topic")
    private String topic;

    @Column(name = "content")
    private String content;

    @Column(name="dismissed")
    private boolean dismissed;

    @Column(name="action_required")
    private boolean actionRequired;

    @Column(name="organization_id")
    private Integer organizationId;

    @Column(name="user_id")
    private Integer userId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isDismissed() {
        return dismissed;
    }

    public void setDismissed(boolean dismissed) {
        this.dismissed = dismissed;
    }

    public boolean isActionRequired() {
        return actionRequired;
    }

    public void setActionRequired(boolean actionRequired) {
        this.actionRequired = actionRequired;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
