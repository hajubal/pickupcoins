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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            log.info("cookie 갱신 user: {}", exchangeDto.userName());
            cookieService.updateCookie(exchangeDto.cookieId(), response.getHeaders().getFirst("cookie"));
        }

        //TODO 리팩토링 필요
        Cookie cookie = cookieService.getCookie(exchangeDto.cookieId());

        //getBody() 는 "10원이 적립 되었습니다." 라는 문자열을 포함하고 있음.
        savedPointRepository.save(SavedPoint.builder()
                .amount(extractAmount(response.getBody()))
                .cookie(cookie)
                .responseBody(response.getBody())
                .build());
    }

    private int extractAmount(String body) {
        Pattern pattern = Pattern.compile("\\s\\d+원이 적립 됩니다.");
        Matcher matcher = pattern.matcher(body);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group().replace("원이 적립 됩니다.", "").trim());
        }

        return 0;
    }
}
