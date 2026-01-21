package kotlinx.coroutines.sync;

import androidx.concurrent.futures.AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugProbesKt;
import kotlin.jvm.functions.Function1;
import kotlinx.coroutines.CancellableContinuation;
import kotlinx.coroutines.CancellableContinuationImpl;
import kotlinx.coroutines.CancellableContinuationKt;
import kotlinx.coroutines.DebugKt;
import kotlinx.coroutines.debug.internal.ConcurrentWeakMap$Core$$ExternalSyntheticBackportWithForwarding0;
import kotlinx.coroutines.internal.ConcurrentLinkedListKt;
import kotlinx.coroutines.internal.ConcurrentLinkedListNode;
import kotlinx.coroutines.internal.Segment;
import kotlinx.coroutines.internal.SegmentOrClosed;
import kotlinx.coroutines.internal.Symbol;
import okhttp3.HttpUrl;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: Semaphore.kt */
@Metadata(d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\u0010\u0003\n\u0002\b\u0004\n\u0002\u0018\u0002\b\u0002\u0018\u00002\u00020\u001eB\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0001\u0012\u0006\u0010\u0003\u001a\u00020\u0001¢\u0006\u0004\b\u0004\u0010\u0005J\u0013\u0010\u0007\u001a\u00020\u0006H\u0096@ø\u0001\u0000¢\u0006\u0004\b\u0007\u0010\bJ\u0013\u0010\t\u001a\u00020\u0006H\u0082@ø\u0001\u0000¢\u0006\u0004\b\t\u0010\bJ\u001d\u0010\r\u001a\u00020\f2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00060\nH\u0002¢\u0006\u0004\b\r\u0010\u000eJ\u000f\u0010\u000f\u001a\u00020\u0006H\u0016¢\u0006\u0004\b\u000f\u0010\u0010J\u000f\u0010\u0011\u001a\u00020\fH\u0016¢\u0006\u0004\b\u0011\u0010\u0012J\u000f\u0010\u0013\u001a\u00020\fH\u0002¢\u0006\u0004\b\u0013\u0010\u0012J\u0019\u0010\u0014\u001a\u00020\f*\b\u0012\u0004\u0012\u00020\u00060\nH\u0002¢\u0006\u0004\b\u0014\u0010\u000eR\u0014\u0010\u0017\u001a\u00020\u00018VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0015\u0010\u0016R \u0010\u001a\u001a\u000e\u0012\u0004\u0012\u00020\u0019\u0012\u0004\u0012\u00020\u00060\u00188\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b\u001a\u0010\u001bR\u0014\u0010\u0002\u001a\u00020\u00018\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b\u0002\u0010\u001c\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006\u001d"}, d2 = {"Lkotlinx/coroutines/sync/SemaphoreImpl;", HttpUrl.FRAGMENT_ENCODE_SET, "permits", "acquiredPermits", "<init>", "(II)V", HttpUrl.FRAGMENT_ENCODE_SET, "acquire", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "acquireSlowPath", "Lkotlinx/coroutines/CancellableContinuation;", "cont", HttpUrl.FRAGMENT_ENCODE_SET, "addAcquireToQueue", "(Lkotlinx/coroutines/CancellableContinuation;)Z", "release", "()V", "tryAcquire", "()Z", "tryResumeNextFromQueue", "tryResumeAcquire", "getAvailablePermits", "()I", "availablePermits", "Lkotlin/Function1;", HttpUrl.FRAGMENT_ENCODE_SET, "onCancellationRelease", "Lkotlin/jvm/functions/Function1;", "I", "kotlinx-coroutines-core", "Lkotlinx/coroutines/sync/Semaphore;"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public final class SemaphoreImpl implements Semaphore {
    volatile /* synthetic */ int _availablePermits;
    private volatile /* synthetic */ long deqIdx = 0;
    private volatile /* synthetic */ long enqIdx = 0;
    private volatile /* synthetic */ Object head;
    private final Function1<Throwable, Unit> onCancellationRelease;
    private final int permits;
    private volatile /* synthetic */ Object tail;
    private static final /* synthetic */ AtomicReferenceFieldUpdater head$FU = AtomicReferenceFieldUpdater.newUpdater(SemaphoreImpl.class, Object.class, "head");
    private static final /* synthetic */ AtomicLongFieldUpdater deqIdx$FU = AtomicLongFieldUpdater.newUpdater(SemaphoreImpl.class, "deqIdx");
    private static final /* synthetic */ AtomicReferenceFieldUpdater tail$FU = AtomicReferenceFieldUpdater.newUpdater(SemaphoreImpl.class, Object.class, "tail");
    private static final /* synthetic */ AtomicLongFieldUpdater enqIdx$FU = AtomicLongFieldUpdater.newUpdater(SemaphoreImpl.class, "enqIdx");
    static final /* synthetic */ AtomicIntegerFieldUpdater _availablePermits$FU = AtomicIntegerFieldUpdater.newUpdater(SemaphoreImpl.class, "_availablePermits");

    public SemaphoreImpl(int i, int i2) {
        this.permits = i;
        boolean z = true;
        if (!(i > 0)) {
            throw new IllegalArgumentException(("Semaphore should have at least 1 permit, but had " + i).toString());
        }
        if (!((i2 < 0 || i2 > i) ? false : false)) {
            throw new IllegalArgumentException(("The number of acquired permits should be in 0.." + i).toString());
        }
        SemaphoreSegment semaphoreSegment = new SemaphoreSegment(0L, null, 2);
        this.head = semaphoreSegment;
        this.tail = semaphoreSegment;
        this._availablePermits = i - i2;
        this.onCancellationRelease = new Function1<Throwable, Unit>() { // from class: kotlinx.coroutines.sync.SemaphoreImpl$onCancellationRelease$1
            /* JADX INFO: Access modifiers changed from: package-private */
            {
                super(1);
            }

            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(Throwable th) {
                invoke2(th);
                return Unit.INSTANCE;
            }

            /* renamed from: invoke  reason: avoid collision after fix types in other method */
            public final void invoke2(Throwable th) {
                SemaphoreImpl.this.release();
            }
        };
    }

    @Override // kotlinx.coroutines.sync.Semaphore
    public int getAvailablePermits() {
        return Math.max(this._availablePermits, 0);
    }

    @Override // kotlinx.coroutines.sync.Semaphore
    public Object acquire(Continuation<? super Unit> continuation) {
        if (_availablePermits$FU.getAndDecrement(this) > 0) {
            return Unit.INSTANCE;
        }
        Object acquireSlowPath = acquireSlowPath(continuation);
        return acquireSlowPath == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? acquireSlowPath : Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r8v6 */
    public final boolean addAcquireToQueue(CancellableContinuation<? super Unit> cancellableContinuation) {
        int i;
        Object m1672constructorimpl;
        SemaphoreSegment createSegment;
        int i2;
        Symbol symbol;
        Symbol symbol2;
        Symbol symbol3;
        boolean z;
        SemaphoreSegment semaphoreSegment = (SemaphoreSegment) this.tail;
        long andIncrement = enqIdx$FU.getAndIncrement(this);
        i = SemaphoreKt.SEGMENT_SIZE;
        long j = andIncrement / i;
        do {
            SemaphoreSegment semaphoreSegment2 = semaphoreSegment;
            while (true) {
                if (semaphoreSegment2.getId() < j || semaphoreSegment2.getRemoved()) {
                    Object nextOrClosed = semaphoreSegment2.getNextOrClosed();
                    if (nextOrClosed == ConcurrentLinkedListKt.CLOSED) {
                        m1672constructorimpl = SegmentOrClosed.m1672constructorimpl(ConcurrentLinkedListKt.CLOSED);
                        break;
                    }
                    SemaphoreSegment semaphoreSegment3 = (Segment) ((ConcurrentLinkedListNode) nextOrClosed);
                    if (semaphoreSegment3 == null) {
                        createSegment = SemaphoreKt.createSegment(semaphoreSegment2.getId() + 1, semaphoreSegment2);
                        semaphoreSegment3 = createSegment;
                        if (semaphoreSegment2.trySetNext(semaphoreSegment3)) {
                            if (semaphoreSegment2.getRemoved()) {
                                semaphoreSegment2.remove();
                            }
                        }
                    }
                    semaphoreSegment2 = semaphoreSegment3;
                } else {
                    m1672constructorimpl = SegmentOrClosed.m1672constructorimpl(semaphoreSegment2);
                    break;
                }
            }
            if (SegmentOrClosed.m1677isClosedimpl(m1672constructorimpl)) {
                break;
            }
            Segment m1675getSegmentimpl = SegmentOrClosed.m1675getSegmentimpl(m1672constructorimpl);
            while (true) {
                Segment segment = (Segment) this.tail;
                if (segment.getId() >= m1675getSegmentimpl.getId()) {
                    break;
                } else if (!m1675getSegmentimpl.tryIncPointers$kotlinx_coroutines_core()) {
                    z = false;
                    continue;
                    break;
                } else if (AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(tail$FU, this, segment, m1675getSegmentimpl)) {
                    if (segment.decPointers$kotlinx_coroutines_core()) {
                        segment.remove();
                    }
                } else if (m1675getSegmentimpl.decPointers$kotlinx_coroutines_core()) {
                    m1675getSegmentimpl.remove();
                }
            }
            z = true;
            continue;
        } while (!z);
        SemaphoreSegment semaphoreSegment4 = (SemaphoreSegment) SegmentOrClosed.m1675getSegmentimpl(m1672constructorimpl);
        i2 = SemaphoreKt.SEGMENT_SIZE;
        int i3 = (int) (andIncrement % i2);
        if (!ConcurrentWeakMap$Core$$ExternalSyntheticBackportWithForwarding0.m(semaphoreSegment4.acquirers, i3, null, cancellableContinuation)) {
            symbol = SemaphoreKt.PERMIT;
            symbol2 = SemaphoreKt.TAKEN;
            if (ConcurrentWeakMap$Core$$ExternalSyntheticBackportWithForwarding0.m(semaphoreSegment4.acquirers, i3, symbol, symbol2)) {
                cancellableContinuation.resume(Unit.INSTANCE, this.onCancellationRelease);
                return true;
            }
            if (DebugKt.getASSERTIONS_ENABLED()) {
                Object obj = semaphoreSegment4.acquirers.get(i3);
                symbol3 = SemaphoreKt.BROKEN;
                if (!(obj == symbol3)) {
                    throw new AssertionError();
                }
            }
            return false;
        }
        cancellableContinuation.invokeOnCancellation(new CancelSemaphoreAcquisitionHandler(semaphoreSegment4, i3));
        return true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:18:0x0059, code lost:
        r6 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x0075, code lost:
        continue;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r6v6 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private final boolean tryResumeNextFromQueue() {
        int i;
        Object m1672constructorimpl;
        SemaphoreSegment createSegment;
        int i2;
        int i3;
        Symbol symbol;
        Symbol symbol2;
        int i4;
        Symbol symbol3;
        Symbol symbol4;
        Symbol symbol5;
        boolean z;
        SemaphoreSegment semaphoreSegment = (SemaphoreSegment) this.head;
        long andIncrement = deqIdx$FU.getAndIncrement(this);
        i = SemaphoreKt.SEGMENT_SIZE;
        long j = andIncrement / i;
        do {
            SemaphoreSegment semaphoreSegment2 = semaphoreSegment;
            while (true) {
                if (semaphoreSegment2.getId() < j || semaphoreSegment2.getRemoved()) {
                    Object nextOrClosed = semaphoreSegment2.getNextOrClosed();
                    if (nextOrClosed == ConcurrentLinkedListKt.CLOSED) {
                        m1672constructorimpl = SegmentOrClosed.m1672constructorimpl(ConcurrentLinkedListKt.CLOSED);
                        break;
                    }
                    SemaphoreSegment semaphoreSegment3 = (Segment) ((ConcurrentLinkedListNode) nextOrClosed);
                    if (semaphoreSegment3 == null) {
                        createSegment = SemaphoreKt.createSegment(semaphoreSegment2.getId() + 1, semaphoreSegment2);
                        semaphoreSegment3 = createSegment;
                        if (semaphoreSegment2.trySetNext(semaphoreSegment3)) {
                            if (semaphoreSegment2.getRemoved()) {
                                semaphoreSegment2.remove();
                            }
                        }
                    }
                    semaphoreSegment2 = semaphoreSegment3;
                } else {
                    m1672constructorimpl = SegmentOrClosed.m1672constructorimpl(semaphoreSegment2);
                    break;
                }
            }
            if (SegmentOrClosed.m1677isClosedimpl(m1672constructorimpl)) {
                break;
            }
            Segment m1675getSegmentimpl = SegmentOrClosed.m1675getSegmentimpl(m1672constructorimpl);
            while (true) {
                Segment segment = (Segment) this.head;
                if (segment.getId() >= m1675getSegmentimpl.getId()) {
                    break;
                } else if (!m1675getSegmentimpl.tryIncPointers$kotlinx_coroutines_core()) {
                    z = false;
                    continue;
                    break;
                } else if (AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(head$FU, this, segment, m1675getSegmentimpl)) {
                    if (segment.decPointers$kotlinx_coroutines_core()) {
                        segment.remove();
                    }
                } else if (m1675getSegmentimpl.decPointers$kotlinx_coroutines_core()) {
                    m1675getSegmentimpl.remove();
                }
            }
        } while (!z);
        SemaphoreSegment semaphoreSegment4 = (SemaphoreSegment) SegmentOrClosed.m1675getSegmentimpl(m1672constructorimpl);
        semaphoreSegment4.cleanPrev();
        if (semaphoreSegment4.getId() > j) {
            return false;
        }
        i3 = SemaphoreKt.SEGMENT_SIZE;
        int i5 = (int) (andIncrement % i3);
        symbol = SemaphoreKt.PERMIT;
        Object andSet = semaphoreSegment4.acquirers.getAndSet(i5, symbol);
        if (andSet == null) {
            i4 = SemaphoreKt.MAX_SPIN_CYCLES;
            for (i2 = 0; i2 < i4; i2++) {
                Object obj = semaphoreSegment4.acquirers.get(i5);
                symbol5 = SemaphoreKt.TAKEN;
                if (obj == symbol5) {
                    return true;
                }
            }
            symbol3 = SemaphoreKt.PERMIT;
            symbol4 = SemaphoreKt.BROKEN;
            return !ConcurrentWeakMap$Core$$ExternalSyntheticBackportWithForwarding0.m(semaphoreSegment4.acquirers, i5, symbol3, symbol4);
        }
        symbol2 = SemaphoreKt.CANCELLED;
        if (andSet == symbol2) {
            return false;
        }
        return tryResumeAcquire((CancellableContinuation) andSet);
    }

    private final boolean tryResumeAcquire(CancellableContinuation<? super Unit> cancellableContinuation) {
        Object tryResume = cancellableContinuation.tryResume(Unit.INSTANCE, null, this.onCancellationRelease);
        if (tryResume == null) {
            return false;
        }
        cancellableContinuation.completeResume(tryResume);
        return true;
    }

    @Override // kotlinx.coroutines.sync.Semaphore
    public boolean tryAcquire() {
        int i;
        do {
            i = this._availablePermits;
            if (i <= 0) {
                return false;
            }
        } while (!_availablePermits$FU.compareAndSet(this, i, i - 1));
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final Object acquireSlowPath(Continuation<? super Unit> continuation) {
        CancellableContinuationImpl orCreateCancellableContinuation = CancellableContinuationKt.getOrCreateCancellableContinuation(IntrinsicsKt.intercepted(continuation));
        CancellableContinuationImpl cancellableContinuationImpl = orCreateCancellableContinuation;
        while (true) {
            if (!addAcquireToQueue(cancellableContinuationImpl)) {
                if (_availablePermits$FU.getAndDecrement(this) > 0) {
                    cancellableContinuationImpl.resume(Unit.INSTANCE, this.onCancellationRelease);
                    break;
                }
            } else {
                break;
            }
        }
        Object result = orCreateCancellableContinuation.getResult();
        if (result == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
            DebugProbesKt.probeCoroutineSuspended(continuation);
        }
        return result == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? result : Unit.INSTANCE;
    }

    @Override // kotlinx.coroutines.sync.Semaphore
    public void release() {
        while (true) {
            int i = this._availablePermits;
            if (!(i < this.permits)) {
                throw new IllegalStateException(("The number of released permits cannot be greater than " + this.permits).toString());
            }
            if (_availablePermits$FU.compareAndSet(this, i, i + 1) && (i >= 0 || tryResumeNextFromQueue())) {
                return;
            }
        }
    }
}
