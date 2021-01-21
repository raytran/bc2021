import torch as t
import torch.nn as nn
import torch.optim as optim
import subprocess
import random as rand
import pickle
from collections import OrderedDict
import os

home = os.getcwd()


# 0 : vote kP
# 1 : vote kI
# 2 : vote kD
# 3 : bot kP
# 4 : bot kI
# 5 : bot kD
# 6 : hp kP
# 7 : hp kI
# 8 : hp kD
class stateInput:
    def __init__(self, input, team):
        self.tensor = t.tensor([input])
        self.values = input
        self.team = team
        assert self.tensor.shape == t.Size([1, 9])

    def print(self):
        actions = self.tensor.flatten()
        print(f'team: {self.team}\n'
              f'vote kP: {actions[0]} kI: {actions[1]} kD: {actions[2]}\n'
              f'bot kP: {actions[3]} kI: {actions[4]} kD: {actions[5]}\n'
              f'hp kP: {actions[6]} kI: {actions[7]} kD: {actions[8]}\n')


red_file = """package baseplayer3.eccontrollers;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import baseplayer3.BotEnlightenment;
import baseplayer3.Utilities;

public class ECBudgetController implements ECController {
    private final RobotController rc;
    private final BotEnlightenment ec;
    private final ECTargetController tc;
    private final PIDBudgetVariable voteDelta = new PIDBudgetVariable(%s, %s, %s);
    private final PIDBudgetVariable botDelta = new PIDBudgetVariable(%s, %s, %s);
    private final PIDBudgetVariable hpDelta = new PIDBudgetVariable(%s, %s, %s);

    private int voteBudget;
    private int botBudget;
    private int hpBudget;


    public ECBudgetController(RobotController rc, BotEnlightenment ec) {
        this.rc = rc;
        this.ec = ec;
        this.tc = new ECTargetController(rc, ec);
    }

    @Override
    public void run() throws GameActionException {
        int currentInfluence = rc.getInfluence();
        int currentRound = rc.getRoundNum();
        if (currentRound == 1) {
            voteBudget = 1;
        }

        int income = currentInfluence - voteBudget - botBudget - hpBudget;
        //System.out.println("Income is: " + income);
        if (income > 0) {
            tc.updateHeuristics();
            tc.updateTargets();

            double voteTarget = tc.getVoteTarget();
            double botTarget = tc.getBotTarget();
            double hpTarget = tc.getHpTarget();

            if (hpBudget > hpTarget) {
                int diff = (int) (hpBudget - hpTarget);
                hpBudget -= diff;
                income += diff;
            }

            // check
            //System.out.println("Game state vars:\\nSafety Eval: " + ec.getSafetyEval() + "\\nAvg Safety: " + ec.getAvgSafetyEval()
            //+ "\\nAvg Influence Change: " + ec.getAvgInfluenceChange() + "\\nAvg Bot Change: " + ec.getAvgBotChange());

            // set new targets
            voteDelta.setTarget(voteTarget);
            botDelta.setTarget(botTarget);
            hpDelta.setTarget(hpTarget);

            // update deltas
            voteDelta.update(rc.getTeamVotes());
            botDelta.update(ec.getLocalRobotCount());
            hpDelta.update(hpBudget);

            // normalize to positive percentages
            double voteDValue = voteDelta.getValue();
            double botDValue = botDelta.getValue();
            double hpDValue = hpDelta.getValue();
            //System.out.println("Deltas:\\nVote: " + voteDValue + "\\nBot: " + botDValue + "\\nHP: " + hpDValue);
            double total = voteDValue + botDValue + hpDValue;
            if (total > 0) {
                voteDValue *= 1. / total;
                botDValue *= 1. / total;
            } else {
                voteDValue = 0;
                botDValue = 1;
            }
            //System.out.println("Normalized Deltas:\\n" + "Vote: " + voteDValue + "\\nBot: " + botDValue);

            int voteAllocation;
            int botAllocation;
            int hpAllocation;
            try {
                voteAllocation = (int) Math.round(voteDValue * income);
                botAllocation = (int) Math.round(botDValue * income);
                hpAllocation = income - voteAllocation - botAllocation;
                assert voteAllocation >= 0 && botAllocation >= 0 && hpAllocation >= 0;
                assert income >= voteAllocation + botAllocation + hpAllocation;
            } catch (AssertionError e) {
                voteAllocation = (int) Math.floor(voteDValue * income);
                botAllocation = (int) Math.floor(botDValue * income);
                hpAllocation = income - voteAllocation - botAllocation;
            }

            //if there are nearby enemy politicians, make sure we have enough hp
            /**double safetyEval = ec.getSafetyEval();
            if (hpBudget <= safetyEval && hpBudget != 0) {
                if (currentInfluence > safetyEval + 1) {
                    hpBudget = (int) safetyEval + 1;
                    int remainingInfluence = currentInfluence - hpBudget;
                    double newTotal = voteDValue + botDValue;
                    voteAllocation = (int) (voteDValue / newTotal * remainingInfluence);
                    botAllocation = remainingInfluence - (voteBudget + voteAllocation);
                } else {
                    voteAllocation = 0;
                    botAllocation = 0;
                    hpBudget += income - 1;
                    botBudget += 1;
                }
            }**/

            if (rc.getTeamVotes() >= Utilities.VOTE_WIN) {
                botBudget += voteAllocation + botAllocation + voteBudget;
                voteBudget = 0;
            } else {
                voteBudget += voteAllocation;
                botBudget += botAllocation;
            }

            if(voteBudget > 2 * ec.getPrevVoteAmount() * ec.getPrevVoteMult() + 1) {
                int diff = voteBudget - 2 * ec.getPrevVoteAmount() * ec.getPrevVoteMult() - 1;
                voteBudget = 2 * ec.getPrevVoteAmount() * ec.getPrevVoteMult() + 1;
                botBudget += diff;
            }

            hpBudget += hpAllocation;

            //System.out.println("Total influence: " + currentInfluence + "\\nVoting Budget: "
                    //+ voteBudget + "\\nBot Budget: " + botBudget + "\\nSaving: " + hpBudget);
        }
    }

    /**
     * @return budget for making bids
     */
    public int getVoteBudget() {
        return voteBudget;
    }

    /**
     * @return budget for making bots
     */
    public int getBotBudget() {
        return botBudget;
    }

    /**
     * @return how much influence to save
     */
    public int getHpBudget() {
        return hpBudget;
    }

    public void withdrawBudget(ECSpawnController sc, int amount) { botBudget -= amount; }

    public void withdrawBudget(ECVoteController vc, int amount) { voteBudget -= amount; }

    public boolean canSpend (ECSpawnController sc, int amount) { return botBudget >= amount; }

    public boolean canSpend (ECVoteController vc, int amount) { return voteBudget >= amount; }
}
"""
blue_file = """package baseplayer2.eccontrollers;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import baseplayer2.BotEnlightenment;
import baseplayer2.Utilities;

public class ECBudgetController implements ECController {
    private final RobotController rc;
    private final BotEnlightenment ec;
    private final ECTargetController tc;
    private final PIDBudgetVariable voteDelta = new PIDBudgetVariable(%s, %s, %s);
    private final PIDBudgetVariable botDelta = new PIDBudgetVariable(%s, %s, %s);
    private final PIDBudgetVariable hpDelta = new PIDBudgetVariable(%s, %s, %s);

    private int voteBudget;
    private int botBudget;
    private int hpBudget;


    public ECBudgetController(RobotController rc, BotEnlightenment ec) {
        this.rc = rc;
        this.ec = ec;
        this.tc = new ECTargetController(rc, ec);
    }

    @Override
    public void run() throws GameActionException {
        int currentInfluence = rc.getInfluence();
        int currentRound = rc.getRoundNum();
        if (currentRound == 1) {
            voteBudget = 1;
        }

        int income = currentInfluence - voteBudget - botBudget - hpBudget;
        //System.out.println("Income is: " + income);
        if (income > 0) {
            tc.updateHeuristics();
            tc.updateTargets();

            double voteTarget = tc.getVoteTarget();
            double botTarget = tc.getBotTarget();
            double hpTarget = tc.getHpTarget();

            if (hpBudget > hpTarget) {
                int diff = (int) (hpBudget - hpTarget);
                hpBudget -= diff;
                income += diff;
            }

            // check
            //System.out.println("Game state vars:\\nSafety Eval: " + ec.getSafetyEval() + "\\nAvg Safety: " + ec.getAvgSafetyEval()
            //+ "\\nAvg Influence Change: " + ec.getAvgInfluenceChange() + "\\nAvg Bot Change: " + ec.getAvgBotChange());

            // set new targets
            voteDelta.setTarget(voteTarget);
            botDelta.setTarget(botTarget);
            hpDelta.setTarget(hpTarget);

            // update deltas
            voteDelta.update(rc.getTeamVotes());
            botDelta.update(ec.getLocalRobotCount());
            hpDelta.update(hpBudget);

            // normalize to positive percentages
            double voteDValue = voteDelta.getValue();
            double botDValue = botDelta.getValue();
            double hpDValue = hpDelta.getValue();
            //System.out.println("Deltas:\\nVote: " + voteDValue + "\\nBot: " + botDValue + "\\nHP: " + hpDValue);
            double total = voteDValue + botDValue + hpDValue;
            if (total > 0) {
                voteDValue *= 1. / total;
                botDValue *= 1. / total;
            } else {
                voteDValue = 0;
                botDValue = 1;
            }
            //System.out.println("Normalized Deltas:\\n" + "Vote: " + voteDValue + "\\nBot: " + botDValue);

            int voteAllocation;
            int botAllocation;
            int hpAllocation;
            try {
                voteAllocation = (int) Math.round(voteDValue * income);
                botAllocation = (int) Math.round(botDValue * income);
                hpAllocation = income - voteAllocation - botAllocation;
                assert voteAllocation >= 0 && botAllocation >= 0 && hpAllocation >= 0;
                assert income >= voteAllocation + botAllocation + hpAllocation;
            } catch (AssertionError e) {
                voteAllocation = (int) Math.floor(voteDValue * income);
                botAllocation = (int) Math.floor(botDValue * income);
                hpAllocation = income - voteAllocation - botAllocation;
            }

            //if there are nearby enemy politicians, make sure we have enough hp
            /**double safetyEval = ec.getSafetyEval();
            if (hpBudget <= safetyEval && hpBudget != 0) {
                if (currentInfluence > safetyEval + 1) {
                    hpBudget = (int) safetyEval + 1;
                    int remainingInfluence = currentInfluence - hpBudget;
                    double newTotal = voteDValue + botDValue;
                    voteAllocation = (int) (voteDValue / newTotal * remainingInfluence);
                    botAllocation = remainingInfluence - (voteBudget + voteAllocation);
                } else {
                    voteAllocation = 0;
                    botAllocation = 0;
                    hpBudget += income - 1;
                    botBudget += 1;
                }
            }**/

            if (rc.getTeamVotes() >= Utilities.VOTE_WIN) {
                botBudget += voteAllocation + botAllocation + voteBudget;
                voteBudget = 0;
            } else {
                voteBudget += voteAllocation;
                botBudget += botAllocation;
            }

            if(voteBudget > 2 * ec.getPrevVoteAmount() * ec.getPrevVoteMult() + 1) {
                int diff = voteBudget - 2 * ec.getPrevVoteAmount() * ec.getPrevVoteMult() - 1;
                voteBudget = 2 * ec.getPrevVoteAmount() * ec.getPrevVoteMult() + 1;
                botBudget += diff;
            }

            hpBudget += hpAllocation;

            //System.out.println("Total influence: " + currentInfluence + "\\nVoting Budget: "
                    //+ voteBudget + "\\nBot Budget: " + botBudget + "\\nSaving: " + hpBudget);
        }
    }

    /**
     * @return budget for making bids
     */
    public int getVoteBudget() {
        return voteBudget;
    }

    /**
     * @return budget for making bots
     */
    public int getBotBudget() {
        return botBudget;
    }

    /**
     * @return how much influence to save
     */
    public int getHpBudget() {
        return hpBudget;
    }

    public void withdrawBudget(ECSpawnController sc, int amount) { botBudget -= amount; }

    public void withdrawBudget(ECVoteController vc, int amount) { voteBudget -= amount; }

    public boolean canSpend (ECSpawnController sc, int amount) { return botBudget >= amount; }

    public boolean canSpend (ECVoteController vc, int amount) { return voteBudget >= amount; }
}
"""
base_values = [0.001, 0.01, 0.1, 1, 5, 10, 25, 50]
initial = [50, 0.1, 0.5, 2, 0.001, 0.1, 0.1, 0.001, 1]
prev_parameters_blue = pickle.load(open('prev_blue.pkl', 'rb'))
prev_parameters_red = pickle.load(open('prev_red.pkl', 'rb'))
win_avg_state = pickle.load(open('state_values.pkl', 'rb'))
experience_buffer = []  # pickle.load(open('experiences.pkl', 'rb'))


