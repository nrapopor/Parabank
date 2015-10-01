package com.parasoft.bookstore2;

public class ItemNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public ItemNotFoundException(String msg) {
		super(msg);
	}
}
