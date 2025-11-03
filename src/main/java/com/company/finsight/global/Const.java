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

    public static final Map<String, String> YNA_INDUSTRIAL_URL_MAP = Map.ofEntries(
            Map.entry("산업/기업", "/industry/industrial-enterprise"),
            Map.entry("전자", "/industry/electronics"),
            Map.entry("중화학", "/industry/heavy-chemistry"),
            Map.entry("자동차", "/industry/automobile"),
            Map.entry("건설/부동산", "/industry/construction"),
            Map.entry("에너지/자원", "/industry/energy-resource"),
            Map.entry("IT/과학", "/industry/technology-science"),
            Map.entry("게임", "/industry/game"),
            Map.entry("유통/서비스", "/industry/distribution-service-industry"),
            Map.entry("벤처/중기", "/industry/venture-business"),
            Map.entry("바이오/헬스", "/industry/bioindustry-health"),
            Map.entry("농업", "/industry/agriculture"),
            Map.entry("해양/수산", "/industry/ocean-fishery")
    );
}
