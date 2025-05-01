package net.badlion.common;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class GetCommon {

	private static String ipForLocalSystem = null;
    private static String ipForDBSystem = null;
    private static String ipForSlowDBSystem = null;

    private static String ipForArchAPI = null;

    private static String ipForMiniUHCNAEast = null;
    private static String ipForMiniUHCNAWest = null;
    private static String ipForMiniUHCEU = null;
    private static String ipForMiniUHCAU = null;

	public static String getIpLocalSystem() {
	 	if (GetCommon.ipForLocalSystem != null) {
			return GetCommon.ipForLocalSystem;
		}

		try {
			Enumeration e = NetworkInterface.getNetworkInterfaces();
			while (e.hasMoreElements()) {
				NetworkInterface n = (NetworkInterface) e.nextElement();
				Enumeration ee = n.getInetAddresses();
				while (ee.hasMoreElements()) {
					InetAddress i = (InetAddress) ee.nextElement();
					if (i.getHostAddress().startsWith("10.")) {
						continue;
					} else if (i.getHostAddress().contains("127.0.0.1")) {
						continue;
					} else if (i.getHostAddress().contains(":")) {
						continue;
					}

					GetCommon.ipForLocalSystem = i.getHostAddress();
					return GetCommon.ipForLocalSystem;
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}

		throw new RuntimeException("Could not find suitable IP");
	}

    public static String getIpForDB() {
        if (GetCommon.ipForDBSystem != null) {
            return GetCommon.ipForDBSystem;
        }

        try {
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress i = (InetAddress) ee.nextElement();
                    if (i.getHostAddress().contains("10.0.0")) {
	                    GetCommon.ipForDBSystem = "10.0.0.1";
                        return GetCommon.ipForDBSystem;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

	    GetCommon.ipForDBSystem = "158.69.228.6";
        return GetCommon.ipForDBSystem;
    }

	public static String getIpForSlowDB() {
		if (GetCommon.ipForSlowDBSystem != null) {
			return GetCommon.ipForSlowDBSystem;
		}

		try {
			Enumeration e = NetworkInterface.getNetworkInterfaces();
			while (e.hasMoreElements()) {
				NetworkInterface n = (NetworkInterface) e.nextElement();
				Enumeration ee = n.getInetAddresses();
				while (ee.hasMoreElements()) {
					InetAddress i = (InetAddress) ee.nextElement();
					if (i.getHostAddress().contains("10.0.0")) {
						GetCommon.ipForSlowDBSystem = "10.0.0.2";
						return GetCommon.ipForSlowDBSystem;
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}

		GetCommon.ipForSlowDBSystem = "198.27.85.153";
		return GetCommon.ipForSlowDBSystem;
	}

	public static String getIpForArchAPI() {
		if (GetCommon.ipForArchAPI != null) {
			return GetCommon.ipForArchAPI;
		}

		try {
			Enumeration e = NetworkInterface.getNetworkInterfaces();
			while (e.hasMoreElements()) {
				NetworkInterface n = (NetworkInterface) e.nextElement();
				Enumeration ee = n.getInetAddresses();
				while (ee.hasMoreElements()) {
					InetAddress i = (InetAddress) ee.nextElement();
					if (i.getHostAddress().contains("10.0.0")) {
						GetCommon.ipForArchAPI = "10.0.0.1";
						return GetCommon.ipForArchAPI;
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}

		GetCommon.ipForArchAPI = "158.69.228.6";
		return GetCommon.ipForArchAPI;
	}

	public static String getIpForNAEastMini() {
		if (GetCommon.ipForMiniUHCNAEast != null) {
			return GetCommon.ipForMiniUHCNAEast;
		}

		try {
			Enumeration e = NetworkInterface.getNetworkInterfaces();
			while (e.hasMoreElements()) {
				NetworkInterface n = (NetworkInterface) e.nextElement();
				Enumeration ee = n.getInetAddresses();
				while (ee.hasMoreElements()) {
					InetAddress i = (InetAddress) ee.nextElement();
					if (i.getHostAddress().contains("10.0.0")) {
						GetCommon.ipForMiniUHCNAEast = "10.0.0.200";
						return GetCommon.ipForMiniUHCNAEast;
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}

		GetCommon.ipForMiniUHCNAEast = "158.69.228.7";
		return GetCommon.ipForMiniUHCNAEast;
	}

	public static String getIpForNAWestMini() {
		if (GetCommon.ipForMiniUHCNAWest != null) {
			return GetCommon.ipForMiniUHCNAWest;
		}

		try {
			Enumeration e = NetworkInterface.getNetworkInterfaces();
			while (e.hasMoreElements()) {
				NetworkInterface n = (NetworkInterface) e.nextElement();
				Enumeration ee = n.getInetAddresses();
				while (ee.hasMoreElements()) {
					InetAddress i = (InetAddress) ee.nextElement();
					if (i.getHostAddress().contains("10.1.1")) {
						GetCommon.ipForMiniUHCNAWest = "10.1.1.39";
						return GetCommon.ipForMiniUHCNAWest;
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}

		GetCommon.ipForMiniUHCNAWest = "45.34.16.146";
		return GetCommon.ipForMiniUHCNAWest;
	}

	public static String getIpForEUMini() {
		if (GetCommon.ipForMiniUHCEU != null) {
			return GetCommon.ipForMiniUHCEU;
		}

		try {
			Enumeration e = NetworkInterface.getNetworkInterfaces();
			while (e.hasMoreElements()) {
				NetworkInterface n = (NetworkInterface) e.nextElement();
				Enumeration ee = n.getInetAddresses();
				while (ee.hasMoreElements()) {
					InetAddress i = (InetAddress) ee.nextElement();
					if (i.getHostAddress().contains("10.0.0")) {
						GetCommon.ipForMiniUHCEU = "10.0.0.210";
						return GetCommon.ipForMiniUHCEU;
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}

		GetCommon.ipForMiniUHCEU = "151.80.104.64";
		return GetCommon.ipForMiniUHCEU;
	}

	public static String getIpForAUMini() {
		if (GetCommon.ipForMiniUHCAU != null) {
			return GetCommon.ipForMiniUHCAU;
		}

		GetCommon.ipForMiniUHCAU = "221.121.151.35";
		return GetCommon.ipForMiniUHCAU;
	}

}
