package com.perspecta.luegimport.business.common.delegate;

import com.perspecta.luegimport.business.common.exception.ServiceException;

import java.util.function.Supplier;

public class ErrorDelegate {

	public static ServiceException returnBadRequest(String message) {
		return new ServiceException(message);
	}


	public static <T> T badRequest(String message) {
		throw new ServiceException(message);
	}

	public static <T> T badRequest(String message, Throwable throwable) {
		throw new ServiceException(message, throwable);
	}

	public static Supplier<ServiceException> supplyBadRequest(String message) {
		return () -> ErrorDelegate.returnBadRequest(message);
	}

}
