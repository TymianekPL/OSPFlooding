package org.tymi.ospflooding.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Invalid parameters specified")
public class InvalidFormatDataException extends RuntimeException {
     public InvalidFormatDataException(String message, String paramater) {
          super("The parameter " + paramater + " is invalid. Reason: " + message);
     }
}
