package com.obamabob.runite.settings;

public class OnChangedSetting<T> {

    private final T _old;
    private final T _new;

    public OnChangedSetting(T _old, T _new) {
        this._old = _old;
        this._new = _new;
    }

    public T getOld() {
        return _old;
    }

    public T getNew() {
        return _new;
    }
}