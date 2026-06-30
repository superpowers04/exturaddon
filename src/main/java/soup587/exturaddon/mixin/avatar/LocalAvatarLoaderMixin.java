package soup587.exturaddon.mixin.avatar;

import com.moulberry.mixinconstraints.annotations.IfModAbsent;

import org.figuramc.figura.FiguraMod;
import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.avatar.UserData;
import org.figuramc.figura.gui.FiguraToast;
import org.figuramc.figura.parsers.*;
import org.figuramc.figura.utils.FiguraResourceListener;
import org.figuramc.figura.utils.FiguraText;
import org.figuramc.figura.utils.IOUtils;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.zip.GZIPOutputStream;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.figuramc.figura.avatar.local.LocalAvatarLoader;

@Mixin(LocalAvatarLoader.class)
public class LocalAvatarLoaderMixin {
	@Unique
	private static Matcher ValidFileMatcher = Pattern.compile(".*(avatar\\.json|avatar\\.jsonc|avatar\\.extura\\.json|(\\.lua|\\.bbmodel|\\.ogg|\\.png))$").matcher("");
	@Shadow 
	private static final HashMap<Path, WatchKey> KEYS = new HashMap<>();
	@Shadow
	protected static void addWatchKey(Path path, BiConsumer<Path, WatchKey> consumer) {}
	/**
	 * Tick the watched key for hotswapping avatars
	 * Reload spans across multiple ticks to prevent a bunch of rapid filechanges causing a bunch of reloads
	 */
	@Unique
	private static boolean queuedReload = false;
	/* I know overwrite isn't recommended but I'm literally replacing the entire function to make it- idk WORK*/
	@Overwrite
	public static void tick() {
		WatchEvent<?> event = null;
		if(queuedReload){
			queuedReload = false;
			AvatarManager.loadLocalAvatar(LocalAvatarLoader.getLastLoadedPath());
			return;
		}
		try{

			Set<Map.Entry<Path, WatchKey>> entries = KEYS.entrySet();
			if(LocalAvatarLoader.IS_WINDOWS){ // This literally just removes one unix-only check, but it prevents some useless looping :3
				for (Map.Entry<Path, WatchKey> entry : entries) {
					if(entry == null) continue;
					WatchKey key = entry.getValue();
					if (!key.isValid())
						continue;

					for (WatchEvent<?> watchEvent : key.pollEvents()) {
						if (watchEvent.kind() == StandardWatchEventKinds.OVERFLOW)
							continue;

						event = watchEvent;
						Path path = entry.getKey().resolve((Path) event.context());
						String name = IOUtils.getFileNameOrEmpty(path);

						if (IOUtils.isHidden(path) || !(Files.isDirectory(path) || ValidFileMatcher.reset(name).matches()))
							continue;
						queuedReload = true;
						FiguraMod.debug("Detected file changes in the Avatar directory (" + event.context().toString() + "), reloading!");
						return;
					}
				}
				return;
			}
			boolean reload = false;
			for (Map.Entry<Path, WatchKey> entry : entries) {
				if(entry == null) continue;
				WatchKey key = entry.getValue();
				if (!key.isValid())
					continue;

				for (WatchEvent<?> watchEvent : key.pollEvents()) {
					WatchEvent.Kind<?> kind = watchEvent.kind();
					if (kind == StandardWatchEventKinds.OVERFLOW)
						continue;

					event = watchEvent;
					Path path = entry.getKey().resolve((Path) event.context());
					String name = IOUtils.getFileNameOrEmpty(path);

					if (IOUtils.isHidden(path) || !(Files.isDirectory(path) || ValidFileMatcher.reset(name).matches()))
						continue;

					// This is it, this is the Unix-only check. I(superpowers04) dunno why only Unix needs to add paths like this
					// Learned this is possibly due to a bug with how Windows handles events, requiring the extendedwatchmodifier?
					if (kind == StandardWatchEventKinds.ENTRY_CREATE) 
						addWatchKey(path, KEYS::put);

					reload = true;

				}
			}

			// reload avatar
			if (reload) {
				queuedReload = true;
				FiguraMod.debug("Detected file changes in the Avatar directory (" + event.context().toString() + "), Reloading on next tick!");
			}
		}catch(java.util.ConcurrentModificationException meow){
			FiguraMod.debug("LocalAvatarLoader.java:tick java.util.ConcurrentModificationException ignored");
		}
	}
}
