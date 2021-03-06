/* Licensed under the Apache License, Version 2.0 (the "License");
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
package org.gnagoli.flowable.admin.client.common.service.exception;

/**
 * Exception thrown when an operation is performed for which the current user has insufficient permissions.
 * 
 * @author Frederik Heremans
 */
public class NotPermittedException extends BaseModelerRestException {

    private static final long serialVersionUID = 1L;

    public NotPermittedException() {
    }

    public NotPermittedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotPermittedException(String message) {
        super(message);
    }

    public NotPermittedException(Throwable cause) {
        super(cause);
    }
}
