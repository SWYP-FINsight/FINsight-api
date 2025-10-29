package com.company.finsight.global.exception.business.article;

import com.company.finsight.global.exception.business.BusinessException;

public class ArticleException extends BusinessException {

    public ArticleException(ArticleErrorCode errorCode) {
        super(errorCode);
    }
}