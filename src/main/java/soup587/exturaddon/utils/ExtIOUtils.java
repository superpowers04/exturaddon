package soup587.exturaddon.utils;

import soup587.exturaddon.Exturaddon;
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


public class ExtIOUtils {
	private static Matcher ValidFileMatcher = Pattern.compile(".*(avatar\\.json|avatar\\.jsonc|avatar\\.extura\\.json|(\\.lua|\\.bbmodel|\\.ogg|\\.png))$").matcher("");


	public static boolean isInvalid(Path path){
		try{

			return Files.isHidden(path) || 
				!(Files.isDirectory(path) || ValidFileMatcher.reset(path.getFileName().toString()).matches());
		}catch(IOException ignored){
			return true;
		}
	}

}