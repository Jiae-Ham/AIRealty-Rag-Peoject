package com.Realty.RealtyWeb.Entity;

import com.Realty.RealtyWeb.enums.Purpose;
import com.Realty.RealtyWeb.enums.RiskLevel;
import com.Realty.RealtyWeb.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*

CREATE TABLE register_analysis (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    pid             BIGINT NOT NULL,  -- house_board.id 참조
    risk_level ENUM('안전', '주의', '위험') DEFAULT '주의',
    issue_date      DATETIME NOT NULL,  -- 등기부등본 발급일자
    owner           VARCHAR(100),        -- 소유자 이름
    risk_keywords   TEXT,                -- 위험 키워드 (콤마 구분: "가압류,가등기,근저당권")
    main_warnings   TEXT,                -- 주요 경고 요약 문장 (UI에 그대로 출력)
    max_claim       DECIMAL(15,2),       -- 채권최고액
    protected_amount DECIMAL(15,2),      -- 보호 가능한 금액
    pdf_base64      LONGTEXT,            -- 원문 PDF (Base64 인코딩)
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pid) REFERENCES house_board(pid) ON DELETE CASCADE
);

 */
@Entity
@Table(name = "register_analysis")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterAnalysisEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userid;

    private Long pid;

    @Column(name = "rag_answer", columnDefinition = "TEXT")
    private String ragAnswer;

    @Enumerated(EnumType.STRING)
    private Purpose purpose;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(columnDefinition = "DECIMAL(15,2)")
    private BigDecimal price;
    @Column(name = "rent_prc", columnDefinition = "DECIMAL(7,2)")
    private BigDecimal rentPrc;


    @Column(name = "exclusive_area", columnDefinition = "DECIMAL(10,2)")
    private BigDecimal exclusiveArea;

    @Lob
    private String pdf_base64;
    private LocalDateTime created_at;

    @PrePersist
    protected void onCreate() {
        this.created_at = LocalDateTime.now();
    }
}
