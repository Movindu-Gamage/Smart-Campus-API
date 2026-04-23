package com.smartcampus.exception.mapper;

import com.smartcampus.model.ErrorMessage;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable exception) {

        // CRITICAL: Let Jersey handle its own internal exceptions
        // Without this, Jersey cannot route any requests and everything returns 500
        if (exception instanceof WebApplicationException) {
            return ((WebApplicationException) exception).getResponse();
        }

        // Only catch genuine unexpected errors (NullPointerException, etc.)
        LOGGER.severe("Unexpected error: " + exception.getMessage());

        ErrorMessage error = new ErrorMessage(
                "An unexpected internal error occurred. Please try again later.",
                500,
                "https://smartcampus.ac.uk/api/docs/errors"
        );
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
