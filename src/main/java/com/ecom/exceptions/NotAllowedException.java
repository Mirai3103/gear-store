package com.ecom.exceptions;

import org.springframework.http.HttpStatus;

public class NotAllowedException extends BaseException {
	public NotAllowedException(String message) {
		super(message, HttpStatus.FORBIDDEN, "FORBIDDEN");
	}
}
