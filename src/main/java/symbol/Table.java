package symbol;

import java.util.Hashtable;

public class Table {
    private Hashtable<Symbol, Binder> dict = new Hashtable<Symbol, Binder>();
    private Symbol top;
    private Binder marks;

    public Table() {
    }

    public void put(Symbol key, Object value) {
        dict.put(key, new Binder(value, top, dict.get(key)));
        top = key;
    }

    public Object get(Symbol key) {
        Binder e = dict.get(key);
        if (e == null) return null;
        else return e.value;
    }

    public void beginScope() {
        marks = new Binder(null, top, marks);
        top = null;
    }

    public void endScope() {
        while (top != null) {
            Binder e = dict.get(top);
            if (e.tail != null) dict.put(top, e.tail);
            else dict.remove(top);
            top = e.prevtop;
        }
        top = marks.prevtop;
        marks = marks.tail;
    }
}