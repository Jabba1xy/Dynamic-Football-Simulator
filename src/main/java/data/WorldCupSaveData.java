package data;

import domain.dto.GrandChampionPodium;

import java.util.List;

/**
 * Stores the persisted state of a World Cup simulation.
 *
 * <p>Contains the participating teams and historical Grand Championship
 * podium results required to restore the simulation.</p>
 *
 * @param teams saved team states
 * @param history historical Grand Championship results
 */
public record WorldCupSaveData(
        List<TeamSaveData> teams,
        List<GrandChampionPodium> history
) {}