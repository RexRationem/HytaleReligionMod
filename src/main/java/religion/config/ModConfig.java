package religion.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

import java.util.ArrayList;
import java.util.List;

public class ModConfig {

    public static final BuilderCodec<ModConfig> CODEC = BuilderCodec.builder(ModConfig.class, ModConfig::new)
            .append(new KeyedCodec<>("BasePietyCap", Codec.INTEGER), (c, v) -> c.basePietyCap = v, c -> c.basePietyCap).add()
            .append(new KeyedCodec<>("PietyPerLayman", Codec.INTEGER), (c, v) -> c.pietyPerLayman = v, c -> c.pietyPerLayman).add()
            .append(new KeyedCodec<>("MiracleCost", Codec.INTEGER), (c, v) -> c.miracleCost = v, c -> c.miracleCost).add()
            .append(new KeyedCodec<>("MiracleChanceCapPercent", Codec.DOUBLE), (c, v) -> c.miracleChanceCapPercent = v, c -> c.miracleChanceCapPercent).add()
            .append(new KeyedCodec<>("HolyWarDeclarationCost", Codec.INTEGER), (c, v) -> c.holyWarDeclarationCost = v, c -> c.holyWarDeclarationCost).add()
            .append(new KeyedCodec<>("HolyWarDurationHours", Codec.INTEGER), (c, v) -> c.holyWarDurationHours = v, c -> c.holyWarDurationHours).add()
            .append(new KeyedCodec<>("HolyWarRepeatKillCooldownMinutes", Codec.INTEGER), (c, v) -> c.holyWarRepeatKillCooldownMinutes = v, c -> c.holyWarRepeatKillCooldownMinutes).add()
            .append(new KeyedCodec<>("ConfessionPietyGain", Codec.INTEGER), (c, v) -> c.confessionPietyGain = v, c -> c.confessionPietyGain).add()
            .append(new KeyedCodec<>("ConfessionCooldownHours", Codec.INTEGER), (c, v) -> c.confessionCooldownHours = v, c -> c.confessionCooldownHours).add()
            .append(new KeyedCodec<>("ApostasyHours", Codec.INTEGER), (c, v) -> c.apostasyHours = v, c -> c.apostasyHours).add()
            .append(new KeyedCodec<>("CurseFreezeHours", Codec.INTEGER), (c, v) -> c.curseFreezeHours = v, c -> c.curseFreezeHours).add()
            .append(new KeyedCodec<>("AltarMinChunkDistance", Codec.INTEGER), (c, v) -> c.altarMinChunkDistance = v, c -> c.altarMinChunkDistance).add()
            .append(new KeyedCodec<>("AltarDistanceIsCenterToCenter", Codec.BOOLEAN), (c, v) -> c.altarDistanceIsCenterToCenter = v, c -> c.altarDistanceIsCenterToCenter).add()
            .append(new KeyedCodec<>("PilgrimageTimeWindowMinutes", Codec.INTEGER), (c, v) -> c.pilgrimageTimeWindowMinutes = v, c -> c.pilgrimageTimeWindowMinutes).add()
            .append(new KeyedCodec<>("PilgrimageCooldownDays", Codec.INTEGER), (c, v) -> c.pilgrimageCooldownDays = v, c -> c.pilgrimageCooldownDays).add()
            .append(new KeyedCodec<>("PilgrimageRewardPiety", Codec.INTEGER), (c, v) -> c.pilgrimageRewardPiety = v, c -> c.pilgrimageRewardPiety).add()
            .append(new KeyedCodec<>("BannedItemPenaltyPiety", Codec.INTEGER), (c, v) -> c.bannedItemPenaltyPiety = v, c -> c.bannedItemPenaltyPiety).add()
            .append(new KeyedCodec<>("BannedItemPenaltyCooldownMinutes", Codec.INTEGER), (c, v) -> c.bannedItemPenaltyCooldownMinutes = v, c -> c.bannedItemPenaltyCooldownMinutes).add()
            .append(new KeyedCodec<>("EncouragedItemMaxBonusPercent", Codec.DOUBLE), (c, v) -> c.encouragedItemMaxBonusPercent = v, c -> c.encouragedItemMaxBonusPercent).add()
            .append(new KeyedCodec<>("DoctrineRespecCostPiety", Codec.INTEGER), (c, v) -> c.doctrineRespecCostPiety = v, c -> c.doctrineRespecCostPiety).add()
            .append(new KeyedCodec<>("RelicSacrificeBonusFlat", Codec.INTEGER), (c, v) -> c.relicSacrificeBonusFlat = v, c -> c.relicSacrificeBonusFlat).add()
            .append(new KeyedCodec<>("StolenRelicDailyDrainPiety", Codec.INTEGER), (c, v) -> c.stolenRelicDailyDrainPiety = v, c -> c.stolenRelicDailyDrainPiety).add()
            .append(new KeyedCodec<>("StolenRelicDailyGainPiety", Codec.INTEGER), (c, v) -> c.stolenRelicDailyGainPiety = v, c -> c.stolenRelicDailyGainPiety).add()
            .append(new KeyedCodec<>("RelicConsecrationCostPiety", Codec.INTEGER), (c, v) -> c.relicConsecrationCostPiety = v, c -> c.relicConsecrationCostPiety).add()
            .append(new KeyedCodec<>("HolidayFreeMiracleRollsPerHoliday", Codec.INTEGER), (c, v) -> c.holidayFreeMiracleRollsPerHoliday = v, c -> c.holidayFreeMiracleRollsPerHoliday).add()
            .build();

    public int basePietyCap = 100;
    public int pietyPerLayman = 100;
    public int miracleCost = 100;
    public double miracleChanceCapPercent = 50.0;
    public int holyWarDeclarationCost = 500;
    public int holyWarDurationHours = 24;
    public int holyWarRepeatKillCooldownMinutes = 5;
    public int confessionPietyGain = 10;
    public int confessionCooldownHours = 24;
    public int apostasyHours = 24;
    public int curseFreezeHours = 6;
    public int altarMinChunkDistance = 5;
    public boolean altarDistanceIsCenterToCenter = true;
    public int pilgrimageTimeWindowMinutes = 60;
    public int pilgrimageCooldownDays = 7;
    public int pilgrimageRewardPiety = 100;
    public int bannedItemPenaltyPiety = 10;
    public int bannedItemPenaltyCooldownMinutes = 60;
    public double encouragedItemMaxBonusPercent = 50.0;
    public int doctrineRespecCostPiety = 500;
    public int relicSacrificeBonusFlat = 5;
    public int stolenRelicDailyDrainPiety = 25;
    public int stolenRelicDailyGainPiety = 25;
    public int relicConsecrationCostPiety = 500;
    public int holidayFreeMiracleRollsPerHoliday = 1;

    public List<String> miracleBuffPool = new ArrayList<>();
    public List<String> miracleItemPool = new ArrayList<>();

    public ModConfig() {
        miracleBuffPool.add("STAMINA_REGEN");
        miracleBuffPool.add("HEALTH_REGEN");
        miracleBuffPool.add("MOVE_SPEED");
        miracleBuffPool.add("DAMAGE_BOOST");
        miracleBuffPool.add("DEFENSE_BOOST");
        miracleBuffPool.add("GATHER_SPEED");

        miracleItemPool.add("Ingredient_Gold_Bar");
        miracleItemPool.add("Consumable_HealthPotion");
        miracleItemPool.add("Consumable_Food_Meat_Cooked");
    }
}