// Custom exceptions in package com.homelink.api.exception

package com.homelink.api.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
