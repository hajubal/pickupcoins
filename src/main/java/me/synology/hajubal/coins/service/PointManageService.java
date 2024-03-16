package me.synology.hajubal.coins.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.Cookie;
import me.synology.hajubal.coins.entity.SavedPoint;
import me.synology.hajubal.coins.respository.SavedPointRepository;
import me.synology.hajubal.coins.service.dto.ExchangeDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class PointManageService {

    private final CookieService cookieService;

    private final SavedPointRepository savedPointRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void savePointPostProcess(ExchangeDto exchangeDto, ResponseEntity<String> response) {
        //cookie session값 갱신
        if(response.getHeaders().containsKey("cookie")) {
            log.info("cookie 갱신 user: {}", exchangeDto.getUserName());
            cookieService.updateCookie(exchangeDto.getCookieId(), response.getHeaders().getFirst("cookie"));
        }

        //TODO 리팩토링 필요
        Cookie cookie = cookieService.getCookie(exchangeDto.getCookieId());

        //getBody() 는 "10원이 적립 되었습니다." 라는 문자열을 포함하고 있음.
        savedPointRepository.save(SavedPoint.builder()
                .point("코드 수정 필요")
                .cookie(cookie)
                .responseBody(response.getBody())
                .build());
    }
}
