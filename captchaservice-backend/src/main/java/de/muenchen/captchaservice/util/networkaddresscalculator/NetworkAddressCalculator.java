package de.muenchen.captchaservice.util.networkaddresscalculator;

import com.google.common.net.InetAddresses;
import java.net.InetAddress;
import java.net.UnknownHostException;

public final class NetworkAddressCalculator {

    private static final int BITS_PER_BYTE = 8;

    private NetworkAddressCalculator() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Calculates the network address for a given IP address and netmask size (CIDR
     * prefix).
     * Supports both IPv4 and IPv6 addresses.
     *
     * @param address The IP address string (e.g., "192.168.1.1", "2001:db8::1").
     * @param netSize The CIDR netmask size (e.g., 24 for IPv4, 64 for IPv6).
     * @return The network address string (e.g., "192.168.1.0", "2001:db8::").
     * @throws InvalidAddressException If the IP address is invalid.
     * @throws InvalidNetSizeException If the `netSize` is out of range for the
     *             given IP version.
     */
    public static String getNetworkAddress(final String address, final int netSize) {
        try {
            final InetAddress inetAddress = InetAddresses.forString(address);
            final byte[] addressBytes = inetAddress.getAddress();

            validateNetSize(netSize, addressBytes.length);

            final byte[] netAddressBytes = applyNetMask(addressBytes, netSize);

            return InetAddress.getByAddress(netAddressBytes).getHostAddress();

        } catch (IllegalArgumentException | UnknownHostException e) {
            throw new InvalidAddressException("Invalid address: " + address, address, e);
        }
    }

    private static void validateNetSize(final int netSize, final int byteLength) {
        final int addressLengthBits = byteLength * BITS_PER_BYTE;

        if (netSize < 0 || netSize > addressLengthBits) {
            throw new InvalidNetSizeException(
                    "Net size " + netSize + " is out of range for a " +
                            (addressLengthBits == 32 ? "IPv4" : "IPv6") +
                            " address (0-" + addressLengthBits + ")",
                    netSize);
        }
    }

    private static byte[] applyNetMask(final byte[] addressBytes, final int netSize) {
        final byte[] result = new byte[addressBytes.length];

        for (int i = 0; i < addressBytes.length; i++) {
            final int remainingBits = netSize - (i * BITS_PER_BYTE);
            result[i] = maskByte(addressBytes[i], remainingBits);
        }

        return result;
    }

    private static byte maskByte(final byte value, final int remainingBits) {
        if (remainingBits >= BITS_PER_BYTE) {
            return value;
        } else if (remainingBits > 0) {
            final int mask = 0xFF << (BITS_PER_BYTE - remainingBits);
            return (byte) (value & mask);
        } else {
            return 0;
        }
    }
}
