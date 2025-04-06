package com.ecom.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
	private final HttpStatus status;
	private final String errorCode;

	protected BaseException(String message, HttpStatus status, String errorCode) {
		super(message);
		this.status = status;
		this.errorCode = errorCode;
	}
}
