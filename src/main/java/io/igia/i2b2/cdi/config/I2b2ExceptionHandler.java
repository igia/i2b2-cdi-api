/**
* This Source Code Form is subject to the terms of the Mozilla Public License, v.
* 2.0 with a Healthcare Disclaimer.
* A copy of the Mozilla Public License, v. 2.0 with the Healthcare Disclaimer can
* be found under the top level directory, named LICENSE.
* If a copy of the MPL was not distributed with this file, You can obtain one at
* http://mozilla.org/MPL/2.0/.
* If a copy of the Healthcare Disclaimer was not distributed with this file, You
* can obtain one at the project website https://github.com/igia.
*
* Copyright (C) 2021-2022 Persistent Systems, Inc.
*/






package io.igia.i2b2.cdi.config;

import java.net.URI;
import java.time.format.DateTimeParseException;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.spring.web.advice.security.SecurityAdviceTrait;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import io.igia.i2b2.cdi.common.exception.I2B2DataNotFoundException;
import io.igia.i2b2.cdi.common.exception.I2b2DataValidationException;
import io.igia.i2b2.cdi.common.exception.I2b2Exception;

@RestControllerAdvice
public class I2b2ExceptionHandler implements ProblemHandling, SecurityAdviceTrait {

    public static final String PROBLEM_BASE_URL = "https://igia.io/problem";

    @Override
    public ResponseEntity<Problem> handleMessageNotReadableException(final HttpMessageNotReadableException exception,
	    final NativeWebRequest request) {

	if (exception.getCause() != null && InvalidFormatException.class.isInstance(exception.getCause())
		&& exception.getCause().getCause() != null
		&& DateTimeParseException.class.isInstance(exception.getCause().getCause())) {
	    String value = "" + ((InvalidFormatException) exception.getCause()).getValue();
	    Problem problem = Problem.builder().withStatus(Status.BAD_REQUEST).withTitle("Invalid date format")
		    .withDetail("Date '" + value + "' should be in format 'yyyy-MM-ddTHH:mm:ss'").build();
	    return create(exception, problem, request);
	} else {
	    return create(Status.BAD_REQUEST, exception, request);
	}
    }

    @ExceptionHandler
    public ResponseEntity<Problem> handleDuplicateKeyException(DuplicateKeyException exception,
	    NativeWebRequest request) {
	Problem problem = Problem.builder().withType(URI.create(PROBLEM_BASE_URL + "/server-timeout"))
		.withTitle("Your request could not be processed at this time. Please try again.")
		.withStatus(Status.REQUEST_TIMEOUT).build();
	return create(exception, problem, request);
    }

    @ExceptionHandler
    public ResponseEntity<Problem> handleDataValidationException(I2b2DataValidationException exception,
	    NativeWebRequest request) {
	Problem problem = Problem.builder().withType(URI.create(PROBLEM_BASE_URL + "/invalid-parameter"))
		.withTitle("One or more request parameters are not valid").withStatus(Status.BAD_REQUEST)
		.withDetail(exception.getMessage()).build();
	return create(exception, problem, request);
    }

    @ExceptionHandler
    public ResponseEntity<Problem> handleI2b2Exception(I2b2Exception exception, NativeWebRequest request) {
	Problem problem = Problem.builder().withType(URI.create(PROBLEM_BASE_URL + "/server-error"))
		.withTitle("The server is not able to process the request").withStatus(Status.INTERNAL_SERVER_ERROR)
		.withDetail(exception.getMessage()).build();
	return create(exception, problem, request);
    }

    @ExceptionHandler
    public ResponseEntity<Problem> handleI2B2DataNotFoundException(I2B2DataNotFoundException exception,
	    NativeWebRequest request) {
	Problem problem = Problem.builder().withType(URI.create(PROBLEM_BASE_URL + "/data-not-found"))
		.withTitle("Data not found").withStatus(Status.NOT_FOUND).withDetail(exception.getMessage()).build();
	return create(exception, problem, request);
    }
}
