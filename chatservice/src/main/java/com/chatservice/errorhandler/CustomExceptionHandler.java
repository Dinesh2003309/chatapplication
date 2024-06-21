package com.chatservice.errorhandler;

import com.chatservice.payload.ErrorResponse;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import java.util.*;

@ControllerAdvice
public class CustomExceptionHandler {
     /**
      * Exception handler for MethodArgumentNotValidException class.
      * Handles validation errors by extracting the error message from the exception
      * and creating a response entity with the error message, status code, and success flag.
      *
      * @param exceptions The exception object that contains information about the validation errors.
      * @return A response entity object containing the error message, status code, and success flag.
      */
     @ExceptionHandler(MethodArgumentNotValidException.class)
     public ResponseEntity<?> handleValidation(MethodArgumentNotValidException exceptions) {
         String errorMessage = exceptions.getBindingResult().getAllErrors().stream()
                 .map(DefaultMessageSourceResolvable::getDefaultMessage)
                 .findFirst()
                 .orElse("Validation failed");
         Map<String, Object> errorResponse = new HashMap<>();
         errorResponse.put("message", errorMessage);
         errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
         errorResponse.put("success", false);
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
     }

    /**
     * Handles the exception when the uploaded file exceeds the maximum permitted size.
     * 
     * @param ex The MaxUploadSizeExceededException object that is thrown when the uploaded file exceeds the maximum permitted size.
     * @return A ResponseEntity object containing the error response with the status code set to HttpStatus.BAD_REQUEST, the error message set to "File exceeds its maximum permitted size, Kindly upload below 5Mb", and the timestamp set to the current date and time.
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "File exceeds its maximum permitted size, Kindly upload below 5Mb", new Date());
        return new ResponseEntity<>(errorResponse , HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles the validation of an HTTP message when there is an invalid data type provided for a field.
     * 
     * @param exceptions The exception object that contains information about the invalid data type.
     * @return A ResponseEntity object with an error message and a status code of 400 (Bad Request).
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleValidation(HttpMessageNotReadableException exceptions) {
        String message = "Invalid data type. Please provide a valid value for the field.";
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("Error", message);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception handler for the MultipartException class.
     * Handles the exception when there is an issue with the uploaded file, specifically when the file type is not selected.
     * 
     * @param ex The exception object that is thrown when there is an issue with the uploaded file.
     * @return A ResponseEntity object containing the error response with the status code set to HttpStatus.BAD_REQUEST, 
     *         the error message set to "Please select the file type", and the timestamp set to the current date and time.
     */
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ErrorResponse> handleFileException(MultipartException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Please select the file type", new Date());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


}

