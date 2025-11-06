package com.company.finsight.global.exception.business.ai;

import com.company.finsight.global.exception.business.BusinessException;

public class AiException extends BusinessException {

    public AiException(AiErrorCode errorCode) {
        super(errorCode);
    }
}