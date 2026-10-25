package com.venusoft.developer.common.web;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestTimeHolderTest {

    @Test
    void returnsElapsedTimeThenResets() {
        RequestTimeHolder.init();
        assertThat(RequestTimeHolder.getTimeSpan().longValue()).isGreaterThanOrEqualTo(0L);
        assertThat(RequestTimeHolder.getTimeSpan()).isEqualTo(-1L);
    }
}
