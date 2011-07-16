/*
 * Copyright 2011 Hippo
 *
 *   Licensed under the Apache License, Version 2.0 (the  "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS"
 *   BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.onehippo.forge.sforcecomps.client;

import net.sf.json.JSON;

public class SalesForceRecordQueryException extends SalesForceException {
    
    private static final long serialVersionUID = 1L;

    public SalesForceRecordQueryException() {
        super();
    }

    public SalesForceRecordQueryException(JSON errors) {
        super(errors);
    }

    public SalesForceRecordQueryException(String message) {
        super(message);
    }

    public SalesForceRecordQueryException(String message, JSON errors) {
        super(message, errors);
    }

    public SalesForceRecordQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public SalesForceRecordQueryException(String message, Throwable cause, JSON errors) {
        super(message, cause, errors);
    }

    public SalesForceRecordQueryException(Throwable cause) {
        super(cause);
    }

    public SalesForceRecordQueryException(Throwable cause, JSON errors) {
        super(cause, errors);
    }

}
