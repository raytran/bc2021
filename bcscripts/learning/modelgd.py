from pidlearning import *
import numpy as np


experience, reward = experience_buffer[0]
experiences = experience
rewards = t.tensor([[reward]])

new_arch = OrderedDict([('lin1', nn.Linear(9, 25)),
                                 ('sig1', nn.ReLU()),
                                 ('lin2', nn.Linear(25, 1)),
                                 ('batch', nn.BatchNorm1d(1))])
new_q = t.load('new_qqq.pkl')  # nn.Sequential(new_arch)
new_q.eval()

for experience, reward in experience_buffer[1:]:
    experiences = t.cat((experiences, experience), dim=0)
    rewards = t.cat((rewards, t.tensor([[reward]])), dim=0)


# train
def batch_gd(network, loss_fn, optimizer, x, y, iters, lrate=1.5e-3, k=25):
    d, n = tuple(x.size())

    network.train()
    opt = optimizer(network.parameters(), lr=lrate)

    np.random.seed(0)
    num_updates = 0
    indices = np.arange(n)
    while num_updates < iters:

        np.random.shuffle(indices)
        x = x[indices, :]
        y = y[indices, :]

        xt = x[:k, :].float()
        yt = y[:k, :].float()

        out = network(xt)
        losses = loss_fn(out, yt)
        loss = losses.sum()

        opt.zero_grad()
        loss.backward()
        opt.step()

        num_updates += 1


def gd_optimize(network, initial, iters, lrate):
    network.eval()

    x = initial
    print(x)
    steps = 0
    while steps < iters:
        out = -network(x)
        print(out)
        out.backward()

        x = x - lrate * x.grad
        x.retain_grad()

        steps += 1
    return x


if __name__ == '__main__':
    #batch_gd(new_q, nn.MSELoss(), optim.SGD, experiences, rewards, 5000)
    #ind = 0
    #for experience, _ in experience_buffer:
    #    new_q.eval()
    #    print(ind, new_q(experience))
    #    ind += 1
    #new_q.train()
    #if input('Save model?') == 'Y':
    #   t.save(new_q, 'new_qqq.pkl')
    optimal = t.load('optimal_x.pkl')  # gd_optimize(new_q, t.tensor([generate_input()], requires_grad=True), 1000, 1.5e-3)
    list_optimal = optimal.detach().tolist()[0]
    for i in range(len(list_optimal)):
        list_optimal[i] = list_optimal[i] if list_optimal[i] > 0 else 0
    print(list_optimal)
    inp = stateInput(list_optimal, 'A')
    print(new_q(inp.tensor))
    #if input('save this?') == 'Y':
        #t.save(optimal, 'optimal_x.pkl')
