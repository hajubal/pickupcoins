package me.synology.hajubal.coins;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpMethod.GET;

public class RestTemplateTest {

    @Test
    void restTemplateTest() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("user-agent", "/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");

        ResponseEntity<String> response = restTemplate.exchange("https://www.naver.com", GET, new HttpEntity<String>(headers), String.class);

        System.out.println("response = " + response.getHeaders());
    }

    @Test
    void webClientTest() throws Exception {
        WebClient client = WebClient.create("https://campaign2-api.naver.com/click-point/?eventId=cr_2023021001_2302_2_1155");

        Mono<String> response = client.get()
                .headers(httpHeaders -> {
                    httpHeaders.add("cookie", "ASID=d3b30b9d0000017dd6296aa700000061; _ga=GA1.1.866894180.1650170369; _ga_7VKFYR6RV1=GS1.1.1661444727.14.0.1661444727.60.0.0; nx_ssl=2; NNB=KIF2CUQKEGHGG; nid_inf=1094034578; NID_AUT=8JnE9R49w0P+kF0VOnvKgiwVSrW5JzO9i+HMo866cr/gQQYfCz1buNwXLS/vact0; NID_JKL=TTICt2nYH2C2v/NI8/rOIxfH5rKzYHTrUWhyxWjCYiU=; NID_SES=AAABsmJjatTzLPdgnO1dm93GjGK6lcASy4d5vllHR4btj2/ddxDxmSe4qw15KRqszDLziqHWDGU6Y6L51jxgKcLaIGUJyHCpMJHOu0S6FUPe65MAuidpHSuThOZamEHuR85HhCme+ZtcQaVLqNT/co5JBVcWQhS4nv+Iaj+ToLFIxVBpnGt5PTL00oDGt1asSTwa/ntUbAztXHtXE8LWUVIoftR4OxgCdKqvUSJgA6FO0CS3xDE6n130Kafm1tDIlMGaWOM2gmLB4zoAixNQzRjO+r6T8bXjKiF/KSDv/qUbB0YVzANAmAF+M9Us58gddv42w8RcwuJoIVh+YI+0DoVLCHYHRSVhae9uBcygDy0E0nCaRtzhE5pv5zymr6ycMMr+4ZCvxi+go6wZNaxu/KxHju4ZUAbVCEuJbccROrjlfcxOA/KjMyvXl75WOES060yY3z3oztfiYLhtm8Y54DXJdIbpNH6fefPsNoe2kfJmEzbSTbMEx+uooi6grd+HPwdg3rEBOgKQTjb3b/yhV/CPMRvzGddYBSksBApiOs92C58zs09Ao42W6Wgh7GEZT1L2gkYdtxcRyTYFbo26H/NelEU=");
                    httpHeaders.add("user-agent", "/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");
                })
                .retrieve()
                .bodyToMono(String.class);

        response.subscribe(s -> {
            System.out.println(">>>>>>>>>>>>>>>>>>>" + s);
        });

        Thread.sleep(1000L);

    }
}
