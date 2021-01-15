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

public class ECBudgetController implements ECController {
    private final RobotController rc;
    private final BotEnlightenment ec;
    private final PIDDelta voteDelta = new PIDDelta(%s, %s, %s);
    private final PIDDelta botDelta = new PIDDelta(%s, %s, %s);
    private final PIDDelta hpDelta = new PIDDelta(%s, %s, %s);
    private int voteBudget;
    private int botBudget;
    private int hpBudget;


    public ECBudgetController(RobotController rc, BotEnlightenment ec) {
        this.rc = rc;
        this.ec = ec;
    }

    @Override
    public void run() throws GameActionException {
        int currentInfluence = rc.getInfluence();
        int currentRound = rc.getRoundNum();
        int income = currentInfluence - voteBudget - botBudget - hpBudget;

        //TODO: update PID targets
        /** add alpha, beta scaling factor to adjust early voting and bot budgets
         double alpha = Math.pow((double) rc.getRoundNum() / MAX_ROUNDS, 0.2);
         double beta = 1 + (1 - alpha);
         voteDelta *= alpha;
         botDelta *= beta; **/
        double voteTarget = 0.5 * currentRound < 250 ? (double) currentRound/250 : 1;
        double botTarget = currentRound * 0.25;
        double hpTarget = 1 + currentRound - ec.getLastEnemySeen() < 500 ? (int) (rc.getRoundNum() * 0.25) : (int) (rc.getRoundNum() * 0.05);

        // set new targets
        voteDelta.setTarget(voteTarget);
        botDelta.setTarget(botTarget);
        hpDelta.setTarget(hpTarget);

        // update deltas
        voteDelta.update((double) rc.getTeamVotes()/currentRound);
        botDelta.update(ec.getLocalRobotCount());
        hpDelta.update(hpBudget);

        // normalize to positive percentages
        double voteDValue = voteDelta.getValue();
        double botDValue = botDelta.getValue();
        double hpDValue = hpDelta.getValue();
        System.out.println("Deltas:\\nVote: " + voteDValue + "\\nBot: " + botDValue + "\\nHP: " + hpDValue);
        if (voteDValue < 0 || botDValue < 0 || hpDValue < 0 ) {
            double min = Math.min(voteDValue, Math.min(botDValue, hpDValue));
            voteDValue -= min;
            botDValue -= min;
            hpDValue -= min;
        }
        double total = voteDValue + botDValue + hpDValue;
        voteDValue *= total > 0 ? 1. / total : 1;
        botDValue *= total > 0 ? 1. / total : 0;
        System.out.println("Normalized Deltas:\\n" + "Vote: " + voteDValue + "\\nBot: " + botDValue);

        int voteAllocation;
        int botAllocation;
        int hpAllocation;
        try {
            voteAllocation = (int) Math.round(voteDValue * income);
            botAllocation = (int) Math.round(botDValue * income);
            hpAllocation = income - voteAllocation - botAllocation;
            System.out.println("initial allocations: " + voteAllocation + ' ' + botAllocation + ' ' + hpAllocation);
            //assert income <= voteAllocation + botAllocation + hpAllocation;
            assert voteAllocation >= 0 && botAllocation >= 0 && hpAllocation >= 0;
        } catch (AssertionError e){
            System.out.println("assertion error");
            voteAllocation = (int) Math.floor(voteDValue * income);
            botAllocation = (int) Math.floor(botDValue * income);
            hpAllocation = income - voteAllocation - botAllocation;
        }

        /** if there are nearby enemy politicians, make sure we have enough hp
        int enemyInfluence = ec.checkNearbyEnemies();
        if (hpBudget <= enemyInfluence && hpBudget != 0) {
            if (currentInfluence > enemyInfluence + 1) {
                hpBudget = enemyInfluence + 1;
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
        }

        if (rc.getTeamVotes() > 1500) {
            botBudget += voteAllocation + botAllocation + voteBudget;
            voteBudget = 0;
        } else if (currentRound - ec.getLastBotSpawn() > 150) {
            voteBudget = voteAllocation + botAllocation + botBudget;
            botBudget = 0;
        } else {
            voteBudget += voteAllocation;
            botBudget += botAllocation;
        }**/

        voteBudget += voteAllocation;
        botBudget += botAllocation;
        hpBudget += hpAllocation;

        System.out.println("Total influence: " + currentInfluence + "\\nVoting Budget: "
                + voteBudget + "\\nBot Budget: " + botBudget + "\\nSaving: " + hpBudget);
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

public class ECBudgetController implements ECController {
    private final RobotController rc;
    private final BotEnlightenment ec;
    private final PIDDelta voteDelta = new PIDDelta(%s, %s, %s);
    private final PIDDelta botDelta = new PIDDelta(%s, %s, %s);
    private final PIDDelta hpDelta = new PIDDelta(%s, %s, %s);
    private int voteBudget;
    private int botBudget;
    private int hpBudget;


    public ECBudgetController(RobotController rc, BotEnlightenment ec) {
        this.rc = rc;
        this.ec = ec;
    }

    @Override
    public void run() throws GameActionException {
        int currentInfluence = rc.getInfluence();
        int currentRound = rc.getRoundNum();
        int income = currentInfluence - voteBudget - botBudget - hpBudget;

        //TODO: update PID targets
        /** add alpha, beta scaling factor to adjust early voting and bot budgets
         double alpha = Math.pow((double) rc.getRoundNum() / MAX_ROUNDS, 0.2);
         double beta = 1 + (1 - alpha);
         voteDelta *= alpha;
         botDelta *= beta; **/
        double voteTarget = 0.5 * currentRound < 250 ? (double) currentRound/250 : 1;
        double botTarget = currentRound * 0.25;
        double hpTarget = 1 + currentRound - ec.getLastEnemySeen() < 500 ? (int) (rc.getRoundNum() * 0.25) : (int) (rc.getRoundNum() * 0.05);

        // set new targets
        voteDelta.setTarget(voteTarget);
        botDelta.setTarget(botTarget);
        hpDelta.setTarget(hpTarget);

        // update deltas
        voteDelta.update((double) rc.getTeamVotes()/currentRound);
        botDelta.update(ec.getLocalRobotCount());
        hpDelta.update(hpBudget);

        // normalize to positive percentages
        double voteDValue = voteDelta.getValue();
        double botDValue = botDelta.getValue();
        double hpDValue = hpDelta.getValue();
        //System.out.println("Deltas:\\nVote: " + voteDValue + "\\nBot: " + botDValue + "\\nHP: " + hpDValue);
        if (voteDValue < 0 || botDValue < 0 || hpDValue < 0 ) {
            double min = Math.min(voteDValue, Math.min(botDValue, hpDValue));
            voteDValue -= min;
            botDValue -= min;
            hpDValue -= min;
        }
        double total = voteDValue + botDValue + hpDValue;
        voteDValue *= total > 0 ? 1. / total : 1;
        botDValue *= total > 0 ? 1. / total : 0;
        //System.out.println("Normalized Deltas:\\n" + "Vote: " + voteDValue + "\\nBot: " + botDValue);

        int voteAllocation;
        int botAllocation;
        int hpAllocation;
        try {
            voteAllocation = (int) Math.round(voteDValue * income);
            botAllocation = (int) Math.round(botDValue * income);
            hpAllocation = income - voteAllocation - botAllocation;
            //System.out.println("initial allocations: " + voteAllocation + ' ' + botAllocation + ' ' + hpAllocation);
            //assert income <= voteAllocation + botAllocation + hpAllocation;
            assert voteAllocation >= 0 && botAllocation >= 0 && hpAllocation >= 0;
        } catch (AssertionError e){
            //System.out.println("assertion error");
            voteAllocation = (int) Math.floor(voteDValue * income);
            botAllocation = (int) Math.floor(botDValue * income);
            hpAllocation = income - voteAllocation - botAllocation;
        }

        /** if there are nearby enemy politicians, make sure we have enough hp
        int enemyInfluence = ec.checkNearbyEnemies();
        if (hpBudget <= enemyInfluence && hpBudget != 0) {
            if (currentInfluence > enemyInfluence + 1) {
                hpBudget = enemyInfluence + 1;
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
        }

        if (rc.getTeamVotes() > 1500) {
            botBudget += voteAllocation + botAllocation + voteBudget;
            voteBudget = 0;
        } else if (currentRound - ec.getLastBotSpawn() > 150) {
            voteBudget = voteAllocation + botAllocation + botBudget;
            botBudget = 0;
        } else {
            voteBudget += voteAllocation;
            botBudget += botAllocation;
        }**/

        voteBudget += voteAllocation;
        botBudget += botAllocation;
        hpBudget += hpAllocation;

        //System.out.println("Total influence: " + currentInfluence + "\\nVoting Budget: "
                //+ voteBudget + "\\nBot Budget: " + botBudget + "\\nSaving: " + hpBudget);
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
base_values = [0.001, 0.01, 0.1, 1, 5, 10, 25, 50, 100]
initial = [50, 0.1, 0.5, 2, 0.001, 0.1, 0.1, 0.001, 1]
prev_parameters_blue = pickle.load(open('prev_blue.pkl', 'rb'))
prev_parameters_red = pickle.load(open('prev_red.pkl', 'rb'))
win_avg_state = pickle.load(open('state_values.pkl', 'rb'))
experience_buffer = pickle.load(open('experiences.pkl', 'rb'))

model_arch = OrderedDict([('lin1', nn.Linear(9, 5)),
                          ('relu1', nn.ReLU()),
                          ('lin2', nn.Linear(5, 1))])

q_network = t.load('q_func.pkl')


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

    blue_file = open('..\\bc2021\\src\\baseplayer2\\eccontrollers\\ECBudgetController.java', 'w')
    red_file = open('..\\bc2021\\src\\baseplayer3\\eccontrollers\\ECBudgetController.java', 'w')
    blue_file.write(new_blue)
    red_file.write(new_red)
    blue_file.close()
    red_file.close()

    os.chdir(os.getcwd() + '\\..\\bc2021\\')
    output = subprocess.run('gradle run', capture_output=True, shell=True, encoding='UTF-8')
    out = output.stdout.split('[server]')[-3]
    return out.strip()


# learns Q(s, a)
def learn_q(network, loss_fn, optimizer, iters, lrate=1e-3):
    global win_avg_state
    global prev_winner
    opt = optimizer(network.parameters(), lr=lrate)
    num_updates = 0

    while num_updates < iters:
        # generate new inputs and log
        print("iteration: ", num_updates + 1)
        inp = generate_input()
        challenger = stateInput(inp, 'B')
        challenger.print()
        king = stateInput(win_avg_state, 'R')
        king.print()

        # run game and calculate reward
        out_string = get_game_output(challenger, king)
        reward_value, winner = reward(challenger, out_string)
        print(f"Challenger got {reward_value} reward for {'winning' if winner == 'B' else 'losing'}")
        experience_buffer.append((challenger.tensor, reward_value))
        # average
        if winner == 'B':
            for i in range(9):
                win_avg_state[i] += inp[i]
                win_avg_state[i] *= 0.5
        prev_winner = winner
        j = 0
        for experience in experience_buffer:
            print(f'Replaying experience: {j}')
            out = network(experience[0])
            loss = loss_fn(out.float(), t.tensor([[experience[1]]]).float())

            opt.zero_grad()
            loss.backward()
            opt.step()
            j += 1

        num_updates += 1

    os.chdir(home)
    t.save(network, 'q_func.pkl')
    pickle.dump(win_avg_state, open('state_values.pkl', "wb"))
    pickle.dump(prev_parameters_blue, open('prev_blue.pkl', "wb"))
    pickle.dump(prev_parameters_red, open('prev_red.pkl', 'wb'))
    pickle.dump(experience_buffer, open('experiences.pkl', 'wb'))


if __name__ == '__main__':
    try:
        learn_q(q_network, nn.MSELoss(), optim.Rprop, 100, lrate=1e-2)
    except Exception as exception:
        print(exception)
        os.chdir(home)
        # emergency dump
        t.save(q_network, 'q_func.pkl')
        pickle.dump(prev_parameters_blue, open('prev_blue.pkl', "wb"))
        pickle.dump(prev_parameters_red, open('prev_red.pkl', 'wb'))
        pickle.dump(win_avg_state, open('state_values.pkl', "wb"))
        pickle.dump(experience_buffer, open('experiences.pkl', 'wb'))
