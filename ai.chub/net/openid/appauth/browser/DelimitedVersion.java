package net.openid.appauth.browser;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class DelimitedVersion implements Comparable<DelimitedVersion> {
    private static final long BIT_MASK_32 = -1;
    private static final int PRIME_HASH_FACTOR = 92821;
    private final long[] mNumericParts;

    private int compareLongs(long l1, long l2) {
        int i = (l1 > l2 ? 1 : (l1 == l2 ? 0 : -1));
        if (i < 0) {
            return -1;
        }
        return i > 0 ? 1 : 0;
    }

    public DelimitedVersion(long[] numericParts) {
        this.mNumericParts = numericParts;
    }

    public String toString() {
        if (this.mNumericParts.length == 0) {
            return "0";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(this.mNumericParts[0]);
        for (int i = 1; i < this.mNumericParts.length; i++) {
            sb.append('.');
            sb.append(this.mNumericParts[i]);
        }
        return sb.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj != null && (obj instanceof DelimitedVersion) && compareTo((DelimitedVersion) obj) == 0;
    }

    public int hashCode() {
        int i = 0;
        for (long j : this.mNumericParts) {
            i = (i * PRIME_HASH_FACTOR) + ((int) (j & (-1)));
        }
        return i;
    }

    @Override // java.lang.Comparable
    public int compareTo(DelimitedVersion other) {
        long[] jArr;
        int i = 0;
        while (true) {
            jArr = this.mNumericParts;
            if (i >= jArr.length) {
                break;
            }
            long[] jArr2 = other.mNumericParts;
            if (i >= jArr2.length) {
                break;
            }
            int compareLongs = compareLongs(jArr[i], jArr2[i]);
            if (compareLongs != 0) {
                return compareLongs;
            }
            i++;
        }
        return compareLongs(jArr.length, other.mNumericParts.length);
    }

    public static DelimitedVersion parse(String versionString) {
        if (versionString == null) {
            return new DelimitedVersion(new long[0]);
        }
        String[] split = versionString.split("[^0-9]+");
        long[] jArr = new long[split.length];
        int i = 0;
        for (String str : split) {
            if (!str.isEmpty()) {
                jArr[i] = Long.parseLong(str);
                i++;
            }
        }
        do {
            i--;
            if (i < 0) {
                break;
            }
        } while (jArr[i] <= 0);
        int i2 = i + 1;
        long[] jArr2 = new long[i2];
        System.arraycopy(jArr, 0, jArr2, 0, i2);
        return new DelimitedVersion(jArr2);
    }
}
