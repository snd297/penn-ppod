package edu.upenn.cis.ppod.services;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public final class PPodExceptionMapper<E extends Throwable>
		implements ExceptionMapper<E> {

	public Response toResponse(E exception) {
		return Response
				.status(500)
				.entity(exception.getMessage()).build();
	}
}
