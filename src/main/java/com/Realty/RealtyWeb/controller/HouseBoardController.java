package com.Realty.RealtyWeb.controller;

import com.Realty.RealtyWeb.dto.*;
import com.Realty.RealtyWeb.enums.Purpose;
import com.Realty.RealtyWeb.enums.TransactionType;
import com.Realty.RealtyWeb.services.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/house-board")
public class HouseBoardController {

    private final HouseBoardService houseBoardService;
    private final ImageService imagesService;
    private final CodefRegisterService codefRegisterService;
    private final HouseInfoService houseInfoService;
    private final RegisterAnalysisService registerAnalysisService;
    private final RagClientService ragClientService;

    @PostMapping("/{pid}/analyze")
    public ResponseEntity<?> analyzeRegister(
            @PathVariable Long pid,
            @RequestBody CodefRequestDTO dto,
            @AuthenticationPrincipal UserDetails user
    ) {
        try {
            // ① 주소 조회
            HouseInfoDTO infoDTO = houseInfoService.findAddressByPid(pid);

            // ② Codef 1차 요청
            JsonNode response = codefRegisterService.requestRegisterFirst(dto, infoDTO.getAddress() + " " + infoDTO.getAddressDetail());


            // ②-1 예외 코드 확인
            String resultCode = response.path("result").path("code").asText();
            if ("CF-12701".equals(resultCode)) {
                Thread.sleep(1000);
                response = codefRegisterService.requestRegisterFirst(dto, infoDTO.getAddress() + " " + infoDTO.getAddressDetail());
                resultCode = response.path("result").path("code").asText();
            }

            if ("CF-13006".equals(resultCode)) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("주소가 정확하지 않거나 철자가 잘못되었습니다.");
            } else if ("CF-13328".equals(resultCode)) {
                return ResponseEntity
                        .status(HttpStatus.PAYMENT_REQUIRED)
                        .body("선불전자지급수단의 잔액이 부족합니다.");
            }

            // ③ 2차 인증이 필요한 경우
            if (codefRegisterService.isTwoWayRequired(response)) {
                JsonNode addrList = response.path("data").path("extraInfo").path("resAddrList");

                // ✅ 무조건 첫 번째 주소 사용
                if (!addrList.isArray() || addrList.size() == 0) {
                    throw new IllegalArgumentException("유효하지 않은 주소입니다.");
                }

                String commUniqueNo = addrList.get(0).path("commUniqueNo").asText();

                // ④ Unique 요청 DTO 구성
                UniqueNoRequestDTO uniqueDto = UniqueNoRequestDTO.builder()
                        .phoneNo(dto.getPhoneNo())
                        .password(dto.getPassword())
                        .uniqueNo(commUniqueNo)
                        .inquiryType("0")     // 보통 "0"
                        .issueType("1")         // 보통 "1"
                        .ePrepayNo(dto.getEPrepayNo())
                        .ePrepayPass(dto.getEPrepayPass())
                        .recordStatus("0")
                        .jointMortgageJeonseYN("0")
                        .tradingYN("0")
                        .electronicClosedYN("0")
                        .selectAddress("0")                     // 자동 선택 핵심
                        .isIdentityViewYN("0")
                        .originDataYN("1")
                        .warningSkipYN("0")
                        .build  ();

                // ⑤ 2차 요청 → 최종 분석
                JsonNode uniqueResponse = codefRegisterService.requestByUnique(uniqueDto);

// ✅ [추가] uniqueResponse를 JSON 파일로 저장
                try {
                    String tempDir = System.getProperty("java.io.tmpdir");
                    String fileName = String.format("unique_response_%d_%d.json", pid, System.currentTimeMillis());
                    Path filePath = Paths.get(tempDir, fileName);
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), uniqueResponse);

                    System.out.println("uniqueResponse 저장 위치: " + filePath.toAbsolutePath());
                } catch (IOException ioe) {
                    System.err.println("uniqueResponse 저장 중 오류 발생: " + ioe.getMessage());
                }

                String pdfBase64 = uniqueResponse.path("data").path("resOriGinalData").asText();

                // RAG 실행
                String ragAnswer = ragClientService.analyzeWithRag(uniqueResponse);

                RegisterAnalysisDTO analysisDTO = RegisterAnalysisDTO.builder()
                        .pid(pid)
                        .userid(user.getUsername())
                        .purpose(Purpose.valueOf(infoDTO.getPurpose()))
                        .transactionType(TransactionType.valueOf(infoDTO.getTransactionType()))
                        .price(infoDTO.getPrice())
                        .rentPrc(infoDTO.getRentPrc())
                        .exclusiveArea(infoDTO.getExclusiveArea())
                        .pdfBase64(pdfBase64)
                        .ragAnswer(ragAnswer)
                        .build();


                // ⑦ 분석 DTO에 LLM 결과 합산
                Long id = registerAnalysisService.save(analysisDTO);
                RegisterAnalysisDTO savedDto = registerAnalysisService.getByIdAndUser(id, user.getUsername());

