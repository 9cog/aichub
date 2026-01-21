package okio.internal;

import java.util.Arrays;
import kotlin.Metadata;
import kotlin.UByte;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.ByteCompanionObject;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import okhttp3.HttpUrl;
import okio.Base64;
import okio.Buffer;
import okio.SegmentedByteString;
import okio._JvmPlatformKt;
/* compiled from: ByteString.kt */
@Metadata(d1 = {"\u0000R\n\u0000\n\u0002\u0010\u0019\n\u0002\b\u0005\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\f\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0005\n\u0002\b\u0017\n\u0002\u0018\u0002\n\u0000\u001a\u0018\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0007H\u0002\u001a\u0011\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\tH\u0080\b\u001a\u0010\u0010\u000e\u001a\u00020\u00072\u0006\u0010\u000f\u001a\u00020\u0010H\u0002\u001a\r\u0010\u0011\u001a\u00020\u0012*\u00020\fH\u0080\b\u001a\r\u0010\u0013\u001a\u00020\u0012*\u00020\fH\u0080\b\u001a\u0015\u0010\u0014\u001a\u00020\u0007*\u00020\f2\u0006\u0010\u0015\u001a\u00020\fH\u0080\b\u001a-\u0010\u0016\u001a\u00020\u0017*\u00020\f2\u0006\u0010\u0018\u001a\u00020\u00072\u0006\u0010\u0019\u001a\u00020\t2\u0006\u0010\u001a\u001a\u00020\u00072\u0006\u0010\u001b\u001a\u00020\u0007H\u0080\b\u001a\u000f\u0010\u001c\u001a\u0004\u0018\u00010\f*\u00020\u0012H\u0080\b\u001a\r\u0010\u001d\u001a\u00020\f*\u00020\u0012H\u0080\b\u001a\r\u0010\u001e\u001a\u00020\f*\u00020\u0012H\u0080\b\u001a\u0015\u0010\u001f\u001a\u00020 *\u00020\f2\u0006\u0010!\u001a\u00020\tH\u0080\b\u001a\u0015\u0010\u001f\u001a\u00020 *\u00020\f2\u0006\u0010!\u001a\u00020\fH\u0080\b\u001a\u0017\u0010\"\u001a\u00020 *\u00020\f2\b\u0010\u0015\u001a\u0004\u0018\u00010#H\u0080\b\u001a\u0015\u0010$\u001a\u00020%*\u00020\f2\u0006\u0010&\u001a\u00020\u0007H\u0080\b\u001a\r\u0010'\u001a\u00020\u0007*\u00020\fH\u0080\b\u001a\r\u0010(\u001a\u00020\u0007*\u00020\fH\u0080\b\u001a\r\u0010)\u001a\u00020\u0012*\u00020\fH\u0080\b\u001a\u001d\u0010*\u001a\u00020\u0007*\u00020\f2\u0006\u0010\u0015\u001a\u00020\t2\u0006\u0010+\u001a\u00020\u0007H\u0080\b\u001a\r\u0010,\u001a\u00020\t*\u00020\fH\u0080\b\u001a\u001d\u0010-\u001a\u00020\u0007*\u00020\f2\u0006\u0010\u0015\u001a\u00020\t2\u0006\u0010+\u001a\u00020\u0007H\u0080\b\u001a\u001d\u0010-\u001a\u00020\u0007*\u00020\f2\u0006\u0010\u0015\u001a\u00020\f2\u0006\u0010+\u001a\u00020\u0007H\u0080\b\u001a-\u0010.\u001a\u00020 *\u00020\f2\u0006\u0010\u0018\u001a\u00020\u00072\u0006\u0010\u0015\u001a\u00020\t2\u0006\u0010/\u001a\u00020\u00072\u0006\u0010\u001b\u001a\u00020\u0007H\u0080\b\u001a-\u0010.\u001a\u00020 *\u00020\f2\u0006\u0010\u0018\u001a\u00020\u00072\u0006\u0010\u0015\u001a\u00020\f2\u0006\u0010/\u001a\u00020\u00072\u0006\u0010\u001b\u001a\u00020\u0007H\u0080\b\u001a\u0015\u00100\u001a\u00020 *\u00020\f2\u0006\u00101\u001a\u00020\tH\u0080\b\u001a\u0015\u00100\u001a\u00020 *\u00020\f2\u0006\u00101\u001a\u00020\fH\u0080\b\u001a\u001d\u00102\u001a\u00020\f*\u00020\f2\u0006\u00103\u001a\u00020\u00072\u0006\u00104\u001a\u00020\u0007H\u0080\b\u001a\r\u00105\u001a\u00020\f*\u00020\fH\u0080\b\u001a\r\u00106\u001a\u00020\f*\u00020\fH\u0080\b\u001a\r\u00107\u001a\u00020\t*\u00020\fH\u0080\b\u001a\u001d\u00108\u001a\u00020\f*\u00020\t2\u0006\u0010\u0018\u001a\u00020\u00072\u0006\u0010\u001b\u001a\u00020\u0007H\u0080\b\u001a\r\u00109\u001a\u00020\u0012*\u00020\fH\u0080\b\u001a\r\u0010:\u001a\u00020\u0012*\u00020\fH\u0080\b\u001a$\u0010;\u001a\u00020\u0017*\u00020\f2\u0006\u0010<\u001a\u00020=2\u0006\u0010\u0018\u001a\u00020\u00072\u0006\u0010\u001b\u001a\u00020\u0007H\u0000\"\u001c\u0010\u0000\u001a\u00020\u00018\u0000X\u0081\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\u0002\u0010\u0003\u001a\u0004\b\u0004\u0010\u0005¨\u0006>"}, d2 = {"HEX_DIGIT_CHARS", HttpUrl.FRAGMENT_ENCODE_SET, "getHEX_DIGIT_CHARS$annotations", "()V", "getHEX_DIGIT_CHARS", "()[C", "codePointIndexToCharIndex", HttpUrl.FRAGMENT_ENCODE_SET, "s", HttpUrl.FRAGMENT_ENCODE_SET, "codePointCount", "commonOf", "Lokio/ByteString;", "data", "decodeHexDigit", "c", HttpUrl.FRAGMENT_ENCODE_SET, "commonBase64", HttpUrl.FRAGMENT_ENCODE_SET, "commonBase64Url", "commonCompareTo", "other", "commonCopyInto", HttpUrl.FRAGMENT_ENCODE_SET, "offset", "target", "targetOffset", "byteCount", "commonDecodeBase64", "commonDecodeHex", "commonEncodeUtf8", "commonEndsWith", HttpUrl.FRAGMENT_ENCODE_SET, "suffix", "commonEquals", HttpUrl.FRAGMENT_ENCODE_SET, "commonGetByte", HttpUrl.FRAGMENT_ENCODE_SET, "pos", "commonGetSize", "commonHashCode", "commonHex", "commonIndexOf", "fromIndex", "commonInternalArray", "commonLastIndexOf", "commonRangeEquals", "otherOffset", "commonStartsWith", "prefix", "commonSubstring", "beginIndex", "endIndex", "commonToAsciiLowercase", "commonToAsciiUppercase", "commonToByteArray", "commonToByteString", "commonToString", "commonUtf8", "commonWrite", "buffer", "Lokio/Buffer;", "okio"}, k = 2, mv = {1, 9, 0}, xi = 48)
/* renamed from: okio.internal.-ByteString */
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public final class ByteString {
    private static final char[] HEX_DIGIT_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static /* synthetic */ void getHEX_DIGIT_CHARS$annotations() {
    }

    public static final String commonUtf8(okio.ByteString byteString) {
        Intrinsics.checkNotNullParameter(byteString, "<this>");
        String utf8$okio = byteString.getUtf8$okio();
        if (utf8$okio == null) {
            String utf8String = _JvmPlatformKt.toUtf8String(byteString.internalArray$okio());
            byteString.setUtf8$okio(utf8String);
            return utf8String;
        }
        return utf8$okio;
    }

    public static final String commonBase64(okio.ByteString byteString) {
        Intrinsics.checkNotNullParameter(byteString, "<this>");
        return Base64.encodeBase64$default(byteString.getData$okio(), null, 1, null);
    }

    public static final String commonBase64Url(okio.ByteString byteString) {
        Intrinsics.checkNotNullParameter(byteString, "<this>");
        return Base64.encodeBase64(byteString.getData$okio(), Base64.getBASE64_URL_SAFE());
    }

    public static final char[] getHEX_DIGIT_CHARS() {
        return HEX_DIGIT_CHARS;
    }

    public static final String commonHex(okio.ByteString byteString) {
        byte[] data$okio;
        Intrinsics.checkNotNullParameter(byteString, "<this>");
        char[] cArr = new char[byteString.getData$okio().length * 2];
        int i = 0;
        for (byte b : byteString.getData$okio()) {
            int i2 = i + 1;
            cArr[i] = getHEX_DIGIT_CHARS()[(b >> 4) & 15];
            i = i2 + 1;
            cArr[i2] = getHEX_DIGIT_CHARS()[b & 15];
        }
        return StringsKt.concatToString(cArr);
    }

    public static final okio.ByteString commonToAsciiLowercase(okio.ByteString byteString) {
        Intrinsics.checkNotNullParameter(byteString, "<this>");
        for (int i = 0; i < byteString.getData$okio().length; i++) {
            byte b = byteString.getData$okio()[i];
            if (b >= 65 && b <= 90) {
                byte[] data$okio = byteString.getData$okio();
                byte[] copyOf = Arrays.copyOf(data$okio, data$okio.length);
                Intrinsics.checkNotNullExpressionValue(copyOf, "copyOf(this, size)");
                copyOf[i] = (byte) (b + 32);
                for (int i2 = i + 1; i2 < copyOf.length; i2++) {
                    byte b2 = copyOf[i2];
                    if (b2 >= 65 && b2 <= 90) {
                        copyOf[i2] = (byte) (b2 + 32);
                    }
                }
                return new okio.ByteString(copyOf);
            }
        }
        return byteString;
    }

    public static final okio.ByteString commonToAsciiUppercase(okio.ByteString byteString) {
        Intrinsics.checkNotNullParameter(byteString, "<this>");
        for (int i = 0; i < byteString.getData$okio().length; i++) {
            byte b = byteString.getData$okio()[i];
            if (b >= 97 && b <= 122) {
                byte[] data$okio = byteString.getData$okio();
                byte[] copyOf = Arrays.copyOf(data$okio, data$okio.length);
                Intrinsics.checkNotNullExpressionValue(copyOf, "copyOf(this, size)");
                copyOf[i] = (byte) (b - 32);
                for (int i2 = i + 1; i2 < copyOf.length; i2++) {
                    byte b2 = copyOf[i2];
                    if (b2 >= 97 && b2 <= 122) {
                        copyOf[i2] = (byte) (b2 - 32);
                    }
                }
                return new okio.ByteString(copyOf);
            }
        }
        return byteString;
    }

    public static final okio.ByteString commonSubstring(okio.ByteString byteString, int i, int i2) {
        Intrinsics.checkNotNullParameter(byteString, "<this>");
        int resolveDefaultParameter = SegmentedByteString.resolveDefaultParameter(byteString, i2);
        if (!(i >= 0)) {
            throw new IllegalArgumentException("beginIndex < 0".toString());
        }
        if (resolveDefaultParameter <= byteString.getData$okio().length) {
            if (resolveDefaultParameter - i >= 0) {
                return (i == 0 && resolveDefaultParameter == byteString.getData$okio().length) ? byteString : new okio.ByteString(ArraysKt.copyOfRange(byteString.getData$okio(), i, resolveDefaultParameter));
            }
            throw new IllegalArgumentException("endIndex < beginIndex".toString());
        }
        throw new IllegalArgumentException(("endIndex > length(" + byteString.getData$okio().length + ')').toString());
    }

    public static final byte commonGetByte(okio.ByteString byteString, int i) {
        Intrinsics.checkNotNullParameter(byteString, "<this>");
        return byteString.getData$okio()[i];
    }

    public static final int commonGetSize(okio.ByteString byteString) {
        Intrinsics.checkNotNullParameter(byteString, "<this>");
        return byteString.getData$okio().length;
    }

    public static final byte[] commonToByteArray(okio.ByteString byteString) {
        Intrinsics.checkNotNullParameter(byteString, "<this>");
        byte[] data$okio = byteString.getData$okio();
        byte[] copyOf = Arrays.copyOf(data$okio, data$okio.length);
        Intrinsics.checkNotNullExpressionValue(copyOf, "copyOf(this, size)");
        return copyOf;
    }

    public static final byte[] commonInternalArray(okio.ByteString byteString) {
        Intrinsics.checkNotNullParameter(byteString, "<this>");
        return byteString.getData$okio();
    }

    public static final boolean commonRangeEquals(okio.ByteString byteString, int i, okio.ByteString other, int i2, int i3) {
        Intrinsics.checkNotNullParameter(byteString, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        return other.rangeEquals(i2, byteString.getData$okio(), i, i3);
    }

    public static final boolean commonRangeEquals(okio.ByteString byteString, int i, byte[] other, int i2, int i3) {
        Intrinsics.checkNotNullParameter(byteString, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        return i >= 0 && i <= byteString.getData$okio().length - i3 && i2 >= 0 && i2 <= other.length - i3 && SegmentedByteString.arrayRangeEquals(byteString.getData$okio(), i, other, i2, i3);
    }

    public static final void commonCopyInto(okio.ByteString byteString, int i, byte[] target, int i2, int i3) {
        Intrinsics.checkNotNullParameter(byteString, "<this>");
        Intrinsics.checkNotNullParameter(target, "target");
        ArraysKt.copyInto(byteString.getData$okio(), target, i2, i, i3 + i);
    }

    public static final boolean commonStartsWith(okio.ByteString byteString, okio.ByteString prefix) {
        Intrinsics.checkNotNullParameter(byteString, "<this>");
        Intrinsics.checkNotNullParameter(prefix, "prefix");
        return byteString.rangeEquals(0, prefix, 0, prefix.size());
    }

    public static final boolean commonStartsWith(okio.ByteString byteString, byte[] prefix) {
        Intrinsics.checkNotNullParameter(byteString, "<this>");
        Intrinsics.checkNotNullParameter(prefix, "prefix");
        return byteString.rangeEquals(0, prefix, 0, prefix.length);
    }

    public static final boolean commonEndsWith(okio.ByteString byteString, okio.ByteString suffix) {
        Intrinsics.checkNotNullParameter(byteString, "<this>");
        Intrinsics.checkNotNullParameter(suffix, "suffix");
        return byteString.rangeEquals(byteString.size() - suffix.size(), suffix, 0, suffix.size());
    }

    public static final boolean commonEndsWith(okio.ByteString byteString, byte[] suffix) {
        Intrinsics.checkNotNullParameter(byteString, "<this>");
        Intrinsics.checkNotNullParameter(suffix, "suffix");
        return byteString.rangeEquals(byteString.size() - suffix.length, suffix, 0, suffix.length);
    }

    public static final int commonIndexOf(okio.ByteString byteString, byte[] other, int i) {
        Intrinsics.checkNotNullParameter(byteString, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        int length = byteString.getData$okio().length - other.length;
        int max = Math.max(i, 0);
        if (max <= length) {
            while (!SegmentedByteString.arrayRangeEquals(byteString.getData$okio(), max, other, 0, other.length)) {
                if (max == length) {
                    return -1;
                }
                max++;
            }
            return max;
        }
        return -1;
    }

    public static final int commonLastIndexOf(okio.ByteString byteString, okio.ByteString other, int i) {
        Intrinsics.checkNotNullParameter(byteString, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        return byteString.lastIndexOf(other.internalArray$okio(), i);
    }

    public static final int commonLastIndexOf(okio.ByteString byteString, byte[] other, int i) {
        Intrinsics.checkNotNullParameter(byteString, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        for (int min = Math.min(SegmentedByteString.resolveDefaultParameter(byteString, i), byteString.getData$okio().length - other.length); -1 < min; min--) {
            if (SegmentedByteString.arrayRangeEquals(byteString.getData$okio(), min, other, 0, other.length)) {
                return min;
            }
        }
        return -1;
    }

    public static final boolean commonEquals(okio.ByteString byteString, Object obj) {
        Intrinsics.checkNotNullParameter(byteString, "<this>");
        if (obj == byteString) {
            return true;
        }
        if (obj instanceof okio.ByteString) {
            okio.ByteString byteString2 = (okio.ByteString) obj;
            if (byteString2.size() == byteString.getData$okio().length && byteString2.rangeEquals(0, byteString.getData$okio(), 0, byteString.getData$okio().length)) {
                return true;
            }
        }
        return false;
    }

    public static final int commonHashCode(okio.ByteString byteString) {
        Intrinsics.checkNotNullParameter(byteString, "<this>");
        int hashCode$okio = byteString.getHashCode$okio();
        if (hashCode$okio != 0) {
            return hashCode$okio;
        }
        int hashCode = Arrays.hashCode(byteString.getData$okio());
        byteString.setHashCode$okio(hashCode);
        return hashCode;
    }

    public static final int commonCompareTo(okio.ByteString byteString, okio.ByteString other) {
        Intrinsics.checkNotNullParameter(byteString, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        int size = byteString.size();
        int size2 = other.size();
        int min = Math.min(size, size2);
        for (int i = 0; i < min; i++) {
            int i2 = byteString.getByte(i) & UByte.MAX_VALUE;
            int i3 = other.getByte(i) & UByte.MAX_VALUE;
            if (i2 != i3) {
                return i2 < i3 ? -1 : 1;
            }
        }
        if (size == size2) {
            return 0;
        }
        return size < size2 ? -1 : 1;
    }

    public static final okio.ByteString commonOf(byte[] data) {
        Intrinsics.checkNotNullParameter(data, "data");
        byte[] copyOf = Arrays.copyOf(data, data.length);
        Intrinsics.checkNotNullExpressionValue(copyOf, "copyOf(this, size)");
        return new okio.ByteString(copyOf);
    }

    public static final okio.ByteString commonToByteString(byte[] bArr, int i, int i2) {
        Intrinsics.checkNotNullParameter(bArr, "<this>");
        int resolveDefaultParameter = SegmentedByteString.resolveDefaultParameter(bArr, i2);
        SegmentedByteString.checkOffsetAndCount(bArr.length, i, resolveDefaultParameter);
        return new okio.ByteString(ArraysKt.copyOfRange(bArr, i, resolveDefaultParameter + i));
    }

    public static final okio.ByteString commonEncodeUtf8(String str) {
        Intrinsics.checkNotNullParameter(str, "<this>");
        okio.ByteString byteString = new okio.ByteString(_JvmPlatformKt.asUtf8ToByteArray(str));
        byteString.setUtf8$okio(str);
        return byteString;
    }

    public static final okio.ByteString commonDecodeBase64(String str) {
        Intrinsics.checkNotNullParameter(str, "<this>");
        byte[] decodeBase64ToArray = Base64.decodeBase64ToArray(str);
        if (decodeBase64ToArray != null) {
            return new okio.ByteString(decodeBase64ToArray);
        }
        return null;
    }

    public static final okio.ByteString commonDecodeHex(String str) {
        Intrinsics.checkNotNullParameter(str, "<this>");
        if (!(str.length() % 2 == 0)) {
            throw new IllegalArgumentException(("Unexpected hex string: " + str).toString());
        }
        int length = str.length() / 2;
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            bArr[i] = (byte) ((decodeHexDigit(str.charAt(i2)) << 4) + decodeHexDigit(str.charAt(i2 + 1)));
        }
        return new okio.ByteString(bArr);
    }

    public static final void commonWrite(okio.ByteString byteString, Buffer buffer, int i, int i2) {
        Intrinsics.checkNotNullParameter(byteString, "<this>");
        Intrinsics.checkNotNullParameter(buffer, "buffer");
        buffer.write(byteString.getData$okio(), i, i2);
    }

    public static final int decodeHexDigit(char c) {
        boolean z = true;
        if ('0' <= c && c < ':') {
            return c - '0';
        }
        char c2 = 'a';
        if (!('a' <= c && c < 'g')) {
            c2 = 'A';
            if (!(('A' > c || c >= 'G') ? false : false)) {
                throw new IllegalArgumentException("Unexpected hex digit: " + c);
            }
        }
        return (c - c2) + 10;
    }

    public static final String commonToString(okio.ByteString byteString) {
        okio.ByteString byteString2 = byteString;
        Intrinsics.checkNotNullParameter(byteString2, "<this>");
        if (byteString.getData$okio().length == 0) {
            return "[size=0]";
        }
        int codePointIndexToCharIndex = codePointIndexToCharIndex(byteString.getData$okio(), 64);
        if (codePointIndexToCharIndex == -1) {
            if (byteString.getData$okio().length <= 64) {
                return "[hex=" + byteString.hex() + ']';
            }
            StringBuilder sb = new StringBuilder("[size=");
            sb.append(byteString.getData$okio().length);
            sb.append(" hex=");
            int resolveDefaultParameter = SegmentedByteString.resolveDefaultParameter(byteString2, 64);
            if (!(resolveDefaultParameter <= byteString.getData$okio().length)) {
                throw new IllegalArgumentException(("endIndex > length(" + byteString.getData$okio().length + ')').toString());
            }
            if (!(resolveDefaultParameter + 0 >= 0)) {
                throw new IllegalArgumentException("endIndex < beginIndex".toString());
            }
            if (resolveDefaultParameter != byteString.getData$okio().length) {
                byteString2 = new okio.ByteString(ArraysKt.copyOfRange(byteString.getData$okio(), 0, resolveDefaultParameter));
            }
            sb.append(byteString2.hex());
            sb.append("…]");
            return sb.toString();
        }
        String utf8 = byteString.utf8();
        String substring = utf8.substring(0, codePointIndexToCharIndex);
        Intrinsics.checkNotNullExpressionValue(substring, "this as java.lang.String…ing(startIndex, endIndex)");
        String replace$default = StringsKt.replace$default(StringsKt.replace$default(StringsKt.replace$default(substring, "\\", "\\\\", false, 4, (Object) null), "\n", "\\n", false, 4, (Object) null), "\r", "\\r", false, 4, (Object) null);
        if (codePointIndexToCharIndex < utf8.length()) {
            return "[size=" + byteString.getData$okio().length + " text=" + replace$default + "…]";
        }
        return "[text=" + replace$default + ']';
    }

    /* JADX WARN: Removed duplicated region for block: B:553:0x0044 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:556:0x007a A[EDGE_INSN: B:556:0x007a->B:350:0x007a ?: BREAK  , SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:563:0x0159 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:576:0x00ce A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:581:0x01fc A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final int codePointIndexToCharIndex(byte[] bArr, int i) {
        byte b;
        boolean z;
        boolean z2;
        int i2;
        boolean z3;
        boolean z4;
        boolean z5;
        int length = bArr.length;
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        loop0: while (i3 < length) {
            byte b2 = bArr[i3];
            if (b2 >= 0) {
                int i6 = i5 + 1;
                if (i5 == i) {
                    return i4;
                }
                if (b2 != 10 && b2 != 13) {
                    if (!(b2 >= 0 && b2 < 32)) {
                        if (!(Byte.MAX_VALUE <= b2 && b2 < 160)) {
                            z2 = false;
                            if (z2) {
                                return -1;
                            }
                        }
                    }
                    z2 = true;
                    if (z2) {
                    }
                }
                if (b2 == 65533) {
                    return -1;
                }
                i4 += b2 < 65536 ? 1 : 2;
                i3++;
                while (true) {
                    i5 = i6;
                    if (i3 < length && (b = bArr[i3]) >= 0) {
                        i3++;
                        i6 = i5 + 1;
                        if (i5 == i) {
                            return i4;
                        }
                        if (b != 10 && b != 13) {
                            if (!(b >= 0 && b < 32)) {
                                if (!(Byte.MAX_VALUE <= b && b < 160)) {
                                    z = false;
                                    if (z) {
                                        break loop0;
                                    }
                                }
                            }
                            z = true;
                            if (z) {
                            }
                        }
                        if (b == 65533) {
                            break loop0;
                        }
                        i4 += b < 65536 ? 1 : 2;
                    }
                }
                return -1;
            }
            if ((b2 >> 5) == -2) {
                int i7 = i3 + 1;
                if (length <= i7) {
                    if (i5 == i) {
                        return i4;
                    }
                    return -1;
                }
                byte b3 = bArr[i7];
                if (!((b3 & 192) == 128)) {
                    if (i5 == i) {
                        return i4;
                    }
                    return -1;
                }
                int i8 = (b3 ^ ByteCompanionObject.MIN_VALUE) ^ (b2 << 6);
                if (i8 < 128) {
                    if (i5 == i) {
                        return i4;
                    }
                    return -1;
                }
                i2 = i5 + 1;
                if (i5 == i) {
                    return i4;
                }
                if (i8 != 10 && i8 != 13) {
                    if (!(i8 >= 0 && i8 < 32)) {
                        if (!(127 <= i8 && i8 < 160)) {
                            z5 = false;
                            if (z5) {
                                return -1;
                            }
                        }
                    }
                    z5 = true;
                    if (z5) {
                    }
                }
                if (i8 == 65533) {
                    return -1;
                }
                i4 += i8 < 65536 ? 1 : 2;
                Unit unit = Unit.INSTANCE;
                i3 += 2;
            } else if ((b2 >> 4) == -2) {
                int i9 = i3 + 2;
                if (length <= i9) {
                    if (i5 == i) {
                        return i4;
                    }
                    return -1;
                }
                byte b4 = bArr[i3 + 1];
                if (!((b4 & 192) == 128)) {
                    if (i5 == i) {
                        return i4;
                    }
                    return -1;
                }
                byte b5 = bArr[i9];
                if (!((b5 & 192) == 128)) {
                    if (i5 == i) {
                        return i4;
                    }
                    return -1;
                }
                int i10 = ((b5 ^ ByteCompanionObject.MIN_VALUE) ^ (b4 << 6)) ^ (b2 << 12);
                if (i10 < 2048) {
                    if (i5 == i) {
                        return i4;
                    }
                    return -1;
                }
                if (55296 <= i10 && i10 < 57344) {
                    if (i5 == i) {
                        return i4;
                    }
                    return -1;
                }
                i2 = i5 + 1;
                if (i5 == i) {
                    return i4;
                }
                if (i10 != 10 && i10 != 13) {
                    if (!(i10 >= 0 && i10 < 32)) {
                        if (!(127 <= i10 && i10 < 160)) {
                            z4 = false;
                            if (z4) {
                                return -1;
                            }
                        }
                    }
                    z4 = true;
                    if (z4) {
                    }
                }
                if (i10 == 65533) {
                    return -1;
                }
                i4 += i10 < 65536 ? 1 : 2;
                Unit unit2 = Unit.INSTANCE;
                i3 += 3;
            } else if ((b2 >> 3) != -2) {
                if (i5 == i) {
                    return i4;
                }
                return -1;
            } else {
                int i11 = i3 + 3;
                if (length <= i11) {
                    if (i5 == i) {
                        return i4;
                    }
                    return -1;
                }
                byte b6 = bArr[i3 + 1];
                if (!((b6 & 192) == 128)) {
                    if (i5 == i) {
                        return i4;
                    }
                    return -1;
                }
                byte b7 = bArr[i3 + 2];
                if (!((b7 & 192) == 128)) {
                    if (i5 == i) {
                        return i4;
                    }
                    return -1;
                }
                byte b8 = bArr[i11];
                if (!((b8 & 192) == 128)) {
                    if (i5 == i) {
                        return i4;
                    }
                    return -1;
                }
                int i12 = (((b8 ^ ByteCompanionObject.MIN_VALUE) ^ (b7 << 6)) ^ (b6 << 12)) ^ (b2 << 18);
                if (i12 > 1114111) {
                    if (i5 == i) {
                        return i4;
                    }
                    return -1;
                }
                if (55296 <= i12 && i12 < 57344) {
                    if (i5 == i) {
                        return i4;
                    }
                    return -1;
                } else if (i12 < 65536) {
                    if (i5 == i) {
                        return i4;
                    }
                    return -1;
                } else {
                    i2 = i5 + 1;
                    if (i5 == i) {
                        return i4;
                    }
                    if (i12 != 10 && i12 != 13) {
                        if (!(i12 >= 0 && i12 < 32)) {
                            if (!(127 <= i12 && i12 < 160)) {
                                z3 = false;
                                if (z3) {
                                    return -1;
                                }
                            }
                        }
                        z3 = true;
                        if (z3) {
                        }
                    }
                    if (i12 == 65533) {
                        return -1;
                    }
                    i4 += i12 < 65536 ? 1 : 2;
                    Unit unit3 = Unit.INSTANCE;
                    i3 += 4;
                }
            }
            i5 = i2;
        }
        return i4;
    }
}
