package com.company.finsight.global;

public enum YNSCategory {
    INDUSTRIAL_ENTERPRISE("산업/기업", "/industry/industrial-enterprise"),
    ELECTRONICS("전자", "/industry/electronics"),
    HEAVY_CHEMISTRY("중화학", "/industry/heavy-chemistry"),
    AUTOMOBILE("자동차", "/industry/automobile"),
    CONSTRUCTION("건설/부동산", "/industry/construction"),
    ENERGY_RESOURCE("에너지/자원", "/industry/energy-resource"),
    TECHNOLOGY_SCIENCE("IT/과학", "/industry/technology-science"),
    GAME("게임", "/industry/game"),
    DISTRIBUTION_SERVICE("유통/서비스", "/industry/distribution-service-industry"),
    VENTURE_BUSINESS("벤처/중기", "/industry/venture-business"),
    BIO_HEALTH("바이오/헬스", "/industry/bioindustry-health"),
    AGRICULTURE("농업", "/industry/agriculture"),
    OCEAN_FISHERY("해양/수산", "/industry/ocean-fishery");

    private final String koreanName;
    private final String path;

    YNSCategory(String koreanName, String path) {
        this.koreanName = koreanName;
        this.path = path;
    }

    public String getKoreanName() { return koreanName; }
    public String getPath() { return path; }
}
