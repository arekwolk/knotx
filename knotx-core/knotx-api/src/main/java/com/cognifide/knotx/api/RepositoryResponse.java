/*
 * Knot.x - Reactive microservice assembler - API
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
package com.cognifide.knotx.api;

import io.vertx.core.json.JsonObject;
import rx.Observable;

public class RepositoryResponse extends JsonObjectRequest {

    private static final long serialVersionUID = -5892594714118271978L;

    private boolean success;

    private String reason;

    private String data;

    private RepositoryResponse() {
        //Empty default constructor
    }

    public RepositoryResponse(JsonObject object) {
        success = object.getBoolean("success");

        if (success) {
            data = object.getString("data");
        } else {
            reason = object.getString("reason");
        }
    }

    public static RepositoryResponse success(String data) {
        RepositoryResponse response = new RepositoryResponse();
        response.success = true;
        response.data = data;
        return response;
    }

    public static RepositoryResponse error(String message, Object... args) {
        RepositoryResponse response = new RepositoryResponse();
        response.success = false;
        response.reason = String.format(message, args);
        return response;
    }

    public Observable<RepositoryResponse> toObservable() {
        return Observable.just(this);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getData() {
        return data;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObject object = new JsonObject().put("success", success);
        if (success) {
            object.put("data", data);
        } else {
            object.put("reason", reason);
        }
        return object;
    }

    @Override
    public String toString() {
        return "RepositoryResponse{" +
                "success=" + success +
                ", reason='" + reason + '\'' +
                ", data=" + data +
                '}';
    }

}