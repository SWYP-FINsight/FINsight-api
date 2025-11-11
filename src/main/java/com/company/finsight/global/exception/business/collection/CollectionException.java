package com.company.finsight.global.exception.business.collection;

import com.company.finsight.global.exception.ErrorCode;
import com.company.finsight.global.exception.business.BusinessException;

public class CollectionException extends BusinessException {
	public CollectionException(ErrorCode errorCode) {
		super(errorCode);
	}
}
