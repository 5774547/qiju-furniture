package com.qiju.furniture.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Result Code Enum
 *
 * @author Qiju Team
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "Success"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    ERROR(500, "Internal Server Error");

    private final int code;
    private final String msg;
}
