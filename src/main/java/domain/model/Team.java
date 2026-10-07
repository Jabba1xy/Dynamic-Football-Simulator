package domain.model;

import config.GameConstants;
import domain.enums.MatchOutcome;
import data.TeamSaveData;
import domain.enums.SimulationType;
import service.engine.DynamicRatingEquation;

import java.util.List;

/**
 * Represents a football team within the simulation.
 *
 * <p>A team stores its identity, current rating, match statistics,
 * long-term performance history, rating modifiers, and recent match form.</p>
 *
 * <p>The team's rating is calculated by the
 * {@link DynamicRatingEquation}, which applies the appropriate rating
 * rules based on the current simulation.</p>
 */
public class Team {

    private static final DynamicRatingEquation DRE =
            new DynamicRatingEquation();
    private final MatchHistory matchHistory = new MatchHistory();

    // TEAMS INFO
    private final String name;

    // TEAM STATS
    private boolean grandChamps = false;
    private int rating;
    private int points = 0;
    private int played = 0;
    private int wins = 0;
    private int draws = 0;
    private int losses = 0;
    private int goalsFor = 0;
    private int goalsAgainst = 0;
    private int ratingBuff = 0 ;
    private float leagueBuff = 1f;
    private float teamStrengthBuff = 1f;
    private int positionBuff = 0;
    private int totalPlayed = 0;
    private int totalWins = 0;
    private int totalDraws = 0;
    private int totalLosses = 0;
    private int totalGoalsFor = 0;
    private int totalGoalsAgainst = 0;

    /**
     * Creates a new team with the given name and the minimum starting rating.
     *
     * @param name the team's display name
     */
    public Team(String name) {
        this.name = name;
        this.rating = GameConstants.MINIMUM_RATING;
    }

    // GETTERS
    public String getName() {
        return name;
    }

    public int getRating() { return rating;}
    public int getPoints() { return points; }
    public int getPlayed() { return played; }
    public int getWins() { return wins; }
    public int getDraws() { return draws; }
    public int getLosses() { return losses; }
    public int getGoalsFor() { return goalsFor; }
    public int getGoalsAgainst() { return goalsAgainst; }
    public int getGoalDifference() { return goalsFor - goalsAgainst; }
    public float getLeagueBuff() { return leagueBuff; }
    public float getTeamStrengthBuff() { return teamStrengthBuff; }
    public float getGoalsPerGame() { return (float) goalsFor / played; }
    public int getPositionBuff() { return positionBuff; }
    public int getTotalPlayed() { return totalPlayed; }
    public int getTotalWins() { return totalWins; }
    public int getTotalDraws() { return totalDraws; }
    public int getTotalLosses() { return totalLosses; }
    public int getTotalGoalsFor() { return totalGoalsFor; }
    public int getTotalGoalsAgainst() { return totalGoalsAgainst; }
    public int getTotalGoalDifference() { return totalGoalsFor - totalGoalsAgainst; }
    public int getRatingBuff() { return ratingBuff; }
    public boolean isWorldChampion() { return grandChamps; }
    public List<MatchOutcome> getRecentForm() {
        return matchHistory.getHistory();
    }
    public double getRecentWinPercentage() {
        return matchHistory.getWinPercentage();
    }
    public double getRecentDrawPercentage() {
        return matchHistory.getDrawPercentage();
    }

    /**
     * Recalculates the team's rating using the dynamic rating equation.
     *
     * <p>The calculation considers the team's performance, goal difference,
     * rating modifiers, league or team-strength modifiers, position, and
     * achievement bonuses. The supplied simulation type determines which
     * simulation-specific rating rules are applied.</p>
     *
     * @param type the simulation type used to determine rating rules
     * @param isGroupStage whether the team is currently in the World Cup group stage
     */
    public void calculateRating(SimulationType type, boolean isGroupStage) {
        rating = DRE.calculate(this, type, isGroupStage);
    }

    // SETTERS
    public void setPositionBuff(int pos) { this.positionBuff = pos; }
    public void setLeagueBuff(float value) { this.leagueBuff = value; }
    public void setTeamStrengthBuff(float value) { this.teamStrengthBuff = value; }
    public void setWorldChamps(boolean bool) {
        this.grandChamps = bool;
    }

