package data;

import domain.dto.GrandChampionPodium;

import java.util.List;

/**
 * Stores the persisted state of a table league career.
 *
 * <p>Contains the three active league tiers and the historical
 * Grand Championship podium results.</p>
 *
 * @param champion the saved Champion league
 * @param diamond the saved Diamond league
 * @param platinum the saved Platinum league
 * @param history historical Grand Championship results
 */
public record TableLeagueSaveData(
        LeagueSaveData champion,
        LeagueSaveData diamond,
        LeagueSaveData platinum,
        List<GrandChampionPodium> history
) {}