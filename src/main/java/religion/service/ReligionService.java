package religion.service;

import religion.api.BlockPos;
import religion.api.ServerBridge;
import religion.config.ModConfig;
import religion.data.*;

import java.util.*;

public class ReligionService {

    private final ModConfig config;
    private final ServerBridge bridge;
    private final CooldownService cooldownService;
    private final PietyService pietyService;
    private final MiracleService miracleService;
    private final HolyWarService holyWarService;
    private final RelicService relicService;
    private final ConfessionService confessionService;
    private final AltarPlacementService altarPlacementService;
    private final EventLogService eventLog;
    private final PilgrimageService pilgrimageService;
    private final PersistenceService persistenceService;

    private final Map<String, Religion> religionsByName = new HashMap<>();
    private final Map<UUID,String> playerReligion = new HashMap<>();

    public ReligionService(
            ModConfig config,
            ServerBridge bridge,
            CooldownService cooldownService,
            PietyService pietyService,
            MiracleService miracleService,
            HolyWarService holyWarService,
            RelicService relicService,
            ConfessionService confessionService,
            AltarPlacementService altarPlacementService,
            EventLogService eventLog,
            PilgrimageService pilgrimageService,
            PersistenceService persistenceService
    ){

        this.config=config;
        this.bridge=bridge;
        this.cooldownService=cooldownService;
        this.pietyService=pietyService;
        this.miracleService=miracleService;
        this.holyWarService=holyWarService;
        this.relicService=relicService;
        this.confessionService=confessionService;
        this.altarPlacementService=altarPlacementService;
        this.eventLog=eventLog;
        this.pilgrimageService=pilgrimageService;
        this.persistenceService=persistenceService;
    }

    public void loadAll(){

        religionsByName.clear();
        playerReligion.clear();

        for(Religion r : persistenceService.loadAll()){

            religionsByName.put(r.name,r);

            for(UUID id : r.members.keySet()){
                playerReligion.put(id,r.name);
            }

            pietyService.recomputeCap(r,bridge.getCurrentEpochMillis());
        }
    }

    public void saveAll(){
        persistenceService.saveAll(religionsByName.values());
    }

    public Religion createReligion(String name, UUID prophet, BlockPos altar){

        long now = bridge.getCurrentEpochMillis();

        Religion r = new Religion(name,prophet,now);

        r.mainAltarPos = altar;
        r.altars.add(altar);

        religionsByName.put(name,r);
        playerReligion.put(prophet,name);

        pietyService.recomputeCap(r,now);

        eventLog.add(r,now,"RELIGION_FOUNDED","Prophet="+prophet);

        bridge.broadcast("[Religion] "+name+" has been founded.");

        return r;
    }

    public Religion getReligionByPlayer(UUID player){

        String name = playerReligion.get(player);

        if(name==null) return null;

        return religionsByName.get(name);
    }

    public boolean joinReligion(UUID player,String name){

        long now = bridge.getCurrentEpochMillis();

        if(playerReligion.containsKey(player)) return false;

        if(cooldownService.isOnCooldown("apostasy:"+player,now)) return false;

        Religion r = religionsByName.get(name);

        if(r==null) return false;

        r.members.put(player,new MemberRecord(player,Role.LAYMAN,now));

        playerReligion.put(player,name);

        pietyService.recomputeCap(r,now);

        eventLog.add(r,now,"MEMBER_JOIN","Player="+player);

        return true;
    }

    public boolean leaveReligion(UUID player){

        Religion r = getReligionByPlayer(player);

        if(r==null) return false;

        long now = bridge.getCurrentEpochMillis();

        r.members.remove(player);

        playerReligion.remove(player);

        cooldownService.setCooldown("apostasy:"+player,now,config.apostasyHours*3600000L);

        pietyService.recomputeCap(r,now);

        return true;
    }

    public boolean confess(UUID player,boolean encouraged,boolean holiday){

        Religion r = getReligionByPlayer(player);

        if(r==null) return false;

        return confessionService.confess(r,player,bridge.getCurrentEpochMillis(),encouraged,holiday);
    }

    public boolean prayForMiracle(UUID player){

        Religion r = getReligionByPlayer(player);

        if(r==null) return false;

        Role role = r.getRole(player);

        if(role!=Role.PROPHET && role!=Role.PRIEST) return false;

        return miracleService.attemptMiracle(r,bridge.getCurrentEpochMillis());
    }

    public boolean declareHolyWar(UUID prophet,String target){

        Religion attacker = getReligionByPlayer(prophet);

        Religion defender = religionsByName.get(target);

        if(attacker==null || defender==null) return false;

        if(attacker.getRole(prophet)!=Role.PROPHET) return false;

        return holyWarService.declareHolyWar(attacker,defender,bridge.getCurrentEpochMillis());
    }

    public RelicRecord consecrateRelic(UUID prophet,String item){

        Religion r = getReligionByPlayer(prophet);

        if(r==null) return null;

        if(r.getRole(prophet)!=Role.PROPHET) return null;

        long now = bridge.getCurrentEpochMillis();

        if(!pietyService.spendPiety(r,config.relicConsecrationCostPiety,now,"RELIC_CONSECRATION"))
            return null;

        return relicService.consecrate(r,item,now);
    }

    public void recordHolyWarKill(UUID killer,UUID victim,Role victimRole){

        Religion killerReligion = getReligionByPlayer(killer);

        if(killerReligion==null) return;

        holyWarService.handleKill(killerReligion,victimRole,killer,victim,bridge.getCurrentEpochMillis());
    }

    public void recordHolyWarDeath(UUID victim,Role role){

        Religion r = getReligionByPlayer(victim);

        if(r==null) return;

        holyWarService.handleDeathPenalty(r,role,bridge.getCurrentEpochMillis());
    }

    public void handleBannedItemUse(UUID player,String item){

        Religion r = getReligionByPlayer(player);

        if(r==null) return;

        long now = bridge.getCurrentEpochMillis();

        String key="banned:"+player+":"+item;

        if(cooldownService.isOnCooldown(key,now)) return;

        cooldownService.setCooldown(key,now,config.bannedItemPenaltyCooldownMinutes*60000L);

        pietyService.removePiety(r,config.bannedItemPenaltyPiety,now,"BANNED_ITEM");
    }

    public Collection<Religion> getAllReligions(){
        return Collections.unmodifiableCollection(religionsByName.values());
    }
}