# determines reward based on input team and stdout
def reward(state_input, output_string):
    splits = [ss.split(')') for ss in output_string.split('(')]
    winner = splits[1][0]
    round_num = [s.split(' ') for s in splits[2]][0][1]
    out = 1500 + 0.5 * (1500 - int(round_num)) if winner == state_input.team else -3000 + int(round_num)
    return out, winner


# generates random input parameters in a 1d list of length 9
def generate_input():
    base = []
    for i in range(9):
        base.append(rand.choice(base_values) * rand.random())
    return base


def modify_parameters(state_input):
    parameters = [str(val) for val in state_input.values]
    file = blue_file if state_input.team == 'B' else red_file
    out = file % (parameters[0], parameters[1], parameters[2], parameters[3], parameters[4],
                  parameters[5], parameters[6], parameters[7], parameters[8])
    return out


# takes in two stateInputs to set red team and blue team parameters
def get_game_output(blue, red):
    new_blue = modify_parameters(blue)
    new_red = modify_parameters(red)

    os.chdir(home)
    blue_file = open('../../src/baseplayer2/eccontrollers/ECBudgetController.java', 'w')
    red_file = open('../../src/baseplayer3/eccontrollers/ECBudgetController.java', 'w')
    blue_file.write(new_blue)
    red_file.write(new_red)
    blue_file.close()
    red_file.close()

    os.chdir('../../')
    output = subprocess.run('gradle run', capture_output=True, shell=True, encoding='UTF-8')
    out = output.stdout.split('[server]')[-3]
    return out.strip()


