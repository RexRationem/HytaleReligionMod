package religion.controller;

import religion.data.Religion;
import religion.data.Role;
import religion.service.ReligionService;

import java.util.UUID;

public final class CombatHookController {

    private CombatHookController() {}

    public static void handleQualifiedPlayerKill(ReligionService religionService,
                                                 UUID killerId,
                                                 UUID victimId) {
        Religion killerReligion = religionService.getReligionByPlayer(killerId);
        Religion victimReligion = religionService.getReligionByPlayer(victimId);

        if (killerReligion == null || victimReligion == null) {
            return;
        }
        if (killerReligion.name.equals(victimReligion.name)) {
            return;
        }

        Role victimRole = victimReligion.getRole(victimId);
        religionService.recordHolyWarKill(killerId, victimId, victimRole);
        religionService.recordHolyWarDeath(victimId, victimRole);
    }
}