package soup587.exturaddon.utils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import soup587.exturaddon.Exturaddon;

// laugh at my amazing pun Exturaddon + Translatable
public class Exturlatable extends TranslatableContents {

    public Exturlatable() {
        super(Exturaddon.MOD_ID, Exturaddon.MOD_FRIENDLY_NAME, NO_ARGS);
    }

    public Exturlatable(String string) {
        super(Exturaddon.MOD_ID + "." + string, null, NO_ARGS);
    }

    public Exturlatable(String string, Object... args) {
        super(Exturaddon.MOD_ID + "." + string, null, args);
    }

    public static MutableComponent of() {
        return MutableComponent.create(new Exturlatable());
    }

    public static MutableComponent of(String string) {
        return MutableComponent.create(new Exturlatable(string));
    }

    public static MutableComponent of(String string, Object... args) {
        return MutableComponent.create(new Exturlatable(string, args));
    }
}