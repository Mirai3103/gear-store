package com.ecom.exceptions;

import org.springframework.http.HttpStatus;

public class UnAuthorizedException extends BaseException {
	public UnAuthorizedException() {
		this("Bạn cần đăng nhập để thực hiện hành động này");
	}

	public UnAuthorizedException(String message) {
		super(message, HttpStatus.UNAUTHORIZED, "REQUIRE_LOGIN");
	}
}
