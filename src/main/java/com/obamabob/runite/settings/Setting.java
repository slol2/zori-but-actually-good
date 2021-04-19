package com.obamabob.runite.settings;

import com.obamabob.runite.Runite;
import com.obamabob.runite.module.Module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Setting<T> {
    public Module parent;
    private String name;
    private Mode mode;

    enum Mode {
        UNKNOWN,
        MODE,
        ENUM,
        TOGGLE,
        NUMBER,
        BIND
    }

    private T value;
    private ArrayList<T> options;

    private T min;
    private T max;

    private Consumer<OnChangedSetting<T>> changeTask = null;
    private Predicate<T> visibleCheck = null;
    private Predicate<T> filter = null;
    private String filterError = null;

    public Setting(String name, Module parent, T value, ArrayList<T> options){
        this.name = name;
        this.parent = parent;
        this.value = value;
        this.options = options;
        this.mode = Mode.MODE;
    }

    public Setting(String name, Module parent, T value, T[] options){
        this.name = name;
        this.parent = parent;
        this.value = value;
        this.options = new ArrayList<>(Arrays.asList(options));
        this.mode = Mode.MODE;
        if (value instanceof Enum) {
            this.mode = Mode.ENUM;
        }
    }

    public Setting(String name, Module parent, T value){
        this.name = name;
        this.parent = parent;
        this.value = value;
        this.mode = Mode.UNKNOWN;
        if (value instanceof Boolean) {
            this.mode = Mode.TOGGLE;
        } else if (value instanceof Bind) {
            this.mode = Mode.BIND;
        }
    }

    public Setting(String name, Module parent, T value, T min, T max){
        this.name = name;
        this.parent = parent;
        this.value = value;
        this.min = min;
        this.max = max;
        this.mode = Mode.NUMBER;
    }

    public String getName(){
        return name;
    }

    public Module getParentMod(){
        return parent;
    }

    public ArrayList<T> getOptions(){
        return this.options;
    }

    public String getCorrectString(String stringIn) {
        if (this.value instanceof String) {
            for (String s : (ArrayList<String>) options) {
                if (s.equalsIgnoreCase(stringIn)) return s;
            }
            return null;
        }
        else if (mode == Mode.ENUM) {
            for (T s : options) {
                if (s.toString().equalsIgnoreCase(stringIn)) return Runite.getTitle(s.toString());
            }
            return null;
        }
        return null;
    }

    public T getCorrectOption(String stringIn) {
        if (mode == Mode.ENUM) {
            for (T s : options) {
                if (s.toString().equalsIgnoreCase(stringIn)) return s;
            }
            return null;
        }
        return null;
    }

    public void setEnumValue(String value) {
        for (Enum e : ((Enum) this.value).getClass().getEnumConstants()) {
            if (e.name().equalsIgnoreCase(value)) {
                T old = this.value;
                this.value = (T) e;
                if (changeTask != null) {
                    changeTask.accept(new OnChangedSetting<>(old, (T) e));
                }
            }
        }
    }

    public T getMax() {
        return max;
    }

    public T getMin() {
        return min;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        if (this.value != value) {
            if (filter != null && !filter.test(value)) {
                return;
            }
            T old = this.value;
            this.value = value;
            if (changeTask != null) {
                changeTask.accept(new OnChangedSetting<>(old, value));
            }
        }
    }

    public boolean isMode(){
        return mode == Mode.MODE;
    }

    public boolean isToggle(){
        return mode == Mode.TOGGLE;
    }

    public boolean isNumber(){
        return mode == Mode.NUMBER;
    }

    public boolean isEnum(){
        return mode == Mode.ENUM;
    }

    public boolean isBind() {
        return mode == Mode.BIND;
    }

    public Setting<T> onChanged(Consumer<OnChangedSetting<T>> run) {
        this.changeTask = run;
        return this;
    }

    public Setting<T> visibleWhen(Predicate<T> predicate) {
        this.visibleCheck = predicate;
        return this;
    }

    public Setting<T> newValueFilter(Predicate<T> predicate) {
        this.filter = predicate;
        return this;
    }
    public Setting<T> withFilterError(String s) {
        this.filterError = s;
        return this;
    }

    public boolean isVisible() {
        if (visibleCheck == null) return true;
        return visibleCheck.test(value);
    }
}
