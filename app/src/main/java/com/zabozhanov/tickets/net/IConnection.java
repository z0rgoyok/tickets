package com.zabozhanov.tickets.net;

import android.content.Context;

/**
 * Created by z0rgoyok on 20.05.16.
 */
public interface IConnection {
    boolean initConnection(Context context, int deviceID);
    boolean writePackage(Package p);
}
