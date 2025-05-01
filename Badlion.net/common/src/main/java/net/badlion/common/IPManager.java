package net.badlion.common;

public class IPManager {

    public static enum Access { ALLOWED, DENIED }

    private GByte[] ips = new GByte[256];

    public void addIP(String ip, Access access) {
        if (ip.contains("/")) {
            String[] ipParts = ip.split("/");
            if (ipParts.length != 2) {
                throw new RuntimeException("Invalid IP provided " + ip);
            }

            Short[] byts = this.getShorts(ipParts[0]);
            Short subnet;
            try {
                subnet = Short.valueOf(ipParts[1]);
            } catch (NumberFormatException e) {
                return;
            }

            int IPsToBan = (int) Math.pow(2, 32 - subnet);
            boolean alreadyShrunken = false;
            if (subnet >= 8) {
                byts[1] = null;
                IPsToBan = (int) Math.pow(2, 8 - subnet);
                alreadyShrunken = true;
            }

            if (subnet >= 16) {
                byts[2] = null;
                if (!alreadyShrunken) {
                    IPsToBan = (int) Math.pow(2, 16 - subnet);
                    alreadyShrunken = true;
                }
            }

            if (subnet >= 24) {
                byts[3] = null;
                if (!alreadyShrunken) {
                    IPsToBan = (int) Math.pow(2, 24 - subnet);
                }
            }

            this.addIP(this.ips, 0, access, IPsToBan, byts);
        } else {
            this.addIP(this.ips, 0, access, 1, this.getShorts(ip));
        }
    }

    private Short[] getShorts(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            throw new RuntimeException("Invalid IP provided " + ip);
        }

        Short[] byts = new Short[4];
        for (int i = 0; i < 4; i++) {
            byts[i] = Short.valueOf(parts[i]);
        }

        return byts;
    }

    private void addIP(GByte[] ips, int rec, Access access, int numOfIps, Short ...byts) {
        if (byts == null || byts.length == 0 || rec >= byts.length || byts[rec] > 255) {
            throw new RuntimeException("\"byts\" is invalid!");
        }

        Short val = byts[rec];
        if (val == null) {
            throw new RuntimeException("null val given");
        }

        GByte gByte = ips[byts[rec]];

        // Well isn't this awkward...now we are allowing part of the subnet
        // Start by denying everything inside and then we will go from there
        if (gByte != null && gByte.getAccess() == Access.DENIED && access == Access.ALLOWED) {
            // Initialize children before we continue
            if (gByte.getBytes() == null) {
                gByte.setBytes(new GByte[256]);
            }

            this.addRange(gByte, 0, 255, Access.DENIED);
        }

        if (gByte == null) {
            // By default allow everything
            gByte = ips[byts[rec]] = new GByte(Access.ALLOWED, rec == 3 || byts[rec + 1] == null ? null : new GByte[256]);
        }

        // We are done with our recursion
        if (rec == 3 || byts[rec + 1] == null) {
            gByte.setAccess(access);

            // Go through to next number until we are done with all the ips?
            if (--numOfIps > 0) {
                this.addIP(ips, rec, access, numOfIps, byts);
            }

            return;
        }

        // Initialize children before we continue
        if (gByte.getBytes() == null) {
            gByte.setBytes(new GByte[256]);
        }

        // Keep going recursively
        this.addIP(gByte.getBytes(), rec + 1, access, numOfIps, byts);
    }

    public Access getAccess(String ip) {
        return this.getAccess(this.ips, 0, this.getShorts(ip));
    }

    private Access getAccess(GByte[] ips, int rec, Short ...byts) {
        if (byts == null || byts.length == 0 || rec >= byts.length) {
            throw new RuntimeException("\"byts\" is invalid!");
        }

        Short val = byts[rec];
        if (val == null) {
            throw new RuntimeException("null val given");
        }

        GByte gByte = ips[byts[rec]];

        // By default it is allowed
        if (gByte == null) {
            return Access.ALLOWED;
        }

        if (rec == 3 || byts[rec + 1] == null) {
            return gByte.getAccess();
        } else if (gByte.getBytes() == null) {
            return gByte.getAccess();
        }

        return this.getAccess(gByte.getBytes(), rec + 1, byts);
    }

    private void addRange(GByte gByte, int start, int end, Access access) {
        for (int i = start; i < end; i++) {
            if (gByte.getBytes()[i] == null) {
                gByte.getBytes()[i] = new GByte(access, null);
            }

            gByte.getBytes()[i].setAccess(access);
        }
    }

    private class GByte {
        private GByte[] bytes;
        private Access access;

        public GByte(Access access, GByte[] bytes) {
            this.access = access;
            this.bytes = bytes;
        }

        public Access getAccess() {
            return this.access;
        }

        public GByte[] getBytes() {
            return bytes;
        }

        public void setAccess(Access access) {
            this.access = access;
        }

        public void setBytes(GByte[] gBytes) {
            this.bytes = gBytes;
        }
    }

}
