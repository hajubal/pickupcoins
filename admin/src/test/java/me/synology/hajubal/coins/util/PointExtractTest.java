package me.synology.hajubal.coins.util;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class PointExtractTest {

    @Test
     void extractTest() {
        String input = "<script>alert('확인 클릭 후 3초 뒤 N페이 15원이 적립 됩니다. \\n (3초 이전 이탈 시 미적립 될 수 있습니다.)')</script>";

        // 정규 표현식을 이용하여 숫자를 추출
        Pattern pattern = Pattern.compile("\\s\\d+원이 적립 됩니다.");
        Matcher matcher = pattern.matcher(input);

        assertThat(matcher.find()).isTrue();
        String number = matcher.group();
        String amount = number.replace("원이 적립 됩니다.", "").trim();
        assertThat(amount).isEqualTo("15");
    }
}
