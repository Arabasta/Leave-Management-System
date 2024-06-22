package com.team4.leaveprocessingsystem.util;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class StringCleaningUtilTest {

    @Test
    public void testForDatabase() {
        assertThat(StringCleaningUtil.forDatabase("~`!@#$%^&*()_-=+{}[]|:';'<>?,./#test~`!@#$%^&*()_-=+{}[]|:';'<>?,./#")).isEqualTo("test");
    }

    @Test
    public void testForCSV() {
        assertThat(StringCleaningUtil.forCSV("test")).isEqualTo("\"test\"");
    }
}
