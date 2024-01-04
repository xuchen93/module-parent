package com.github.xuchen93.model;

import lombok.Data;

@Data
public class R<T> {

	private boolean success;

	private int code;

	private String msg;

	private T data;


	public R() {

	}

	private R(boolean success, int code, String msg, T data) {
		this.success = success;
		this.code = code;
		this.msg = msg;
		this.data = data;
	}

	public static <T> R<T> success() {
		return success(null);
	}

	public static <T> R<T> success(T data) {
		return success(0, data);
	}

	public static <T> R<T> success(int code, T data) {
		return new R<>(true, code, "success", data);
	}

	public static <T> R<T> fail() {
		return fail(null);
	}

	public static <T> R<T> fail(String msg) {
		return fail(1, msg);
	}

	public static <T> R<T> fail(int code, String msg) {
		return new R<>(false, code, msg, null);
	}


}
