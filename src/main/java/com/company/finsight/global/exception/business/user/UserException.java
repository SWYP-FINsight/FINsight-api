package com.company.finsight.global.exception.business.user;

import com.company.finsight.global.exception.ErrorCode;
import com.company.finsight.global.exception.business.BusinessException;

public class UserException extends BusinessException {
	public UserException(ErrorCode errorCode) {
		super(errorCode);
	}
}
