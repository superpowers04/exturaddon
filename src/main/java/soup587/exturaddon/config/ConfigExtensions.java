package soup587.exturaddon.config;

import net.minecraft.ChatFormatting;
import org.figuramc.figura.backend2.NetworkStuff;
import org.figuramc.figura.config.*;
import org.figuramc.figura.utils.FiguraText;

import static org.figuramc.figura.config.Configs.SERVER_IP;

public class ConfigExtensions {
	public static final ConfigType.Category EXTURA_NETWORKING = new ConfigType.Category("Extura - Networking");

	public static final ConfigType.BoolConfig USE_SECURE_CLOUD = new ConfigType.BoolConfig("use_secure_cloud", EXTURA_NETWORKING, true) {
			@Override
			public void onChange() {
				super.onChange();
				NetworkStuff.reAuth();
			}
			{
				this.name = this.name.copy().withStyle(ChatFormatting.RED);
				this.tooltip = FiguraText.of("config.use_secure_cloud.tooltip");
			}
		};
	public static final ConfigType.BoolConfig BLOCK_CLOUD = new ConfigType.BoolConfig("block_cloud", EXTURA_NETWORKING, false) {
		@Override
		public void onChange() {
			super.onChange();
			NetworkStuff.reAuth();
		}
		{
			this.name = this.name.copy().withStyle(ChatFormatting.RED);
			this.tooltip = FiguraText.of("config.block_cloud.tooltip",SERVER_IP.defaultValue);
		}
	};
	public static final ConfigType.BoolConfig VANILLA_CLOUD = new ConfigType.BoolConfig("vanilla_cloud", EXTURA_NETWORKING, false) {
		@Override
		public void onChange() {
			super.onChange();
			NetworkStuff.reAuth();
		}
		{
			this.name = this.name.copy().withStyle(ChatFormatting.RED);
			this.tooltip = FiguraText.of("config.vanilla_cloud.tooltip");
		}
	};
	public static final ConfigType.BoolConfig USE_MC_HOST_RESOLVER = new ConfigType.BoolConfig("use_mc_host_resolver", EXTURA_NETWORKING, true) {
		@Override
		public void onChange() {
			super.onChange();
			NetworkStuff.reAuth();
		}
		{
			this.name = this.name.copy();
			this.tooltip = FiguraText.of("config.use_mc_host_resolver.tooltip");
		}
	};
}
