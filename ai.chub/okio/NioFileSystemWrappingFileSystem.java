package okio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.io.path.PathsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import okhttp3.HttpUrl;
import okio.Path;
/* compiled from: NioFileSystemWrappingFileSystem.kt */
@Metadata(d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\n\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u0018\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0016J\u0018\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\b2\u0006\u0010\u000e\u001a\u00020\bH\u0016J\u0010\u0010\u000f\u001a\u00020\b2\u0006\u0010\u0010\u001a\u00020\bH\u0016J\u0018\u0010\u0011\u001a\u00020\f2\u0006\u0010\u0012\u001a\u00020\b2\u0006\u0010\u0013\u001a\u00020\nH\u0016J\u0018\u0010\u0014\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\b2\u0006\u0010\u000e\u001a\u00020\bH\u0016J\u0018\u0010\u0015\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0016J\u0016\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\b0\u00172\u0006\u0010\u0012\u001a\u00020\bH\u0016J \u0010\u0016\u001a\n\u0012\u0004\u0012\u00020\b\u0018\u00010\u00172\u0006\u0010\u0012\u001a\u00020\b2\u0006\u0010\u0018\u001a\u00020\nH\u0002J\u0018\u0010\u0019\u001a\n\u0012\u0004\u0012\u00020\b\u0018\u00010\u00172\u0006\u0010\u0012\u001a\u00020\bH\u0016J\u0012\u0010\u001a\u001a\u0004\u0018\u00010\u001b2\u0006\u0010\u0010\u001a\u00020\bH\u0016J\u0010\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u0007\u001a\u00020\bH\u0016J \u0010\u001e\u001a\u00020\u001d2\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0013\u001a\u00020\n2\u0006\u0010\t\u001a\u00020\nH\u0016J\u0018\u0010\u001f\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0013\u001a\u00020\nH\u0016J\u0010\u0010\r\u001a\u00020 2\u0006\u0010\u0007\u001a\u00020\bH\u0016J\b\u0010!\u001a\u00020\"H\u0016J\f\u0010#\u001a\u00020$*\u00020\bH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006%"}, d2 = {"Lokio/NioFileSystemWrappingFileSystem;", "Lokio/NioSystemFileSystem;", "nioFileSystem", "Ljava/nio/file/FileSystem;", "(Ljava/nio/file/FileSystem;)V", "appendingSink", "Lokio/Sink;", "file", "Lokio/Path;", "mustExist", HttpUrl.FRAGMENT_ENCODE_SET, "atomicMove", HttpUrl.FRAGMENT_ENCODE_SET, "source", "target", "canonicalize", "path", "createDirectory", "dir", "mustCreate", "createSymlink", "delete", "list", HttpUrl.FRAGMENT_ENCODE_SET, "throwOnFailure", "listOrNull", "metadataOrNull", "Lokio/FileMetadata;", "openReadOnly", "Lokio/FileHandle;", "openReadWrite", "sink", "Lokio/Source;", "toString", HttpUrl.FRAGMENT_ENCODE_SET, "resolve", "Ljava/nio/file/Path;", "okio"}, k = 1, mv = {1, 9, 0}, xi = 48)
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public final class NioFileSystemWrappingFileSystem extends NioSystemFileSystem {
    private final java.nio.file.FileSystem nioFileSystem;

    public NioFileSystemWrappingFileSystem(java.nio.file.FileSystem nioFileSystem) {
        Intrinsics.checkNotNullParameter(nioFileSystem, "nioFileSystem");
        this.nioFileSystem = nioFileSystem;
    }

    private final java.nio.file.Path resolve(Path path) {
        java.nio.file.Path path2 = this.nioFileSystem.getPath(path.toString(), new String[0]);
        Intrinsics.checkNotNullExpressionValue(path2, "getPath(...)");
        return path2;
    }

    @Override // okio.JvmSystemFileSystem, okio.FileSystem
    public Path canonicalize(Path path) {
        Intrinsics.checkNotNullParameter(path, "path");
        try {
            Path.Companion companion = Path.Companion;
            java.nio.file.Path realPath = resolve(path).toRealPath(new LinkOption[0]);
            Intrinsics.checkNotNullExpressionValue(realPath, "toRealPath(...)");
            return Path.Companion.get$default(companion, realPath, false, 1, (Object) null);
        } catch (NoSuchFileException unused) {
            throw new FileNotFoundException("no such file: " + path);
        }
    }

    @Override // okio.NioSystemFileSystem, okio.JvmSystemFileSystem, okio.FileSystem
    public FileMetadata metadataOrNull(Path path) {
        Intrinsics.checkNotNullParameter(path, "path");
        return metadataOrNull(resolve(path));
    }

    @Override // okio.JvmSystemFileSystem, okio.FileSystem
    public List<Path> list(Path dir) {
        Intrinsics.checkNotNullParameter(dir, "dir");
        List<Path> list = list(dir, true);
        Intrinsics.checkNotNull(list);
        return list;
    }

    @Override // okio.JvmSystemFileSystem, okio.FileSystem
    public List<Path> listOrNull(Path dir) {
        Intrinsics.checkNotNullParameter(dir, "dir");
        return list(dir, false);
    }

    private final List<Path> list(Path path, boolean z) {
        java.nio.file.Path resolve = resolve(path);
        try {
            List<java.nio.file.Path> listDirectoryEntries$default = PathsKt.listDirectoryEntries$default(resolve, null, 1, null);
            ArrayList arrayList = new ArrayList();
            for (java.nio.file.Path path2 : listDirectoryEntries$default) {
                arrayList.add(Path.Companion.get$default(Path.Companion, path2, false, 1, (Object) null));
            }
            ArrayList arrayList2 = arrayList;
            CollectionsKt.sort(arrayList2);
            return arrayList2;
        } catch (Exception unused) {
            if (z) {
                if (Files.exists(resolve, (LinkOption[]) Arrays.copyOf(new LinkOption[0], 0))) {
                    throw new IOException("failed to list " + path);
                }
                throw new FileNotFoundException("no such file: " + path);
            }
            return null;
        }
    }

    @Override // okio.JvmSystemFileSystem, okio.FileSystem
    public FileHandle openReadOnly(Path file) {
        Intrinsics.checkNotNullParameter(file, "file");
        try {
            FileChannel open = FileChannel.open(resolve(file), StandardOpenOption.READ);
            Intrinsics.checkNotNull(open);
            return new NioFileSystemFileHandle(false, open);
        } catch (NoSuchFileException unused) {
            throw new FileNotFoundException("no such file: " + file);
        }
    }

    @Override // okio.JvmSystemFileSystem, okio.FileSystem
    public FileHandle openReadWrite(Path file, boolean z, boolean z2) {
        Intrinsics.checkNotNullParameter(file, "file");
        if (!((z && z2) ? false : true)) {
            throw new IllegalArgumentException("Cannot require mustCreate and mustExist at the same time.".toString());
        }
        List createListBuilder = CollectionsKt.createListBuilder();
        createListBuilder.add(StandardOpenOption.READ);
        createListBuilder.add(StandardOpenOption.WRITE);
        if (z) {
            createListBuilder.add(StandardOpenOption.CREATE_NEW);
        } else if (!z2) {
            createListBuilder.add(StandardOpenOption.CREATE);
        }
        List build = CollectionsKt.build(createListBuilder);
        try {
            java.nio.file.Path resolve = resolve(file);
            StandardOpenOption[] standardOpenOptionArr = (StandardOpenOption[]) build.toArray(new StandardOpenOption[0]);
            FileChannel open = FileChannel.open(resolve, (OpenOption[]) Arrays.copyOf(standardOpenOptionArr, standardOpenOptionArr.length));
            Intrinsics.checkNotNull(open);
            return new NioFileSystemFileHandle(true, open);
        } catch (NoSuchFileException unused) {
            throw new FileNotFoundException("no such file: " + file);
        }
    }

    @Override // okio.JvmSystemFileSystem, okio.FileSystem
    public Source source(Path file) {
        Intrinsics.checkNotNullParameter(file, "file");
        try {
            InputStream newInputStream = Files.newInputStream(resolve(file), (OpenOption[]) Arrays.copyOf(new OpenOption[0], 0));
            Intrinsics.checkNotNullExpressionValue(newInputStream, "newInputStream(this, *options)");
            return Okio.source(newInputStream);
        } catch (NoSuchFileException unused) {
            throw new FileNotFoundException("no such file: " + file);
        }
    }

    @Override // okio.JvmSystemFileSystem, okio.FileSystem
    public Sink sink(Path file, boolean z) {
        Intrinsics.checkNotNullParameter(file, "file");
        List createListBuilder = CollectionsKt.createListBuilder();
        if (z) {
            createListBuilder.add(StandardOpenOption.CREATE_NEW);
        }
        List build = CollectionsKt.build(createListBuilder);
        try {
            java.nio.file.Path resolve = resolve(file);
            StandardOpenOption[] standardOpenOptionArr = (StandardOpenOption[]) build.toArray(new StandardOpenOption[0]);
            OpenOption[] openOptionArr = (OpenOption[]) Arrays.copyOf(standardOpenOptionArr, standardOpenOptionArr.length);
            OutputStream newOutputStream = Files.newOutputStream(resolve, (OpenOption[]) Arrays.copyOf(openOptionArr, openOptionArr.length));
            Intrinsics.checkNotNullExpressionValue(newOutputStream, "newOutputStream(this, *options)");
            return Okio.sink(newOutputStream);
        } catch (NoSuchFileException unused) {
            throw new FileNotFoundException("no such file: " + file);
        }
    }

    @Override // okio.JvmSystemFileSystem, okio.FileSystem
    public Sink appendingSink(Path file, boolean z) {
        Intrinsics.checkNotNullParameter(file, "file");
        List createListBuilder = CollectionsKt.createListBuilder();
        createListBuilder.add(StandardOpenOption.APPEND);
        if (!z) {
            createListBuilder.add(StandardOpenOption.CREATE);
        }
        List build = CollectionsKt.build(createListBuilder);
        java.nio.file.Path resolve = resolve(file);
        StandardOpenOption[] standardOpenOptionArr = (StandardOpenOption[]) build.toArray(new StandardOpenOption[0]);
        OpenOption[] openOptionArr = (OpenOption[]) Arrays.copyOf(standardOpenOptionArr, standardOpenOptionArr.length);
        OutputStream newOutputStream = Files.newOutputStream(resolve, (OpenOption[]) Arrays.copyOf(openOptionArr, openOptionArr.length));
        Intrinsics.checkNotNullExpressionValue(newOutputStream, "newOutputStream(this, *options)");
        return Okio.sink(newOutputStream);
    }

    /* JADX WARN: Code restructure failed: missing block: B:5:0x0011, code lost:
        if (r0.isDirectory() == true) goto L5;
     */
    @Override // okio.JvmSystemFileSystem, okio.FileSystem
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void createDirectory(Path dir, boolean z) {
        Intrinsics.checkNotNullParameter(dir, "dir");
        FileMetadata metadataOrNull = metadataOrNull(dir);
        boolean z2 = metadataOrNull != null;
        if (z2 && z) {
            throw new IOException(dir + " already exists.");
        }
        try {
            Intrinsics.checkNotNullExpressionValue(Files.createDirectory(resolve(dir), (FileAttribute[]) Arrays.copyOf(new FileAttribute[0], 0)), "createDirectory(this, *attributes)");
        } catch (IOException e) {
            if (z2) {
                return;
            }
            throw new IOException("failed to create directory: " + dir, e);
        }
    }

    @Override // okio.NioSystemFileSystem, okio.JvmSystemFileSystem, okio.FileSystem
    public void atomicMove(Path source, Path target) {
        Intrinsics.checkNotNullParameter(source, "source");
        Intrinsics.checkNotNullParameter(target, "target");
        try {
            Intrinsics.checkNotNullExpressionValue(Files.move(resolve(source), resolve(target), (CopyOption[]) Arrays.copyOf(new CopyOption[]{StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING}, 2)), "move(this, target, *options)");
        } catch (UnsupportedOperationException unused) {
            throw new IOException("atomic move not supported");
        } catch (NoSuchFileException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Override // okio.JvmSystemFileSystem, okio.FileSystem
    public void delete(Path path, boolean z) {
        Intrinsics.checkNotNullParameter(path, "path");
        if (Thread.interrupted()) {
            throw new InterruptedIOException("interrupted");
        }
        java.nio.file.Path resolve = resolve(path);
        try {
            Files.delete(resolve);
        } catch (NoSuchFileException unused) {
            if (z) {
                throw new FileNotFoundException("no such file: " + path);
            }
        } catch (IOException unused2) {
            if (Files.exists(resolve, (LinkOption[]) Arrays.copyOf(new LinkOption[0], 0))) {
                throw new IOException("failed to delete " + path);
            }
        }
    }

    @Override // okio.NioSystemFileSystem, okio.JvmSystemFileSystem, okio.FileSystem
    public void createSymlink(Path source, Path target) {
        Intrinsics.checkNotNullParameter(source, "source");
        Intrinsics.checkNotNullParameter(target, "target");
        Intrinsics.checkNotNullExpressionValue(Files.createSymbolicLink(resolve(source), resolve(target), (FileAttribute[]) Arrays.copyOf(new FileAttribute[0], 0)), "createSymbolicLink(this, target, *attributes)");
    }

    @Override // okio.NioSystemFileSystem, okio.JvmSystemFileSystem
    public String toString() {
        String simpleName = Reflection.getOrCreateKotlinClass(this.nioFileSystem.getClass()).getSimpleName();
        Intrinsics.checkNotNull(simpleName);
        return simpleName;
    }
}
