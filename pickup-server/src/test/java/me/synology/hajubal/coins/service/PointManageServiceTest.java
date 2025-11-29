package me.synology.hajubal.coins.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import me.synology.hajubal.coins.config.NaverPointProperties;
import me.synology.hajubal.coins.entity.Cookie;
import me.synology.hajubal.coins.entity.SavedPoint;
import me.synology.hajubal.coins.respository.SavedPointRepository;
import me.synology.hajubal.coins.dto.ExchangeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class PointManageServiceTest {

  @Mock private CookieService cookieService;
  @Mock private SavedPointRepository savedPointRepository;
  @Mock private NaverPointProperties naverPointProperties;

  private PointManageService pointManageService;

  @BeforeEach
  void setUp() {
    given(naverPointProperties.getAmountPattern()).willReturn("\\s\\d+원이 적립 됩니다.");
    pointManageService =
        new PointManageService(cookieService, savedPointRepository, naverPointProperties);
  }

  @Test
  @DisplayName("포인트 적립 후 처리 - Set-Cookie 헤더가 있는 경우 쿠키 갱신")
  void savePointPostProcess_withSetCookieHeader() {
    // given
    ExchangeDto exchangeDto =
        new ExchangeDto(
            1L, "testUser", "oldCookie", "https://webhook.slack.com/test", "naver");

    Cookie cookie = Cookie.builder().userName("testUser").siteName("naver").build();

    String responseBody = "축하합니다! 10원이 적립 됩니다.";
    HttpHeaders headers = new HttpHeaders();
    headers.add("Set-Cookie", "newCookieValue=abc123; Path=/; HttpOnly");

    ResponseEntity<String> response = new ResponseEntity<>(responseBody, headers, HttpStatus.OK);

    given(cookieService.getCookie(1L)).willReturn(cookie);

    // when
    pointManageService.savePointPostProcess(exchangeDto, response);

    // then
    then(cookieService).should().updateCookie(1L, "newCookieValue=abc123; Path=/; HttpOnly");

    ArgumentCaptor<SavedPoint> captor = ArgumentCaptor.forClass(SavedPoint.class);
    then(savedPointRepository).should().save(captor.capture());

    SavedPoint savedPoint = captor.getValue();
    assertThat(savedPoint.getAmount()).isEqualTo(10);
    assertThat(savedPoint.getCookie()).isEqualTo(cookie);
    assertThat(savedPoint.getResponseBody()).isEqualTo(responseBody);
  }

  @Test
  @DisplayName("포인트 적립 후 처리 - Set-Cookie 헤더가 없는 경우 쿠키 갱신하지 않음")
  void savePointPostProcess_withoutSetCookieHeader() {
    // given
    ExchangeDto exchangeDto =
        new ExchangeDto(1L, "testUser", "cookie", "https://webhook.slack.com/test", "naver");

    Cookie cookie = Cookie.builder().userName("testUser").siteName("naver").build();

    String responseBody = "축하합니다! 5원이 적립 됩니다.";
    ResponseEntity<String> response = new ResponseEntity<>(responseBody, HttpStatus.OK);

    given(cookieService.getCookie(1L)).willReturn(cookie);

    // when
    pointManageService.savePointPostProcess(exchangeDto, response);

    // then
    then(cookieService).should(never()).updateCookie(anyLong(), anyString());

    ArgumentCaptor<SavedPoint> captor = ArgumentCaptor.forClass(SavedPoint.class);
    then(savedPointRepository).should().save(captor.capture());

    SavedPoint savedPoint = captor.getValue();
    assertThat(savedPoint.getAmount()).isEqualTo(5);
  }

  @Test
  @DisplayName("응답 본문에서 포인트 금액 추출 - 다양한 금액")
  void extractAmount_variousAmounts() {
    // given
    ExchangeDto exchangeDto =
        new ExchangeDto(1L, "testUser", "cookie", "https://webhook.slack.com/test", "naver");

    Cookie cookie = Cookie.builder().userName("testUser").siteName("naver").build();
    given(cookieService.getCookie(1L)).willReturn(cookie);

    // when & then - 100원
    String responseBody100 = "축하합니다! 100원이 적립 됩니다.";
    ResponseEntity<String> response100 = new ResponseEntity<>(responseBody100, HttpStatus.OK);

    pointManageService.savePointPostProcess(exchangeDto, response100);

    ArgumentCaptor<SavedPoint> captor = ArgumentCaptor.forClass(SavedPoint.class);
    then(savedPointRepository).should().save(captor.capture());
    assertThat(captor.getValue().getAmount()).isEqualTo(100);

    // when & then - 1원
    String responseBody1 = "축하합니다! 1원이 적립 됩니다.";
    ResponseEntity<String> response1 = new ResponseEntity<>(responseBody1, HttpStatus.OK);

    pointManageService.savePointPostProcess(exchangeDto, response1);

    then(savedPointRepository).should(times(2)).save(captor.capture());
    assertThat(captor.getValue().getAmount()).isEqualTo(1);
  }

  @Test
  @DisplayName("응답 본문에서 포인트 금액 추출 실패 시 0원으로 저장")
  void extractAmount_failureReturnsZero() {
    // given
    ExchangeDto exchangeDto =
        new ExchangeDto(1L, "testUser", "cookie", "https://webhook.slack.com/test", "naver");

    Cookie cookie = Cookie.builder().userName("testUser").siteName("naver").build();
    given(cookieService.getCookie(1L)).willReturn(cookie);

    String responseBody = "포인트 적립에 실패했습니다.";
    ResponseEntity<String> response = new ResponseEntity<>(responseBody, HttpStatus.OK);

    // when
    pointManageService.savePointPostProcess(exchangeDto, response);

    // then
    ArgumentCaptor<SavedPoint> captor = ArgumentCaptor.forClass(SavedPoint.class);
    then(savedPointRepository).should().save(captor.capture());

    SavedPoint savedPoint = captor.getValue();
    assertThat(savedPoint.getAmount()).isEqualTo(0);
  }
}
