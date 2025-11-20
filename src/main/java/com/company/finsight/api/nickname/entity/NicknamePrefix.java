package com.company.finsight.api.nickname.entity;

import java.util.Random;

public enum NicknamePrefix {
	침착한,
	차분한,
	예리한,
	냉철한,
	영리한,
	현명한,
	신중한,
	안정적인,
	민첩한,
	날카로운,
	성장하는,
	상승하는,
	수익형,
	가치있는,
	전략적,
	스마트한,
	최적화된,
	균형잡힌,
	테크한,
	디지털,
	알고리즘,
	자동화된,
	혁신적,
	모던한,
	데이터형;

	public static String random() {
		return values()[
				new Random().nextInt(values().length)
			].name();
	}
}
