package de.muenchen.captchaservice.util.networkaddresscalculator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NetworkAddressCalculatorTest {

    // --- IPv4 Tests ---

    @Test
    void testIPv4_ClassC_Exact() {
        String ip = "192.168.1.100";
        int netSize = 24;
        String expected = "192.168.1.0";
        assertEquals(expected, NetworkAddressCalculator.getNetworkAddress(ip, netSize));
    }

    @Test
    void testIPv4_ClassB_Exact() {
        String ip = "172.16.5.20";
        int netSize = 16;
        String expected = "172.16.0.0";
        assertEquals(expected, NetworkAddressCalculator.getNetworkAddress(ip, netSize));
    }

    @Test
    void testIPv4_ClassA_Exact() {
        String ip = "10.0.0.1";
        int netSize = 8;
        String expected = "10.0.0.0";
        assertEquals(expected, NetworkAddressCalculator.getNetworkAddress(ip, netSize));
    }

    @Test
    void testIPv4_NonOctetBoundary() {
        String ip = "192.168.1.100";
        int netSize = 20;
        String expected = "192.168.0.0";
        assertEquals(expected, NetworkAddressCalculator.getNetworkAddress(ip, netSize));
    }

    @Test
    void testIPv4_NonOctetBoundary_AnotherExample() {
        String ip = "10.10.10.10";
        int netSize = 13;
        String expected = "10.8.0.0";
        assertEquals(expected, NetworkAddressCalculator.getNetworkAddress(ip, netSize));
    }

    @Test
    void testIPv4_FullMask() {
        String ip = "192.168.1.5";
        int netSize = 32;
        String expected = "192.168.1.5";
        assertEquals(expected, NetworkAddressCalculator.getNetworkAddress(ip, netSize));
    }

    @Test
    void testIPv4_ZeroMask() {
        String ip = "192.168.1.5";
        int netSize = 0;
        String expected = "0.0.0.0";
        assertEquals(expected, NetworkAddressCalculator.getNetworkAddress(ip, netSize));
    }

    // --- IPv6 Tests ---

    @Test
    void testIPv6_StandardPrefix() {
        String ip = "2001:0db8:85a3:0000:0000:8a2e:0370:7334";
        int netSize = 64;
        String expected = "2001:db8:85a3:0:0:0:0:0"; // Or "2001:db8:85a3::" depending on InetAddress normalization
        assertEquals(expected, NetworkAddressCalculator.getNetworkAddress(ip, netSize));
    }

    @Test
    void testIPv6_Shorthand() {
        String ip = "2001:db8::1";
        int netSize = 64;
        String expected = "2001:db8:0:0:0:0:0:0"; // InetAddress returns full form
        assertEquals(expected, NetworkAddressCalculator.getNetworkAddress(ip, netSize));
    }

    @Test
    void testIPv6_ShortPrefix() {
        String ip = "2001:db8:85a3:8d3:1319:8a2e:370:7344";
        int netSize = 32;
        String expected = "2001:db8:0:0:0:0:0:0";
        assertEquals(expected, NetworkAddressCalculator.getNetworkAddress(ip, netSize));
    }

    @Test
    void testIPv6_NonOctetBoundary() {
        String ip = "2001:db8:85a3:8d3:1319:8a2e:370:7334";
        // /70 = 4 complete hextets (64 bits) + 6 bits from 5th hextet (1319 -> 1000)
        int netSize = 70; // 64 bits (4 hextets) + 6 bits into the 5th hextet
        String expected = "2001:db8:85a3:8d3:1000:0:0:0";
        assertEquals(expected, NetworkAddressCalculator.getNetworkAddress(ip, netSize));
    }

    @Test
    void testIPv6_FullMask() {
        String ip = "2001:db8::1";
        int netSize = 128;
        String expected = "2001:db8:0:0:0:0:0:1";
        assertEquals(expected, NetworkAddressCalculator.getNetworkAddress(ip, netSize));
    }

    @Test
    void testIPv6_ZeroMask() {
        String ip = "2001:db8::1";
        int netSize = 0;
        String expected = "0:0:0:0:0:0:0:0";
        assertEquals(expected, NetworkAddressCalculator.getNetworkAddress(ip, netSize));
    }

    // --- Error Handling Tests ---

    @Test
    void testInvalidIpAddress() {
        String ip = "invalid-ip";
        int netSize = 24;
        assertThrows(InvalidAddressException.class, () -> {
            NetworkAddressCalculator.getNetworkAddress(ip, netSize);
        });
    }

    @Test
    void testNetSizeTooLargeForIPv4() {
        String ip = "192.168.1.1";
        int netSize = 33;
        assertThrows(InvalidNetSizeException.class, () -> {
            NetworkAddressCalculator.getNetworkAddress(ip, netSize);
        });
    }

    @Test
    void testNetSizeTooSmall() {
        String ip = "192.168.1.1";
        int netSize = -1;
        assertThrows(InvalidNetSizeException.class, () -> {
            NetworkAddressCalculator.getNetworkAddress(ip, netSize);
        });
    }

    @Test
    void testNetSizeTooLargeForIPv6() {
        String ip = "2001:db8::1";
        int netSize = 129;
        assertThrows(InvalidNetSizeException.class, () -> {
            NetworkAddressCalculator.getNetworkAddress(ip, netSize);
        });
    }
}
