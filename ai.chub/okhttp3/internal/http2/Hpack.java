package okhttp3.internal.http2;

import androidx.browser.trusted.sharing.ShareTarget;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.openid.appauth.AuthorizationManagementUtil;
import okhttp3.HttpUrl;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;
import okio.Source;
/* compiled from: Hpack.kt */
@Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\t\bÆ\u0002\u0018\u00002\u00020\u0001:\u0002\u0018\u0019B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u000e\u0010\u0015\u001a\u00020\u00052\u0006\u0010\u0016\u001a\u00020\u0005J\u0014\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004H\u0002R\u001d\u0010\u0003\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u000e\u0010\t\u001a\u00020\u0006X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0006X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0006X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0006X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0006X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0006X\u0082T¢\u0006\u0002\n\u0000R\u0019\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00110\u0010¢\u0006\n\n\u0002\u0010\u0014\u001a\u0004\b\u0012\u0010\u0013¨\u0006\u001a"}, d2 = {"Lokhttp3/internal/http2/Hpack;", HttpUrl.FRAGMENT_ENCODE_SET, "()V", "NAME_TO_FIRST_INDEX", HttpUrl.FRAGMENT_ENCODE_SET, "Lokio/ByteString;", HttpUrl.FRAGMENT_ENCODE_SET, "getNAME_TO_FIRST_INDEX", "()Ljava/util/Map;", "PREFIX_4_BITS", "PREFIX_5_BITS", "PREFIX_6_BITS", "PREFIX_7_BITS", "SETTINGS_HEADER_TABLE_SIZE", "SETTINGS_HEADER_TABLE_SIZE_LIMIT", "STATIC_HEADER_TABLE", HttpUrl.FRAGMENT_ENCODE_SET, "Lokhttp3/internal/http2/Header;", "getSTATIC_HEADER_TABLE", "()[Lokhttp3/internal/http2/Header;", "[Lokhttp3/internal/http2/Header;", "checkLowercase", "name", "nameToFirstIndex", "Reader", "Writer", "okhttp"}, k = 1, mv = {1, 8, 0}, xi = 48)
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public final class Hpack {
    public static final Hpack INSTANCE;
    private static final Map<ByteString, Integer> NAME_TO_FIRST_INDEX;
    private static final int PREFIX_4_BITS = 15;
    private static final int PREFIX_5_BITS = 31;
    private static final int PREFIX_6_BITS = 63;
    private static final int PREFIX_7_BITS = 127;
    private static final int SETTINGS_HEADER_TABLE_SIZE = 4096;
    private static final int SETTINGS_HEADER_TABLE_SIZE_LIMIT = 16384;
    private static final Header[] STATIC_HEADER_TABLE;

    private Hpack() {
    }

    public final Header[] getSTATIC_HEADER_TABLE() {
        return STATIC_HEADER_TABLE;
    }

    static {
        Hpack hpack = new Hpack();
        INSTANCE = hpack;
        STATIC_HEADER_TABLE = new Header[]{new Header(Header.TARGET_AUTHORITY, HttpUrl.FRAGMENT_ENCODE_SET), new Header(Header.TARGET_METHOD, ShareTarget.METHOD_GET), new Header(Header.TARGET_METHOD, ShareTarget.METHOD_POST), new Header(Header.TARGET_PATH, "/"), new Header(Header.TARGET_PATH, "/index.html"), new Header(Header.TARGET_SCHEME, "http"), new Header(Header.TARGET_SCHEME, "https"), new Header(Header.RESPONSE_STATUS, "200"), new Header(Header.RESPONSE_STATUS, "204"), new Header(Header.RESPONSE_STATUS, "206"), new Header(Header.RESPONSE_STATUS, "304"), new Header(Header.RESPONSE_STATUS, "400"), new Header(Header.RESPONSE_STATUS, "404"), new Header(Header.RESPONSE_STATUS, "500"), new Header("accept-charset", HttpUrl.FRAGMENT_ENCODE_SET), new Header("accept-encoding", "gzip, deflate"), new Header("accept-language", HttpUrl.FRAGMENT_ENCODE_SET), new Header("accept-ranges", HttpUrl.FRAGMENT_ENCODE_SET), new Header("accept", HttpUrl.FRAGMENT_ENCODE_SET), new Header("access-control-allow-origin", HttpUrl.FRAGMENT_ENCODE_SET), new Header("age", HttpUrl.FRAGMENT_ENCODE_SET), new Header("allow", HttpUrl.FRAGMENT_ENCODE_SET), new Header(AuthorizationManagementUtil.REQUEST_TYPE_AUTHORIZATION, HttpUrl.FRAGMENT_ENCODE_SET), new Header("cache-control", HttpUrl.FRAGMENT_ENCODE_SET), new Header("content-disposition", HttpUrl.FRAGMENT_ENCODE_SET), new Header("content-encoding", HttpUrl.FRAGMENT_ENCODE_SET), new Header("content-language", HttpUrl.FRAGMENT_ENCODE_SET), new Header("content-length", HttpUrl.FRAGMENT_ENCODE_SET), new Header("content-location", HttpUrl.FRAGMENT_ENCODE_SET), new Header("content-range", HttpUrl.FRAGMENT_ENCODE_SET), new Header("content-type", HttpUrl.FRAGMENT_ENCODE_SET), new Header("cookie", HttpUrl.FRAGMENT_ENCODE_SET), new Header("date", HttpUrl.FRAGMENT_ENCODE_SET), new Header("etag", HttpUrl.FRAGMENT_ENCODE_SET), new Header("expect", HttpUrl.FRAGMENT_ENCODE_SET), new Header("expires", HttpUrl.FRAGMENT_ENCODE_SET), new Header("from", HttpUrl.FRAGMENT_ENCODE_SET), new Header("host", HttpUrl.FRAGMENT_ENCODE_SET), new Header("if-match", HttpUrl.FRAGMENT_ENCODE_SET), new Header("if-modified-since", HttpUrl.FRAGMENT_ENCODE_SET), new Header("if-none-match", HttpUrl.FRAGMENT_ENCODE_SET), new Header("if-range", HttpUrl.FRAGMENT_ENCODE_SET), new Header("if-unmodified-since", HttpUrl.FRAGMENT_ENCODE_SET), new Header("last-modified", HttpUrl.FRAGMENT_ENCODE_SET), new Header("link", HttpUrl.FRAGMENT_ENCODE_SET), new Header("location", HttpUrl.FRAGMENT_ENCODE_SET), new Header("max-forwards", HttpUrl.FRAGMENT_ENCODE_SET), new Header("proxy-authenticate", HttpUrl.FRAGMENT_ENCODE_SET), new Header("proxy-authorization", HttpUrl.FRAGMENT_ENCODE_SET), new Header("range", HttpUrl.FRAGMENT_ENCODE_SET), new Header("referer", HttpUrl.FRAGMENT_ENCODE_SET), new Header("refresh", HttpUrl.FRAGMENT_ENCODE_SET), new Header("retry-after", HttpUrl.FRAGMENT_ENCODE_SET), new Header("server", HttpUrl.FRAGMENT_ENCODE_SET), new Header("set-cookie", HttpUrl.FRAGMENT_ENCODE_SET), new Header("strict-transport-security", HttpUrl.FRAGMENT_ENCODE_SET), new Header("transfer-encoding", HttpUrl.FRAGMENT_ENCODE_SET), new Header("user-agent", HttpUrl.FRAGMENT_ENCODE_SET), new Header("vary", HttpUrl.FRAGMENT_ENCODE_SET), new Header("via", HttpUrl.FRAGMENT_ENCODE_SET), new Header("www-authenticate", HttpUrl.FRAGMENT_ENCODE_SET)};
        NAME_TO_FIRST_INDEX = hpack.nameToFirstIndex();
    }

    public final Map<ByteString, Integer> getNAME_TO_FIRST_INDEX() {
        return NAME_TO_FIRST_INDEX;
    }

    /* compiled from: Hpack.kt */
    @Metadata(d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010!\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\r\u0018\u00002\u00020\u0001B!\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0005¢\u0006\u0002\u0010\u0007J\b\u0010\u0012\u001a\u00020\u0013H\u0002J\b\u0010\u0014\u001a\u00020\u0013H\u0002J\u0010\u0010\u0015\u001a\u00020\u00052\u0006\u0010\u0016\u001a\u00020\u0005H\u0002J\u0010\u0010\u0017\u001a\u00020\u00052\u0006\u0010\u0018\u001a\u00020\u0005H\u0002J\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\n0\u001aJ\u0010\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u0016\u001a\u00020\u0005H\u0002J\u0018\u0010\u001d\u001a\u00020\u00132\u0006\u0010\u0016\u001a\u00020\u00052\u0006\u0010\u001e\u001a\u00020\nH\u0002J\u0010\u0010\u001f\u001a\u00020 2\u0006\u0010\u0016\u001a\u00020\u0005H\u0002J\u0006\u0010\u0006\u001a\u00020\u0005J\b\u0010!\u001a\u00020\u0005H\u0002J\u0006\u0010\"\u001a\u00020\u001cJ\u0006\u0010#\u001a\u00020\u0013J\u0010\u0010$\u001a\u00020\u00132\u0006\u0010\u0016\u001a\u00020\u0005H\u0002J\u0016\u0010%\u001a\u00020\u00052\u0006\u0010&\u001a\u00020\u00052\u0006\u0010'\u001a\u00020\u0005J\u0010\u0010(\u001a\u00020\u00132\u0006\u0010)\u001a\u00020\u0005H\u0002J\b\u0010*\u001a\u00020\u0013H\u0002J\u0010\u0010+\u001a\u00020\u00132\u0006\u0010\u0016\u001a\u00020\u0005H\u0002J\b\u0010,\u001a\u00020\u0013H\u0002R\u001c\u0010\b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\t8\u0006@\u0006X\u0087\u000e¢\u0006\u0004\n\u0002\u0010\u000bR\u0012\u0010\f\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\r\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\n0\u000fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0011X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006-"}, d2 = {"Lokhttp3/internal/http2/Hpack$Reader;", HttpUrl.FRAGMENT_ENCODE_SET, "source", "Lokio/Source;", "headerTableSizeSetting", HttpUrl.FRAGMENT_ENCODE_SET, "maxDynamicTableByteCount", "(Lokio/Source;II)V", "dynamicTable", HttpUrl.FRAGMENT_ENCODE_SET, "Lokhttp3/internal/http2/Header;", "[Lokhttp3/internal/http2/Header;", "dynamicTableByteCount", "headerCount", "headerList", HttpUrl.FRAGMENT_ENCODE_SET, "nextHeaderIndex", "Lokio/BufferedSource;", "adjustDynamicTableByteCount", HttpUrl.FRAGMENT_ENCODE_SET, "clearDynamicTable", "dynamicTableIndex", "index", "evictToRecoverBytes", "bytesToRecover", "getAndResetHeaderList", HttpUrl.FRAGMENT_ENCODE_SET, "getName", "Lokio/ByteString;", "insertIntoDynamicTable", "entry", "isStaticHeader", HttpUrl.FRAGMENT_ENCODE_SET, "readByte", "readByteString", "readHeaders", "readIndexedHeader", "readInt", "firstByte", "prefixMask", "readLiteralHeaderWithIncrementalIndexingIndexedName", "nameIndex", "readLiteralHeaderWithIncrementalIndexingNewName", "readLiteralHeaderWithoutIndexingIndexedName", "readLiteralHeaderWithoutIndexingNewName", "okhttp"}, k = 1, mv = {1, 8, 0}, xi = 48)
    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static final class Reader {
        public Header[] dynamicTable;
        public int dynamicTableByteCount;
        public int headerCount;
        private final List<Header> headerList;
        private final int headerTableSizeSetting;
        private int maxDynamicTableByteCount;
        private int nextHeaderIndex;
        private final BufferedSource source;

        /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
        public Reader(Source source, int i) {
            this(source, i, 0, 4, null);
            Intrinsics.checkNotNullParameter(source, "source");
        }

        public Reader(Source source, int i, int i2) {
            Intrinsics.checkNotNullParameter(source, "source");
            this.headerTableSizeSetting = i;
            this.maxDynamicTableByteCount = i2;
            this.headerList = new ArrayList();
            this.source = Okio.buffer(source);
            Header[] headerArr = new Header[8];
            this.dynamicTable = headerArr;
            this.nextHeaderIndex = headerArr.length - 1;
        }

        public /* synthetic */ Reader(Source source, int i, int i2, int i3, DefaultConstructorMarker defaultConstructorMarker) {
            this(source, i, (i3 & 4) != 0 ? i : i2);
        }

        public final List<Header> getAndResetHeaderList() {
            List<Header> list = CollectionsKt.toList(this.headerList);
            this.headerList.clear();
            return list;
        }

        public final int maxDynamicTableByteCount() {
            return this.maxDynamicTableByteCount;
        }

        private final void adjustDynamicTableByteCount() {
            int i = this.maxDynamicTableByteCount;
            int i2 = this.dynamicTableByteCount;
            if (i < i2) {
                if (i == 0) {
                    clearDynamicTable();
                } else {
                    evictToRecoverBytes(i2 - i);
                }
            }
        }

        private final void clearDynamicTable() {
            ArraysKt.fill$default(this.dynamicTable, (Object) null, 0, 0, 6, (Object) null);
            this.nextHeaderIndex = this.dynamicTable.length - 1;
            this.headerCount = 0;
            this.dynamicTableByteCount = 0;
        }

        private final int evictToRecoverBytes(int i) {
            int i2;
            int i3 = 0;
            if (i > 0) {
                int length = this.dynamicTable.length;
                while (true) {
                    length--;
                    i2 = this.nextHeaderIndex;
                    if (length < i2 || i <= 0) {
                        break;
                    }
                    Header header = this.dynamicTable[length];
                    Intrinsics.checkNotNull(header);
                    i -= header.hpackSize;
                    this.dynamicTableByteCount -= header.hpackSize;
                    this.headerCount--;
                    i3++;
                }
                Header[] headerArr = this.dynamicTable;
                System.arraycopy(headerArr, i2 + 1, headerArr, i2 + 1 + i3, this.headerCount);
                this.nextHeaderIndex += i3;
            }
            return i3;
        }

        public final void readHeaders() throws IOException {
            while (!this.source.exhausted()) {
                int and = Util.and(this.source.readByte(), 255);
                if (and == 128) {
                    throw new IOException("index == 0");
                }
                if ((and & 128) == 128) {
                    readIndexedHeader(readInt(and, 127) - 1);
                } else if (and == 64) {
                    readLiteralHeaderWithIncrementalIndexingNewName();
                } else if ((and & 64) == 64) {
                    readLiteralHeaderWithIncrementalIndexingIndexedName(readInt(and, 63) - 1);
                } else if ((and & 32) == 32) {
                    int readInt = readInt(and, Hpack.PREFIX_5_BITS);
                    this.maxDynamicTableByteCount = readInt;
                    if (readInt < 0 || readInt > this.headerTableSizeSetting) {
                        throw new IOException("Invalid dynamic table size update " + this.maxDynamicTableByteCount);
                    }
                    adjustDynamicTableByteCount();
                } else if (and == 16 || and == 0) {
                    readLiteralHeaderWithoutIndexingNewName();
                } else {
                    readLiteralHeaderWithoutIndexingIndexedName(readInt(and, 15) - 1);
                }
            }
        }

        private final void readIndexedHeader(int i) throws IOException {
            if (isStaticHeader(i)) {
                this.headerList.add(Hpack.INSTANCE.getSTATIC_HEADER_TABLE()[i]);
                return;
            }
            int dynamicTableIndex = dynamicTableIndex(i - Hpack.INSTANCE.getSTATIC_HEADER_TABLE().length);
            if (dynamicTableIndex >= 0) {
                Header[] headerArr = this.dynamicTable;
                if (dynamicTableIndex < headerArr.length) {
                    Header header = headerArr[dynamicTableIndex];
                    Intrinsics.checkNotNull(header);
                    this.headerList.add(header);
                    return;
                }
            }
            throw new IOException("Header index too large " + (i + 1));
        }

        private final int dynamicTableIndex(int i) {
            return this.nextHeaderIndex + 1 + i;
        }

        private final void readLiteralHeaderWithoutIndexingIndexedName(int i) throws IOException {
            this.headerList.add(new Header(getName(i), readByteString()));
        }

        private final void readLiteralHeaderWithoutIndexingNewName() throws IOException {
            this.headerList.add(new Header(Hpack.INSTANCE.checkLowercase(readByteString()), readByteString()));
        }

        private final void readLiteralHeaderWithIncrementalIndexingIndexedName(int i) throws IOException {
            insertIntoDynamicTable(-1, new Header(getName(i), readByteString()));
        }

        private final void readLiteralHeaderWithIncrementalIndexingNewName() throws IOException {
            insertIntoDynamicTable(-1, new Header(Hpack.INSTANCE.checkLowercase(readByteString()), readByteString()));
        }

        private final ByteString getName(int i) throws IOException {
            if (isStaticHeader(i)) {
                return Hpack.INSTANCE.getSTATIC_HEADER_TABLE()[i].name;
            }
            int dynamicTableIndex = dynamicTableIndex(i - Hpack.INSTANCE.getSTATIC_HEADER_TABLE().length);
            if (dynamicTableIndex >= 0) {
                Header[] headerArr = this.dynamicTable;
                if (dynamicTableIndex < headerArr.length) {
                    Header header = headerArr[dynamicTableIndex];
                    Intrinsics.checkNotNull(header);
                    return header.name;
                }
            }
            throw new IOException("Header index too large " + (i + 1));
        }

        private final boolean isStaticHeader(int i) {
            return i >= 0 && i <= Hpack.INSTANCE.getSTATIC_HEADER_TABLE().length - 1;
        }

        private final void insertIntoDynamicTable(int i, Header header) {
            this.headerList.add(header);
            int i2 = header.hpackSize;
            if (i != -1) {
                Header header2 = this.dynamicTable[dynamicTableIndex(i)];
                Intrinsics.checkNotNull(header2);
                i2 -= header2.hpackSize;
            }
            int i3 = this.maxDynamicTableByteCount;
            if (i2 > i3) {
                clearDynamicTable();
                return;
            }
            int evictToRecoverBytes = evictToRecoverBytes((this.dynamicTableByteCount + i2) - i3);
            if (i == -1) {
                int i4 = this.headerCount + 1;
                Header[] headerArr = this.dynamicTable;
                if (i4 > headerArr.length) {
                    Header[] headerArr2 = new Header[headerArr.length * 2];
                    System.arraycopy(headerArr, 0, headerArr2, headerArr.length, headerArr.length);
                    this.nextHeaderIndex = this.dynamicTable.length - 1;
                    this.dynamicTable = headerArr2;
                }
                int i5 = this.nextHeaderIndex;
                this.nextHeaderIndex = i5 - 1;
                this.dynamicTable[i5] = header;
                this.headerCount++;
            } else {
                this.dynamicTable[i + dynamicTableIndex(i) + evictToRecoverBytes] = header;
            }
            this.dynamicTableByteCount += i2;
        }

        private final int readByte() throws IOException {
            return Util.and(this.source.readByte(), 255);
        }

        public final int readInt(int i, int i2) throws IOException {
            int i3 = i & i2;
            if (i3 < i2) {
                return i3;
            }
            int i4 = 0;
            while (true) {
                int readByte = readByte();
                if ((readByte & 128) == 0) {
                    return i2 + (readByte << i4);
                }
                i2 += (readByte & 127) << i4;
                i4 += 7;
            }
        }

        public final ByteString readByteString() throws IOException {
            int readByte = readByte();
            boolean z = (readByte & 128) == 128;
            long readInt = readInt(readByte, 127);
            if (z) {
                Buffer buffer = new Buffer();
                Huffman.INSTANCE.decode(this.source, readInt, buffer);
                return buffer.readByteString();
            }
            return this.source.readByteString(readInt);
        }
    }

    private final Map<ByteString, Integer> nameToFirstIndex() {
        Header[] headerArr = STATIC_HEADER_TABLE;
        LinkedHashMap linkedHashMap = new LinkedHashMap(headerArr.length);
        int length = headerArr.length;
        for (int i = 0; i < length; i++) {
            Header[] headerArr2 = STATIC_HEADER_TABLE;
            if (!linkedHashMap.containsKey(headerArr2[i].name)) {
                linkedHashMap.put(headerArr2[i].name, Integer.valueOf(i));
            }
        }
        Map<ByteString, Integer> unmodifiableMap = Collections.unmodifiableMap(linkedHashMap);
        Intrinsics.checkNotNullExpressionValue(unmodifiableMap, "unmodifiableMap(result)");
        return unmodifiableMap;
    }

    /* compiled from: Hpack.kt */
    @Metadata(d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0005\u0018\u00002\u00020\u0001B#\b\u0007\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0002\u0010\bJ\b\u0010\u0013\u001a\u00020\u0014H\u0002J\b\u0010\u0015\u001a\u00020\u0014H\u0002J\u0010\u0010\u0016\u001a\u00020\u00032\u0006\u0010\u0017\u001a\u00020\u0003H\u0002J\u0010\u0010\u0018\u001a\u00020\u00142\u0006\u0010\u0019\u001a\u00020\u000bH\u0002J\u000e\u0010\u001a\u001a\u00020\u00142\u0006\u0010\u0002\u001a\u00020\u0003J\u000e\u0010\u001b\u001a\u00020\u00142\u0006\u0010\u001c\u001a\u00020\u001dJ\u0014\u0010\u001e\u001a\u00020\u00142\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u000b0 J\u001e\u0010!\u001a\u00020\u00142\u0006\u0010\"\u001a\u00020\u00032\u0006\u0010#\u001a\u00020\u00032\u0006\u0010$\u001a\u00020\u0003R\u001c\u0010\t\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000b0\n8\u0006@\u0006X\u0087\u000e¢\u0006\u0004\n\u0002\u0010\fR\u0012\u0010\r\u001a\u00020\u00038\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\u000f\u001a\u00020\u00038\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\u0002\u001a\u00020\u00038\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\u0010\u001a\u00020\u00038\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0003X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0003X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006%"}, d2 = {"Lokhttp3/internal/http2/Hpack$Writer;", HttpUrl.FRAGMENT_ENCODE_SET, "headerTableSizeSetting", HttpUrl.FRAGMENT_ENCODE_SET, "useCompression", HttpUrl.FRAGMENT_ENCODE_SET, "out", "Lokio/Buffer;", "(IZLokio/Buffer;)V", "dynamicTable", HttpUrl.FRAGMENT_ENCODE_SET, "Lokhttp3/internal/http2/Header;", "[Lokhttp3/internal/http2/Header;", "dynamicTableByteCount", "emitDynamicTableSizeUpdate", "headerCount", "maxDynamicTableByteCount", "nextHeaderIndex", "smallestHeaderTableSizeSetting", "adjustDynamicTableByteCount", HttpUrl.FRAGMENT_ENCODE_SET, "clearDynamicTable", "evictToRecoverBytes", "bytesToRecover", "insertIntoDynamicTable", "entry", "resizeHeaderTable", "writeByteString", "data", "Lokio/ByteString;", "writeHeaders", "headerBlock", HttpUrl.FRAGMENT_ENCODE_SET, "writeInt", "value", "prefixMask", "bits", "okhttp"}, k = 1, mv = {1, 8, 0}, xi = 48)
    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static final class Writer {
        public Header[] dynamicTable;
        public int dynamicTableByteCount;
        private boolean emitDynamicTableSizeUpdate;
        public int headerCount;
        public int headerTableSizeSetting;
        public int maxDynamicTableByteCount;
        private int nextHeaderIndex;
        private final Buffer out;
        private int smallestHeaderTableSizeSetting;
        private final boolean useCompression;

        /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
        public Writer(int i, Buffer out) {
            this(i, false, out, 2, null);
            Intrinsics.checkNotNullParameter(out, "out");
        }

        /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
        public Writer(Buffer out) {
            this(0, false, out, 3, null);
            Intrinsics.checkNotNullParameter(out, "out");
        }

        public Writer(int i, boolean z, Buffer out) {
            Intrinsics.checkNotNullParameter(out, "out");
            this.headerTableSizeSetting = i;
            this.useCompression = z;
            this.out = out;
            this.smallestHeaderTableSizeSetting = Integer.MAX_VALUE;
            this.maxDynamicTableByteCount = i;
            Header[] headerArr = new Header[8];
            this.dynamicTable = headerArr;
            this.nextHeaderIndex = headerArr.length - 1;
        }

        public /* synthetic */ Writer(int i, boolean z, Buffer buffer, int i2, DefaultConstructorMarker defaultConstructorMarker) {
            this((i2 & 1) != 0 ? 4096 : i, (i2 & 2) != 0 ? true : z, buffer);
        }

        private final void clearDynamicTable() {
            ArraysKt.fill$default(this.dynamicTable, (Object) null, 0, 0, 6, (Object) null);
            this.nextHeaderIndex = this.dynamicTable.length - 1;
            this.headerCount = 0;
            this.dynamicTableByteCount = 0;
        }

        private final int evictToRecoverBytes(int i) {
            int i2;
            int i3 = 0;
            if (i > 0) {
                int length = this.dynamicTable.length;
                while (true) {
                    length--;
                    i2 = this.nextHeaderIndex;
                    if (length < i2 || i <= 0) {
                        break;
                    }
                    Header header = this.dynamicTable[length];
                    Intrinsics.checkNotNull(header);
                    i -= header.hpackSize;
                    int i4 = this.dynamicTableByteCount;
                    Header header2 = this.dynamicTable[length];
                    Intrinsics.checkNotNull(header2);
                    this.dynamicTableByteCount = i4 - header2.hpackSize;
                    this.headerCount--;
                    i3++;
                }
                Header[] headerArr = this.dynamicTable;
                System.arraycopy(headerArr, i2 + 1, headerArr, i2 + 1 + i3, this.headerCount);
                Header[] headerArr2 = this.dynamicTable;
                int i5 = this.nextHeaderIndex;
                Arrays.fill(headerArr2, i5 + 1, i5 + 1 + i3, (Object) null);
                this.nextHeaderIndex += i3;
            }
            return i3;
        }

        private final void insertIntoDynamicTable(Header header) {
            int i = header.hpackSize;
            int i2 = this.maxDynamicTableByteCount;
            if (i > i2) {
                clearDynamicTable();
                return;
            }
            evictToRecoverBytes((this.dynamicTableByteCount + i) - i2);
            int i3 = this.headerCount + 1;
            Header[] headerArr = this.dynamicTable;
            if (i3 > headerArr.length) {
                Header[] headerArr2 = new Header[headerArr.length * 2];
                System.arraycopy(headerArr, 0, headerArr2, headerArr.length, headerArr.length);
                this.nextHeaderIndex = this.dynamicTable.length - 1;
                this.dynamicTable = headerArr2;
            }
            int i4 = this.nextHeaderIndex;
            this.nextHeaderIndex = i4 - 1;
            this.dynamicTable[i4] = header;
            this.headerCount++;
            this.dynamicTableByteCount += i;
        }

        public final void writeHeaders(List<Header> headerBlock) throws IOException {
            int i;
            int i2;
            Intrinsics.checkNotNullParameter(headerBlock, "headerBlock");
            if (this.emitDynamicTableSizeUpdate) {
                int i3 = this.smallestHeaderTableSizeSetting;
                if (i3 < this.maxDynamicTableByteCount) {
                    writeInt(i3, Hpack.PREFIX_5_BITS, 32);
                }
                this.emitDynamicTableSizeUpdate = false;
                this.smallestHeaderTableSizeSetting = Integer.MAX_VALUE;
                writeInt(this.maxDynamicTableByteCount, Hpack.PREFIX_5_BITS, 32);
            }
            int size = headerBlock.size();
            for (int i4 = 0; i4 < size; i4++) {
                Header header = headerBlock.get(i4);
                ByteString asciiLowercase = header.name.toAsciiLowercase();
                ByteString byteString = header.value;
                Integer num = Hpack.INSTANCE.getNAME_TO_FIRST_INDEX().get(asciiLowercase);
                if (num != null) {
                    i = num.intValue() + 1;
                    if (2 <= i && i < 8) {
                        if (Intrinsics.areEqual(Hpack.INSTANCE.getSTATIC_HEADER_TABLE()[i - 1].value, byteString)) {
                            i2 = i;
                        } else if (Intrinsics.areEqual(Hpack.INSTANCE.getSTATIC_HEADER_TABLE()[i].value, byteString)) {
                            i2 = i;
                            i++;
                        }
                    }
                    i2 = i;
                    i = -1;
                } else {
                    i = -1;
                    i2 = -1;
                }
                if (i == -1) {
                    int i5 = this.nextHeaderIndex + 1;
                    int length = this.dynamicTable.length;
                    while (true) {
                        if (i5 >= length) {
                            break;
                        }
                        Header header2 = this.dynamicTable[i5];
                        Intrinsics.checkNotNull(header2);
                        if (Intrinsics.areEqual(header2.name, asciiLowercase)) {
                            Header header3 = this.dynamicTable[i5];
                            Intrinsics.checkNotNull(header3);
                            if (Intrinsics.areEqual(header3.value, byteString)) {
                                i = Hpack.INSTANCE.getSTATIC_HEADER_TABLE().length + (i5 - this.nextHeaderIndex);
                                break;
                            } else if (i2 == -1) {
                                i2 = (i5 - this.nextHeaderIndex) + Hpack.INSTANCE.getSTATIC_HEADER_TABLE().length;
                            }
                        }
                        i5++;
                    }
                }
                if (i != -1) {
                    writeInt(i, 127, 128);
                } else if (i2 == -1) {
                    this.out.writeByte(64);
                    writeByteString(asciiLowercase);
                    writeByteString(byteString);
                    insertIntoDynamicTable(header);
                } else if (asciiLowercase.startsWith(Header.PSEUDO_PREFIX) && !Intrinsics.areEqual(Header.TARGET_AUTHORITY, asciiLowercase)) {
                    writeInt(i2, 15, 0);
                    writeByteString(byteString);
                } else {
                    writeInt(i2, 63, 64);
                    writeByteString(byteString);
                    insertIntoDynamicTable(header);
                }
            }
        }

        public final void writeInt(int i, int i2, int i3) {
            if (i < i2) {
                this.out.writeByte(i | i3);
                return;
            }
            this.out.writeByte(i3 | i2);
            int i4 = i - i2;
            while (i4 >= 128) {
                this.out.writeByte(128 | (i4 & 127));
                i4 >>>= 7;
            }
            this.out.writeByte(i4);
        }

        public final void writeByteString(ByteString data) throws IOException {
            Intrinsics.checkNotNullParameter(data, "data");
            if (this.useCompression && Huffman.INSTANCE.encodedLength(data) < data.size()) {
                Buffer buffer = new Buffer();
                Huffman.INSTANCE.encode(data, buffer);
                ByteString readByteString = buffer.readByteString();
                writeInt(readByteString.size(), 127, 128);
                this.out.write(readByteString);
                return;
            }
            writeInt(data.size(), 127, 0);
            this.out.write(data);
        }

        public final void resizeHeaderTable(int i) {
            this.headerTableSizeSetting = i;
            int min = Math.min(i, 16384);
            int i2 = this.maxDynamicTableByteCount;
            if (i2 == min) {
                return;
            }
            if (min < i2) {
                this.smallestHeaderTableSizeSetting = Math.min(this.smallestHeaderTableSizeSetting, min);
            }
            this.emitDynamicTableSizeUpdate = true;
            this.maxDynamicTableByteCount = min;
            adjustDynamicTableByteCount();
        }

        private final void adjustDynamicTableByteCount() {
            int i = this.maxDynamicTableByteCount;
            int i2 = this.dynamicTableByteCount;
            if (i < i2) {
                if (i == 0) {
                    clearDynamicTable();
                } else {
                    evictToRecoverBytes(i2 - i);
                }
            }
        }
    }

    public final ByteString checkLowercase(ByteString name) throws IOException {
        Intrinsics.checkNotNullParameter(name, "name");
        int size = name.size();
        for (int i = 0; i < size; i++) {
            byte b = name.getByte(i);
            if (65 <= b && b < 91) {
                throw new IOException("PROTOCOL_ERROR response malformed: mixed case name: " + name.utf8());
            }
        }
        return name;
    }
}