# learns Q(s, a)
def run_matches(iters):
    global win_avg_state
    global prev_winner
    global experience_buffer
    num_updates = 0

    while num_updates < iters:
        # generate new inputs and log
        print("iteration: ", num_updates + 1)
        inp = generate_input()
        challenger = stateInput(inp, 'B')
        challenger.print()
        king = stateInput(win_avg_state, 'A')
        king.print()

        # run game and calculate reward
        out_string = get_game_output(challenger, king)
        challenger_reward, winner = reward(challenger, out_string)
        king_reward, _ = reward(king, out_string)
        print(f"Challenger got {challenger_reward} reward for {'winning' if winner == 'B' else 'losing'}")
        print(f"King got {king_reward} reward for {'winning' if winner == 'A' else 'losing'}")
        experience_buffer.append((challenger.tensor, challenger_reward))
        experience_buffer.append((king.tensor, king_reward))
        # average
        if winner == 'B':
            for i in range(9):
                win_avg_state[i] += inp[i]
                win_avg_state[i] *= 0.5
        prev_winner = winner

        num_updates += 1

    os.chdir(home)
    pickle.dump(win_avg_state, open('state_values.pkl', "wb"))
    pickle.dump(prev_parameters_blue, open('prev_blue.pkl', "wb"))
    pickle.dump(prev_parameters_red, open('prev_red.pkl', 'wb'))
    pickle.dump(experience_buffer, open('experiences.pkl', 'wb'))


if __name__ == '__main__':
    try:
        run_matches(100)
    except Exception as exception:
        print(exception)
        os.chdir(home)
        # emergency dump
        pickle.dump(prev_parameters_blue, open('prev_blue.pkl', "wb"))
        pickle.dump(prev_parameters_red, open('prev_red.pkl', 'wb'))
        pickle.dump(win_avg_state, open('state_values.pkl', "wb"))
        pickle.dump(experience_buffer, open('experiences.pkl', 'wb'))
