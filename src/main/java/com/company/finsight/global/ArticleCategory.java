package com.company.finsight.global;

import com.company.finsight.api.article.service.ArticleType;
import lombok.Getter;

@Getter
public enum ArticleCategory {
    INDUSTRIAL_ENTERPRISE("산업/기업", "/industry/industrial-enterprise", ArticleType.YNS),
    ELECTRONICS("전자", "/industry/electronics", ArticleType.YNS),
    HEAVY_CHEMISTRY("중화학", "/industry/heavy-chemistry", ArticleType.YNS),
    AUTOMOBILE("자동차", "/industry/automobile", ArticleType.YNS),
    CONSTRUCTION("건설/부동산", "/industry/construction", ArticleType.YNS),
    ENERGY_RESOURCE("에너지/자원", "/industry/energy-resource", ArticleType.YNS),
    TECHNOLOGY_SCIENCE("IT/과학", "/industry/technology-science", ArticleType.YNS),
    GAME("게임", "/industry/game", ArticleType.YNS),
    DISTRIBUTION_SERVICE("유통/서비스", "/industry/distribution-service-industry", ArticleType.YNS),
    VENTURE_BUSINESS("벤처/중기", "/industry/venture-business", ArticleType.YNS),
    BIO_HEALTH("바이오/헬스", "/industry/bioindustry-health", ArticleType.YNS),
    AGRICULTURE("농업", "/industry/agriculture", ArticleType.YNS),
    OCEAN_FISHERY("해양/수산", "/industry/ocean-fishery", ArticleType.YNS);

    private final String koreanName;
    private final String path;
    private final ArticleType articleType;

    ArticleCategory(String koreanName, String path, ArticleType articleType) {
        this.koreanName = koreanName;
        this.path = path;
        this.articleType = articleType;
    }

}
