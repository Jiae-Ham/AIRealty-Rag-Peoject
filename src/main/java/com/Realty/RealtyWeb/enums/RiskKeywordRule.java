package com.Realty.RealtyWeb.enums;

import java.util.Arrays;
import java.util.Optional;

public enum RiskKeywordRule {

    근저당권("주의", "매물을 담보로 받은 융자금이 있어요.",
            "채권최고액과 보증금의 합이 시세의 70% 이하일 때 안전하다고 판단할 수 있어요."),

    가압류("위험", "집이 법적 압류 상태예요.",
            "채권자가 소송이나 빚 문제로 이 집에 대해 가압류를 걸어둔 상태예요. 계약 전 주의가 필요해요."),

    가등기("위험", "소유권 이전 예약이 등록되어 있어요.", "가등기는 소유권 이전을 예약하는 등기로, 본등기로 전환될 경우 소유권이 이전될 수 있어요. 추후 분쟁의 소지가 있으므로 주의가 필요해요."),

    가처분("위험", "부동산에 법적 분쟁이 걸려 있어요.",
            "가처분은 법원이 부동산의 처분을 금지하는 조치로, 매매나 이전이 제한될 수 있어요. 계약 전에 법적 상태를 확인해야 해요."),

    전세권설정("주의", "기존 세입자의 전세권이 등록되어 있어요.",
            "보증금 회수 우선순위가 밀릴 수 있어요. 보호한도 초과 여부 확인이 필요해요."),

    임차권등기명령("위험", "이전 세입자가 보증금을 못 받은 기록이 있어요.",
            "보증금 회수가 어려울 수 있습니다. 새 입주자도 위험할 수 있어요."),

    소유권이전등기("안전", "소유권 변동 가능성이 있어요.",
            "집주인 또는 전문가와 소유권 변동에 대한 확인이 필요해요."),

    공동담보설정("주의", "부동산이 공동담보로 설정되어 있어요.", "공동담보는 하나의 채권에 대해 여러 부동산이 담보로 설정된 것으로, 다른 부동산의 채무 불이행이 영향을 줄 수 있어요. "),

    소유권보존등기("안전", "최초 소유권이 등록된 상태예요.", "일반적으로 문제가 없어요. 그러나 등기일과 실제 사용 상태를 확인해야 해요."),

    경매개시결정("위험", "이 집이 경매 절차에 들어간 상태예요.",
            "계약해도 경매 낙찰로 무효될 수 있어요."),

    신탁("위험", "부동산이 신탁회사에 신탁되었어요.", "신탁은 부동산의 소유권이 신탁회사에 이전되어 관리되는 것으로, 계약 시 신탁 조건과 수익자의 권리를 확인해야 해요.");

    private final String level;
    private final String title;
    private final String description;

    RiskKeywordRule(String level, String title, String description) {
        this.level = level;
        this.title = title;
        this.description = description;
    }

    public String getLevel() {
        return level;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public static Optional<RiskKeywordRule> match(String raw) {
        return Arrays.stream(values())
                .filter(rule -> raw.replace(" ", "").contains(rule.name()))
                .findFirst();
    }
}
