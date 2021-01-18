from random import random

def moving(size, array):
    out = []
    internal = []
    for val in array:
        ind = array.index(val)
        internal_size = len(internal)
        sum = 0
        if internal_size < size:
            internal.append(val)
            internal_size += 1
        else:
            internal[ind % size] = val
        for i in range(internal_size):
            sum += internal[i]
        sum *= 1/internal_size
        out.append(sum)
    return out


def welford(size, array):
    out = [array[0]]
    for val in array[1:]:
        k = len(out)
        out.append(out[k-1] + (val-out[k-1])/size)
    return out


if __name__ == '__main__':
    rand = [random() for i in range(100)]
    print(moving(10, rand))
    print(welford(10, rand))