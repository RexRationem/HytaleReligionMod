package religion;

import com.hypixel.hytale.server.core.command.system.CommandRegistry;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import religion.command.ReligionCommand;
import religion.config.ModConfig;
import religion.interaction.ReligionAltarInteraction;
import religion.service.CooldownService;
import religion.service.EventLogService;
import religion.service.PietyService;

public final class ReligionMod extends JavaPlugin {

    private final ModConfig config = new ModConfig();

    private EventLogService eventLogService;
    private CooldownService cooldownService;
    private PietyService pietyService;

    public ReligionMod(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        eventLogService = new EventLogService();
        cooldownService = new CooldownService();
        pietyService = new PietyService(config, eventLogService);

        getCodecRegistry(Interaction.CODEC)
                .register("religion_altar_interaction", ReligionAltarInteraction.class, ReligionAltarInteraction.CODEC);

        CommandRegistry commandRegistry = getCommandRegistry();
        commandRegistry.registerCommand(new ReligionCommand());

        getLogger().atInfo().log("[ReligionMod] Loaded.");
    }

    @Override
    protected void shutdown() {
        getLogger().atInfo().log("[ReligionMod] Shutdown.");
    }

    public ModConfig getModConfig() {
        return config;
    }

    public EventLogService getEventLogService() {
        return eventLogService;
    }

    public CooldownService getCooldownService() {
        return cooldownService;
    }

    public PietyService getPietyService() {
        return pietyService;
    }
}