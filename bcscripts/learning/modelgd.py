from pidlearning import *
import numpy as np

experience_buffer = pickle.load(open('experiences_chevron.pkl', 'rb')) + pickle.load(open('experiences_andromeda.pkl', 'rb'))\
                    + pickle.load(open('experiences_gridlock.pkl', 'rb'))
print(len(experience_buffer))
experience, reward = experience_buffer[0]
experiences = experience
rewards = t.tensor([[reward]])

q_arch = OrderedDict([('lin1', nn.Linear(9, 25)),
                      ('sig1', nn.ReLU()),
                      ('lin2', nn.Linear(25, 1)),
                      ('batch', nn.BatchNorm1d(1))])
q_func = nn.Sequential(q_arch)  # t.load('q_func.pkl')
q_func.eval()

for experience, reward in experience_buffer[1:]:
    experiences = t.cat((experiences, experience), dim=0)
    rewards = t.cat((rewards, t.tensor([[reward]])), dim=0)


# train
def batch_gd(network, loss_fn, optimizer, x, y, iters, lrate=1.5e-4, k=25):
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

        all_linear1_params = t.cat([x.view(-1) for x in network.lin1.parameters()])
        all_linear2_params = t.cat([x.view(-1) for x in network.lin2.parameters()])
        loss = loss_fn(out, yt) + .1 * t.norm(all_linear1_params, 1) + .1 * t.norm(all_linear2_params, 2)
        # loss = losses.sum()

        opt.zero_grad()
        loss.backward()
        opt.step()

        num_updates += 1


def gd_optimize(network, initial, iters, lrate):
    network.eval()

    x = initial
    steps = 0
    while steps < iters:
        out = -network(x)
        if steps % 100 == 0:
            print(out)
        out.backward()

        x = x - lrate * x.grad
        x.retain_grad()

        steps += 1
    if input('continue?') == 'Y':
        gd_optimize(network, x, iters, lrate)
    else:
        return x


def train_and_save():
    batch_gd(q_func, nn.MSELoss(), optim.SGD, experiences, rewards, 5000)
    ind = 0
    for experience, _ in experience_buffer:
        q_func.eval()
        print(ind, q_func(experience))
        ind += 1
    q_func.train()
    if input('Save model?') == 'Y':
       t.save(q_func, 'q_func.pkl')


def optimize_and_save():
    optimal = gd_optimize(q_func, t.tensor([generate_input()], requires_grad=True), 5000, 1.5e-4)
    list_optimal = optimal.detach().tolist()[0]
    for i in range(len(list_optimal)):
        list_optimal[i] = list_optimal[i] if list_optimal[i] > 0 else 0
    print(list_optimal)
    inp = stateInput(list_optimal, 'A')
    print(q_func(inp.tensor))
    if input('save this?') == 'Y':
        t.save(optimal, 'optimal_x.pkl')


if __name__ == '__main__':
    train_and_save()
    optimize_and_save()
