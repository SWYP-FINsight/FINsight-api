package com.company.finsight.global;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

public class Const {

    public static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
            "실적", "공시", "증시", "금리", "주가", "상승", "하락",
            "매수", "매도", "외국인", "기관", "개인",
            "유상증자", "무상증자", "자사주", "배당", "M&A", "인수", "합병",
            "특허", "신약", "임상",
            "반도체", "AI", "배터리",
            "삼성전자", "SK하이닉스", "현대차"
    ));
}
