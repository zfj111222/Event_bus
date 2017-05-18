package com.brook.app.android.eventbus.utils;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * @author Brook
 * @time 2017/4/28 14:37
 */
public class MirrorsReference<T> extends WeakReference {

    public MirrorsReference(T referent) {
        super(referent);
    }

    public MirrorsReference(Object referent, ReferenceQueue q) {
        super(referent, q);
    }

    @Override
    public int hashCode() {
        if (get() == null) {
            return super.hashCode();
        } else {
            return get().hashCode();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (get() == null) {
            return super.equals(obj);
        } else {
            if (obj instanceof MirrorsReference) {
                return get().equals(((MirrorsReference) obj).get());
            }
            return get().equals(obj);
        }
    }
}
