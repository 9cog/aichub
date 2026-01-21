package android.support.customtabs.trusted;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public interface ITrustedWebActivityCallback extends IInterface {
    public static final String DESCRIPTOR = "android.support.customtabs.trusted.ITrustedWebActivityCallback";

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static class Default implements ITrustedWebActivityCallback {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.support.customtabs.trusted.ITrustedWebActivityCallback
        public void onExtraCallback(String str, Bundle bundle) throws RemoteException {
        }
    }

    void onExtraCallback(String str, Bundle bundle) throws RemoteException;

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static abstract class Stub extends Binder implements ITrustedWebActivityCallback {
        static final int TRANSACTION_onExtraCallback = 2;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, ITrustedWebActivityCallback.DESCRIPTOR);
        }

        public static ITrustedWebActivityCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(ITrustedWebActivityCallback.DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof ITrustedWebActivityCallback)) {
                return (ITrustedWebActivityCallback) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(ITrustedWebActivityCallback.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(ITrustedWebActivityCallback.DESCRIPTOR);
                return true;
            } else if (i == 2) {
                onExtraCallback(parcel.readString(), (Bundle) _Parcel.readTypedObject(parcel, Bundle.CREATOR));
                parcel2.writeNoException();
                return true;
            } else {
                return super.onTransact(i, parcel, parcel2, i2);
            }
        }

        /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
        private static class Proxy implements ITrustedWebActivityCallback {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return ITrustedWebActivityCallback.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.support.customtabs.trusted.ITrustedWebActivityCallback
            public void onExtraCallback(String str, Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ITrustedWebActivityCallback.DESCRIPTOR);
                    obtain.writeString(str);
                    _Parcel.writeTypedObject(obtain, bundle, 0);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }
    }

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static class _Parcel {
        /* JADX INFO: Access modifiers changed from: private */
        public static <T> T readTypedObject(Parcel parcel, Parcelable.Creator<T> creator) {
            if (parcel.readInt() != 0) {
                return creator.createFromParcel(parcel);
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static <T extends Parcelable> void writeTypedObject(Parcel parcel, T t, int i) {
            if (t != null) {
                parcel.writeInt(1);
                t.writeToParcel(parcel, i);
                return;
            }
            parcel.writeInt(0);
        }
    }
}