    // UPDATE TEAM STATS
    /**
     * Records a match win and updates the team's statistics.
     *
     * <p>Season statistics are always updated. Long-term statistics and
     * match history are updated only when {@code calculateTotal} is true.</p>
     *
     * @param goalsFor goals scored by this team
     * @param goalsAgainst goals conceded by this team
     * @param calculateTotal whether long-term statistics and match history should be updated
     */
    public void recordWin(int goalsFor, int goalsAgainst, boolean calculateTotal) {
        incrementPlayed();
        incrementWin();
        addGoals(goalsFor, goalsAgainst);
        addPoints(MatchOutcome.WIN.getPoints());

        if (calculateTotal) {
            incrementTotalPlayed();
            incrementTotalWin();
            addTotalGoals(goalsFor, goalsAgainst);
            matchHistory.addResult(MatchOutcome.WIN);
        }
    }

    /**
     * Records a match draw and updates the team's statistics.
     *
     * <p>Season statistics are always updated. When {@code calculateTotal}
     * is {@code true}, the result is also added to the team's long-term
     * statistics and recent match history.</p>
     *
     * @param goalsFor goals scored by this team
     * @param goalsAgainst goals scored by the opponent
     * @param calculateTotal whether the result should be included in
     * long-term statistics and recent form
     */
    public void recordDraw(int goalsFor, int goalsAgainst, boolean calculateTotal) {
        incrementPlayed();
        incrementDraw();
        addPoints(MatchOutcome.DRAW.getPoints());
        addGoals(goalsFor, goalsAgainst);

        if (calculateTotal) {
            incrementTotalPlayed();
            incrementTotalDraw();
            addTotalGoals(goalsFor, goalsAgainst);
            matchHistory.addResult(MatchOutcome.DRAW);
        }
    }

    /**
     * Records a match loss and updates the team's statistics.
     *
     * <p>Season statistics are always updated. When {@code calculateTotal}
     * is {@code true}, the result is also added to the team's long-term
     * statistics and recent match history.</p>
     *
     * @param goalsFor goals scored by this team
     * @param goalsAgainst goals scored by the opponent
     * @param calculateTotal whether the result should be included in
     *                       long-term statistics and recent form
     */
    public void recordLoss(int goalsFor, int goalsAgainst, boolean calculateTotal) {
        incrementPlayed();
        incrementLoss();
        addGoals(goalsFor, goalsAgainst);

        if (calculateTotal) {
            incrementTotalPlayed();
            incrementTotalLoss();
            addTotalGoals(goalsFor, goalsAgainst);
            matchHistory.addResult(MatchOutcome.LOSS);
        }

    }
    private void addPoints(int value) {
        points += value;
    }
    private void incrementPlayed() {
        played++;
    }
    private void incrementWin() {
        wins++;
    }
    private void incrementDraw() {
        draws++;
    }
    private void incrementLoss() {
        losses++;
    }
    private void addGoals(int goalsFor, int goalsAgainst) {
        this.goalsFor += goalsFor;
        this.goalsAgainst += goalsAgainst;
    }
    private void addTotalGoals(int goalsFor, int goalsAgainst) {
        this.totalGoalsFor += goalsFor;
        this.totalGoalsAgainst += goalsAgainst;
    }
    private void incrementTotalPlayed() {
        totalPlayed++;
    }
    private void incrementTotalWin() {
        totalWins++;
    }
    private void incrementTotalDraw() {
        totalDraws++;
    }
    private void incrementTotalLoss() { totalLosses++; }
    public void addRatingBuff(int value) {
        ratingBuff += value;
    }

    // HELPER METHODS
    /**
     * Resets season-specific statistics while preserving long-term statistics,
     * rating, and match history.
     */
    public void resetStats() {
        points = 0;
        wins = 0;
        draws = 0;
        losses = 0;
        goalsFor = 0;
        goalsAgainst = 0;
        played = 0;
    }

    /**
     * Restores the team's persistent state from saved data.
     *
     * <p>Restores the current rating, long-term statistics, rating modifiers,
     * World Cup strength modifier, championship status, and recent match form.</p>
     *
     * @param save the saved team data
     */
    public void restore(TeamSaveData save) {

        rating = save.rating();

        totalGoalsFor = save.totalGoalsFor();
        totalGoalsAgainst = save.totalGoalAgainst();

        totalPlayed = save.totalPlayed();
        totalWins = save.totalWins();
        totalDraws = save.totalDraws();
        totalLosses = save.totalLosses();

        positionBuff = save.positionBuff();
        ratingBuff = save.ratingBuff();
        teamStrengthBuff = save.teamStrength();
        grandChamps = save.grandChampion();

        matchHistory.restoreHistory(save.recentForm());
    }

    @Override
    public String toString() {
        return name + " (" + getRating() + ")";
    }
}
