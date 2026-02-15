package com.Realty.RealtyWeb.services;

import com.Realty.RealtyWeb.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface CodefRegisterService {

    /**
     * 등기부등본 1차 요청: selectAddress 여부 포함
     */
    JsonNode requestRegisterFirst(CodefRequestDTO dto, String address) throws JsonProcessingException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException, UnsupportedEncodingException, InterruptedException;

    JsonNode requestByUnique(UniqueNoRequestDTO dto);

    /**
     * 응답에서 추가 인증 여부 확인 및 분기 판단용
     */
    boolean isTwoWayRequired(JsonNode response);

   // Long parseFinalResult(JsonNode response, Long pid, String userid, HouseInfoDTO houseInfo);

}
