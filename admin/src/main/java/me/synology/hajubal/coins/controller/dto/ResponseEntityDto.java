package me.synology.hajubal.coins.controller.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Optional;

@Data
public class ResponseEntityDto implements Serializable {

    private String message;

    private ResponseEntityDto() {
    }

    public static ResponseEntityDto successResponse() {
        ResponseEntityDto dto = new ResponseEntityDto();
        dto.message = "OK";

        return dto;
    }

    /**
     * 화면에서 오류관련 처리가 없을경우에 보내는 응답
     * @return
     */
    public static ResponseEntityDto failResponse() {
        return failResponse(Optional.empty());
    }

    public static ResponseEntityDto failResponse(Optional<String> message) {
        ResponseEntityDto dto = new ResponseEntityDto();
        dto.message = message.orElse("FAIL");

        return dto;
    }

}
