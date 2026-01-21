package kotlinx.coroutines.flow;

import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.channels.BroadcastChannel;
import kotlinx.coroutines.channels.ChannelResult;
import kotlinx.coroutines.channels.ChannelsKt;
import kotlinx.coroutines.channels.ReceiveChannel;
import kotlinx.coroutines.flow.internal.ChannelFlowKt;
import okhttp3.HttpUrl;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: Channels.kt */
@Metadata(d1 = {"\u00000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u001e\u0010\u0000\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u0003H\u0007\u001a\u001c\u0010\u0004\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u0005\u001a/\u0010\u0006\u001a\u00020\u0007\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0005H\u0086@ø\u0001\u0000¢\u0006\u0002\u0010\n\u001a9\u0010\u000b\u001a\u00020\u0007\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u0002H\u00020\u00052\u0006\u0010\f\u001a\u00020\rH\u0082@ø\u0001\u0000¢\u0006\u0004\b\u000e\u0010\u000f\u001a&\u0010\u0010\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0005\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00012\u0006\u0010\u0011\u001a\u00020\u0012H\u0007\u001a\u001c\u0010\u0013\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u0005\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006\u0014"}, d2 = {"asFlow", "Lkotlinx/coroutines/flow/Flow;", "T", "Lkotlinx/coroutines/channels/BroadcastChannel;", "consumeAsFlow", "Lkotlinx/coroutines/channels/ReceiveChannel;", "emitAll", HttpUrl.FRAGMENT_ENCODE_SET, "Lkotlinx/coroutines/flow/FlowCollector;", "channel", "(Lkotlinx/coroutines/flow/FlowCollector;Lkotlinx/coroutines/channels/ReceiveChannel;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "emitAllImpl", "consume", HttpUrl.FRAGMENT_ENCODE_SET, "emitAllImpl$FlowKt__ChannelsKt", "(Lkotlinx/coroutines/flow/FlowCollector;Lkotlinx/coroutines/channels/ReceiveChannel;ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "produceIn", "scope", "Lkotlinx/coroutines/CoroutineScope;", "receiveAsFlow", "kotlinx-coroutines-core"}, k = 5, mv = {1, 6, 0}, xi = 48, xs = "kotlinx/coroutines/flow/FlowKt")
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public final /* synthetic */ class FlowKt__ChannelsKt {
    public static final <T> Object emitAll(FlowCollector<? super T> flowCollector, ReceiveChannel<? extends T> receiveChannel, Continuation<? super Unit> continuation) {
        Object emitAllImpl$FlowKt__ChannelsKt = emitAllImpl$FlowKt__ChannelsKt(flowCollector, receiveChannel, true, continuation);
        return emitAllImpl$FlowKt__ChannelsKt == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? emitAllImpl$FlowKt__ChannelsKt : Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:10:0x0025  */
    /* JADX WARN: Removed duplicated region for block: B:22:0x0058  */
    /* JADX WARN: Removed duplicated region for block: B:25:0x006c A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:26:0x006d  */
    /* JADX WARN: Removed duplicated region for block: B:29:0x0076 A[Catch: all -> 0x0056, TRY_LEAVE, TryCatch #0 {all -> 0x0056, blocks: (B:13:0x0033, B:27:0x0070, B:29:0x0076, B:35:0x0085, B:36:0x0086, B:18:0x004c), top: B:46:0x0023 }] */
    /* JADX WARN: Removed duplicated region for block: B:36:0x0086 A[Catch: all -> 0x0056, TRY_LEAVE, TryCatch #0 {all -> 0x0056, blocks: (B:13:0x0033, B:27:0x0070, B:29:0x0076, B:35:0x0085, B:36:0x0086, B:18:0x004c), top: B:46:0x0023 }] */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:37:0x0096 -> B:14:0x0036). Please submit an issue!!! */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final <T> Object emitAllImpl$FlowKt__ChannelsKt(FlowCollector<? super T> flowCollector, ReceiveChannel<? extends T> receiveChannel, boolean z, Continuation<? super Unit> continuation) {
        FlowKt__ChannelsKt$emitAllImpl$1 flowKt__ChannelsKt$emitAllImpl$1;
        int i;
        Object obj;
        FlowCollector flowCollector2;
        boolean z2;
        try {
            if (continuation instanceof FlowKt__ChannelsKt$emitAllImpl$1) {
                flowKt__ChannelsKt$emitAllImpl$1 = (FlowKt__ChannelsKt$emitAllImpl$1) continuation;
                if ((flowKt__ChannelsKt$emitAllImpl$1.label & Integer.MIN_VALUE) != 0) {
                    flowKt__ChannelsKt$emitAllImpl$1.label -= Integer.MIN_VALUE;
                    Object obj2 = flowKt__ChannelsKt$emitAllImpl$1.result;
                    Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                    i = flowKt__ChannelsKt$emitAllImpl$1.label;
                    if (i != 0) {
                        ResultKt.throwOnFailure(obj2);
                        FlowKt.ensureActive(flowCollector);
                        z2 = z;
                        flowKt__ChannelsKt$emitAllImpl$1.L$0 = flowCollector;
                        flowKt__ChannelsKt$emitAllImpl$1.L$1 = receiveChannel;
                        flowKt__ChannelsKt$emitAllImpl$1.Z$0 = z2;
                        flowKt__ChannelsKt$emitAllImpl$1.label = 1;
                        obj = receiveChannel.mo1621receiveCatchingJP2dKIU(flowKt__ChannelsKt$emitAllImpl$1);
                        if (obj == coroutine_suspended) {
                        }
                    } else if (i == 1) {
                        flowCollector = (FlowCollector<? super T>) flowKt__ChannelsKt$emitAllImpl$1.Z$0;
                        receiveChannel = (ReceiveChannel) flowKt__ChannelsKt$emitAllImpl$1.L$1;
                        FlowCollector flowCollector3 = (FlowCollector) flowKt__ChannelsKt$emitAllImpl$1.L$0;
                        ResultKt.throwOnFailure(obj2);
                        obj = ((ChannelResult) obj2).m1640unboximpl();
                        flowCollector2 = flowCollector3;
                        if (!ChannelResult.m1636isClosedimpl(obj)) {
                        }
                    } else if (i == 2) {
                        flowCollector = (FlowCollector<? super T>) flowKt__ChannelsKt$emitAllImpl$1.Z$0;
                        receiveChannel = (ReceiveChannel) flowKt__ChannelsKt$emitAllImpl$1.L$1;
                        FlowCollector<? super T> flowCollector4 = (FlowCollector) flowKt__ChannelsKt$emitAllImpl$1.L$0;
                        ResultKt.throwOnFailure(obj2);
                        FlowCollector<? super T> flowCollector5 = flowCollector4;
                        FlowCollector<? super T> flowCollector6 = flowCollector5;
                        z2 = flowCollector;
                        flowCollector = flowCollector6;
                        try {
                            flowKt__ChannelsKt$emitAllImpl$1.L$0 = flowCollector;
                            flowKt__ChannelsKt$emitAllImpl$1.L$1 = receiveChannel;
                            flowKt__ChannelsKt$emitAllImpl$1.Z$0 = z2;
                            flowKt__ChannelsKt$emitAllImpl$1.label = 1;
                            obj = receiveChannel.mo1621receiveCatchingJP2dKIU(flowKt__ChannelsKt$emitAllImpl$1);
                            if (obj == coroutine_suspended) {
                                return coroutine_suspended;
                            }
                            FlowCollector<? super T> flowCollector7 = z2;
                            flowCollector2 = flowCollector;
                            flowCollector = flowCollector7;
                            if (!ChannelResult.m1636isClosedimpl(obj)) {
                                Throwable m1632exceptionOrNullimpl = ChannelResult.m1632exceptionOrNullimpl(obj);
                                if (m1632exceptionOrNullimpl != null) {
                                    throw m1632exceptionOrNullimpl;
                                }
                                if (flowCollector != null) {
                                    ChannelsKt.cancelConsumed(receiveChannel, null);
                                }
                                return Unit.INSTANCE;
                            }
                            Object m1634getOrThrowimpl = ChannelResult.m1634getOrThrowimpl(obj);
                            flowKt__ChannelsKt$emitAllImpl$1.L$0 = flowCollector2;
                            flowKt__ChannelsKt$emitAllImpl$1.L$1 = receiveChannel;
                            flowKt__ChannelsKt$emitAllImpl$1.Z$0 = (boolean) flowCollector;
                            flowKt__ChannelsKt$emitAllImpl$1.label = 2;
                            Object emit = flowCollector2.emit(m1634getOrThrowimpl, flowKt__ChannelsKt$emitAllImpl$1);
                            flowCollector5 = flowCollector2;
                            if (emit == coroutine_suspended) {
                                return coroutine_suspended;
                            }
                            FlowCollector<? super T> flowCollector62 = flowCollector5;
                            z2 = flowCollector;
                            flowCollector = flowCollector62;
                            flowKt__ChannelsKt$emitAllImpl$1.L$0 = flowCollector;
                            flowKt__ChannelsKt$emitAllImpl$1.L$1 = receiveChannel;
                            flowKt__ChannelsKt$emitAllImpl$1.Z$0 = z2;
                            flowKt__ChannelsKt$emitAllImpl$1.label = 1;
                            obj = receiveChannel.mo1621receiveCatchingJP2dKIU(flowKt__ChannelsKt$emitAllImpl$1);
                            if (obj == coroutine_suspended) {
                            }
                        } catch (Throwable th) {
                            FlowCollector<? super T> flowCollector8 = z2;
                            th = th;
                            flowCollector = flowCollector8;
                            try {
                                throw th;
                            } catch (Throwable th2) {
                                if (flowCollector != null) {
                                    ChannelsKt.cancelConsumed(receiveChannel, th);
                                }
                                throw th2;
                            }
                        }
                    } else {
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                    }
                }
            }
            if (i != 0) {
            }
        } catch (Throwable th3) {
            th = th3;
        }
        flowKt__ChannelsKt$emitAllImpl$1 = new FlowKt__ChannelsKt$emitAllImpl$1(continuation);
        Object obj22 = flowKt__ChannelsKt$emitAllImpl$1.result;
        Object coroutine_suspended2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        i = flowKt__ChannelsKt$emitAllImpl$1.label;
    }

    public static final <T> Flow<T> receiveAsFlow(ReceiveChannel<? extends T> receiveChannel) {
        return new ChannelAsFlow(receiveChannel, false, null, 0, null, 28, null);
    }

    public static final <T> Flow<T> consumeAsFlow(ReceiveChannel<? extends T> receiveChannel) {
        return new ChannelAsFlow(receiveChannel, true, null, 0, null, 28, null);
    }

    public static final <T> ReceiveChannel<T> produceIn(Flow<? extends T> flow, CoroutineScope coroutineScope) {
        return ChannelFlowKt.asChannelFlow(flow).produceImpl(coroutineScope);
    }

    @Deprecated(level = DeprecationLevel.WARNING, message = "'BroadcastChannel' is obsolete and all corresponding operators are deprecated in the favour of StateFlow and SharedFlow")
    public static final <T> Flow<T> asFlow(final BroadcastChannel<T> broadcastChannel) {
        return new Flow<T>() { // from class: kotlinx.coroutines.flow.FlowKt__ChannelsKt$asFlow$$inlined$unsafeFlow$1
            @Override // kotlinx.coroutines.flow.Flow
            public Object collect(FlowCollector<? super T> flowCollector, Continuation<? super Unit> continuation) {
                Object emitAll = FlowKt.emitAll(flowCollector, BroadcastChannel.this.openSubscription(), continuation);
                return emitAll == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? emitAll : Unit.INSTANCE;
            }
        };
    }
}
