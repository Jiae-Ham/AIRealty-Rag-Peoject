package com.Realty.RealtyWeb.controller;

import com.Realty.RealtyWeb.dto.RegisterAnalysisDTO;
import com.Realty.RealtyWeb.services.RegisterAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analysis")
public class RegisterAnalysisController {

    private final RegisterAnalysisService registerAnalysisService;

    // 사용자별 분석 이력 조회
    @GetMapping("/list")
    public ResponseEntity<List<RegisterAnalysisDTO>> getUserAnalyses(@AuthenticationPrincipal UserDetails userDetails) {
        String userid = userDetails.getUsername();
        List<RegisterAnalysisDTO> result = registerAnalysisService.getAllByUser(userid);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegisterAnalysisDTO> getAnalysisDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userid = userDetails.getUsername();

        RegisterAnalysisDTO result = registerAnalysisService.getByIdAndUser(id, userid);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/pdf/{id}")
    public ResponseEntity<byte[]> downloadPdf(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        String userid = userDetails.getUsername();
        RegisterAnalysisDTO dto = registerAnalysisService.getByIdAndUser(id, userid);

        if (dto == null || dto.getPdfBase64() == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] decodedPdf = Base64.getDecoder().decode(dto.getPdfBase64());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header("Content-Disposition", "attachment; filename=\"register-analysis-" + id + ".pdf\"")
                .body(decodedPdf);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnalysis(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user
    ) {
        registerAnalysisService.deleteByIdAndUser(id, user.getUsername());
        return ResponseEntity.noContent().build();
    }


}
