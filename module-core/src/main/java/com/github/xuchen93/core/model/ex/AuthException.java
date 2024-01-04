package com.github.xuchen93.core.model.ex;

public class AuthException extends RuntimeException {
	int code;

	public AuthException(int code, String message) {
		super(message);
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
