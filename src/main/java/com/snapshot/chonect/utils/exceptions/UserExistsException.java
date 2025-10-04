package com.snapshot.chonect.utils.exceptions;

public class UserExistsException 
    extends RuntimeException {

        public UserExistsException(String message) {
            super(message);
        }
}
