package com.company.finsight.api.nickname.entity;

import java.util.Random;

public enum NicknameSuffix {
	분석가,
	투자자,
	설계자,
	운영자,
	관리자,
	리스크맨,
	전략가,
	계량가;

	public static String random() {
		return values()[
			new Random().nextInt(values().length)
			].name();
	}

}
