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

package org.pesc.api.exception;

import org.pesc.api.model.ApiError;

import javax.ws.rs.core.Response;
import java.util.Calendar;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 6/14/16.
 */
public class ApiException extends RuntimeException {

    private ApiError apiError = new ApiError();

    private Response.Status status;

    public ApiException(Throwable cause, Response.Status status, String path) {
        super(cause);
        this.status = status;

        apiError.setTimestamp(Calendar.getInstance().getTimeInMillis());
        apiError.setStatus(status.getStatusCode());
        apiError.setError(status.getReasonPhrase());
        apiError.setException(cause.getClass().getCanonicalName());
        apiError.setMessage(cause.getMessage());
        apiError.setPath(path);
    }


    public ApiError getApiError() {
        return apiError;
    }

    public void setApiError(ApiError apiError) {
        this.apiError = apiError;
    }

    public Response.Status getStatus() {
        return status;
    }
}
