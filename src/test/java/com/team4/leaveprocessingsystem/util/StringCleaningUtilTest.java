package com.team4.leaveprocessingsystem.util;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class StringCleaningUtilTest {

    @Test
    public void testForDatabase() {
        assertThat(StringCleaningUtil.forDatabase("~`!@#$%^&*()_-=+{}[]|:';'<>?/#This#, is a!@# test string.~`!@#$%^&*()_-=+{}[]|:';'<>?/#")).isEqualTo("This, is a test string.");
    }

    @Test
    public void testForCSV() {
        assertThat(StringCleaningUtil.forCSV("test")).isEqualTo("\"test\"");
    }
}
