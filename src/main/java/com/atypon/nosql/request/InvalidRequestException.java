package com.atypon.nosql.request;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class InvalidRequestException extends RuntimeException {
}