                return ResponseEntity.ok(savedDto);

            }
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("2차 인증이 필요하지 않은 요청입니다. 현재는 2차 인증만 지원됩니다.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 매물 게시글 등록 
    /*
      @AuthenticationPrincipal UserDetails userDetails을 이용해서 하면
      유저 디테일에서 현재 로그인한 사용자의 정보를 가져올 수 있음. 
     */
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HouseResisterRequestDTO> createHouseBoard(
            @RequestPart("data") HouseResisterRequestDTO requestDTO,
            @RequestParam(value = "pimg", required = false) MultipartFile pimg,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (requestDTO.getHouseBoardDTO() == null || requestDTO.getHouseInfoDTO() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // 이미지 저장 및 세팅
        String imageUrl = (pimg != null && !pimg.isEmpty()) ? imagesService.save(pimg) : null;
        requestDTO.getHouseBoardDTO().setPimg(imageUrl);

        HouseResisterRequestDTO responseDTO = houseBoardService.createHouseBoard(
                userDetails.getUsername(),
                requestDTO.getHouseBoardDTO(),
                requestDTO.getHouseInfoDTO()
        );

        return ResponseEntity.ok(responseDTO);
    }

    // 특정 매물 게시글 조회
    @GetMapping("/{pid}")
    public ResponseEntity<HouseResisterRequestDTO> getHouseBoardById(@PathVariable Long pid) {
        Optional<HouseResisterRequestDTO> houseBoard = houseBoardService.getHouseBoardById(pid);
        return houseBoard.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 특정 회원이 작성한 매물 게시글 조회 -> 아이디말고 닉네임으로 수정할 것
    @GetMapping("/user/{username}")
    public ResponseEntity<List<HouseResisterRequestDTO>> getHouseBoardsByUser(@PathVariable String username) {
        List<HouseResisterRequestDTO> houseBoards = houseBoardService.getHouseBoardsByUsername(username);
        return ResponseEntity.ok(houseBoards);
    }

    //전체 매물 게시글 조회
    @GetMapping("/list")
    public Page<HouseBoardSummaryDTO> getHouseBoards(
            @RequestParam(required = false) String purpose,       // 매물 종류 (Optional)
            @RequestParam(required = false) String transactionType, // 거래 방식 (Optional)
            @RequestParam(required = false) Integer minPrice,    // 최소 가격
            @RequestParam(required = false) Integer maxPrice,    // 최대 가격
            @RequestParam(required = false) Integer minRentPrc,    // 최소 월세
            @RequestParam(required = false) Integer maxRentPrc,    // 최대 월세
            @RequestParam(required = false) Integer minExclusiveArea, // 최소 전용 면적
            @RequestParam(required = false) Integer maxExclusiveArea, // 최대 전용 면적
            @RequestParam(required = false) Integer minParkingPerHouseholdCount, // 주차 대수
            @RequestParam(required = false) String addrCode, // 지역코드
            @RequestParam(defaultValue = "0") int page,         // 페이지 번호 (기본값: 0)
            @RequestParam(defaultValue = "10") int size         // 페이지 크기 (기본값: 10)
    ) {
        HouseBoardFilterDTO filter = HouseBoardFilterDTO.builder()
                .purpose(purpose != null ? Purpose.valueOf(purpose) : null)
                .transactionType(transactionType != null ? TransactionType.valueOf(transactionType) : null)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .minRentPrc(minRentPrc)
                .maxRentPrc(maxRentPrc)
                .minExclusiveArea(minExclusiveArea)
                .maxExclusiveArea(maxExclusiveArea)
                .minParkingPerHouseholdCount(minParkingPerHouseholdCount)
                .addrCode(addrCode)
                .build();

        Pageable pageable = PageRequest.of(page, size);
        return houseBoardService.getAllHouseBoards(filter, pageable);
    }


    // 매물 게시글 수정
    /*
    아래 방법은 좀 더 수동적인 방법. SecurityContextHolder를 이용해서 
    수동으로 현재 로그인한 사용자의 정보를 가져옴. 컨트롤러보다는 서비스에서 사용하면 될 방법
     */
    @PutMapping(value = "/update/{pid}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HouseResisterRequestDTO> updateHouseBoard(
            @PathVariable Long pid,
            @RequestPart("data") HouseResisterRequestDTO requestDTO,
            @RequestPart(value = "pimg", required = false) MultipartFile pimg) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        // 🔄 이미지 새로 올렸으면 저장하고 덮어쓰기
        if (pimg != null && !pimg.isEmpty()) {
            String imageUrl = imagesService.save(pimg);
            requestDTO.getHouseBoardDTO().setPimg(imageUrl);
        }

        HouseResisterRequestDTO updatedBoard = houseBoardService.updateHouseBoard(
                pid,
                userId,
                requestDTO.getHouseBoardDTO(),
                requestDTO.getHouseInfoDTO()
        );

        return ResponseEntity.ok(updatedBoard);
    }



    // 매물 게시글 삭제
    @DeleteMapping("/delete/{pid}")
    public ResponseEntity<String> deleteHouseBoard(@PathVariable Long pid) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName(); // 🔹 JWT에서 userId 가져오기

        boolean deleted = houseBoardService.deleteHouseBoard(pid, userId);
        return deleted ? ResponseEntity.ok("매물 게시글이 삭제되었습니다.") : ResponseEntity.notFound().build();
    }

}
