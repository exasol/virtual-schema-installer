package com.exasol.adapter.installer.fingerprint;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class FingerprintExtractorTest {

    @Test
    void testExtractFingerprintWithCertificateValidationOff() {
        assertThat(FingerprintExtractor.extractFingerprint("jdbc:exa:localhost:49160;validateservercertificate=0")
                .isPresent(), is(false));
    }

    @Test
    void testExtractFingerprintFromLocalhostUrl() {
        assertThat(
                FingerprintExtractor
                        .extractFingerprint("jdbc:exa:localhost/fingerprint:1234;validateservercertificate=1").get(),
                equalTo("fingerprint"));
    }

    @Test
    void testExtractFingerprint() {
        assertThat(
                FingerprintExtractor
                        .extractFingerprint("jdbc:exa:127.0.0.1/fingerprint:1234;validateservercertificate=1").get(),
                equalTo("fingerprint"));
    }

    @Test
    void testExtractFingerprintFailed() {
        final IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> FingerprintExtractor.extractFingerprint("jdbc:exa:127.0.0.1:1234;validateservercertificate=1"));
        assertThat(exception.getMessage(), equalTo(
                "E-VS-INSTL-11: Error extracting fingerprint from 'jdbc:exa:127.0.0.1:1234;validateservercertificate=1'"));
    }
}
