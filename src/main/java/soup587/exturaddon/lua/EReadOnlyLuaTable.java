package soup587.exturaddon.lua;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class EReadOnlyLuaTable extends LuaTable {
    public EReadOnlyLuaTable() {}
    public EReadOnlyLuaTable(LuaValue table) {
        presize(table.length(), 0);
        for (Varargs n = table.next(LuaValue.NIL); !n.arg1().isnil(); n = table.next(n.arg1())) {
            LuaValue key = n.arg1();
            LuaValue value = n.arg(2);
            super.rawset(key, value.istable() ? value == table ? this : new EReadOnlyLuaTable(value) : value);
        }
    }
    public LuaValue setmetatable(LuaValue metatable) { return error("table is read-only"); }
    public void set(int key, LuaValue value) { error("table is read-only"); }
    public void rawset(int key, LuaValue value) { error("table is read-only"); }
    public void rawset(LuaValue key, LuaValue value) { error("table is read-only"); }
    public LuaValue remove(int pos) { return error("table is read-only"); }
    public void javaset(int key, LuaValue value) { super.rawset(key,value); }
    public void javaset(LuaValue key, LuaValue value) { super.rawset(key,value); }
}
