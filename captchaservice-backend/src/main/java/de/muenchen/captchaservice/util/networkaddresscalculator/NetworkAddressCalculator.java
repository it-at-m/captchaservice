package de.muenchen.captchaservice.util.networkaddresscalculator;

import com.google.common.net.InetAddresses;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkAddressCalculator {

    /**
     * Calculates the network address for a given IP address and netmask size (CIDR prefix).
     * Supports both IPv4 and IPv6 addresses.
     *
     * @param address The IP address string (e.g., "192.168.1.1", "2001:db8::1").
     * @param netSize The CIDR netmask size (e.g., 24 for IPv4, 64 for IPv6).
     * @return The network address string (e.g., "192.168.1.0", "2001:db8::").
     * @throws InvalidAddressException If the IP address is invalid.
     * @throws InvalidNetSizeException If the `netSize` is out of range for the given IP version.
     */
    public static String getNetworkAddress(String address, int netSize) {
        try {
            InetAddress inetAddress = InetAddresses.forString(address);
            byte[] addressBytes = inetAddress.getAddress();
            int addressLengthBits = addressBytes.length * 8;

            if (netSize < 0 || netSize > addressLengthBits) {
                throw new InvalidNetSizeException(
                        "Net size " + netSize + " is out of range for a " +
                                (addressLengthBits == 32 ? "IPv4" : "IPv6") + " address (0-" + addressLengthBits + ")",
                        netSize);
            }

            byte[] netAddressBytes = new byte[addressBytes.length];

            // Apply the netmask bit by bit
            for (int i = 0; i < addressBytes.length; i++) {
                int byteNetSizeRemaining = netSize - (i * 8);
                if (byteNetSizeRemaining >= 8) {
                    netAddressBytes[i] = addressBytes[i];
                } else if (byteNetSizeRemaining > 0) {
                    int mask = 0xFF << (8 - byteNetSizeRemaining);
                    netAddressBytes[i] = (byte) (addressBytes[i] & mask);
                } else {
                    netAddressBytes[i] = 0;
                }
            }

            InetAddress netInetAddress = InetAddress.getByAddress(netAddressBytes);
            return netInetAddress.getHostAddress();
        } catch (IllegalArgumentException | UnknownHostException e) {
            throw new InvalidAddressException("Invalid address: " + address, address, e);
        }
    }
}
