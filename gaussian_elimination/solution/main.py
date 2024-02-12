from collections import deque
from graphviz import Digraph
from threading import Thread
import pandas as pd
from random import random
from numpy import array

def init(n):
    A = []
    T = []

    for i in range(1, n):
        for k in range(i+1, n+1):
            A.append(f"A_{i},{k}")
            T.append((f"m_{k},{i}", (f"M_{k},{i}", f"M_{i},{i}")))
            for j in range(i, n+2):
                A.append(f"B_{i},{j},{k}")
                T.append((f"n_{k},{i},{j}", (f"M_{i},{j}", f"m_{k},{i}")))

                A.append(f"C_{i},{j},{k}")
                T.append((f"M_{k},{j}", (f"M_{k},{j}", f"n_{k},{i},{j}")))

    w = A.copy()

    return A, T, w
def dependency_relationship(A, T, n):
    D = set()
    for i in range(n):
        x = T[i][0]
        for j in range(n):
            if i == j:
                D.add((A[i], A[i]))
            elif x == T[j][0] or x in T[j][1]:
                D.add((A[i], A[j]))
                D.add((A[j], A[i]))
    I = {(x, y) for x in A for y in A}
    I = sorted(I - D)
    D = sorted(D)

    return D, I

def dependency_graph(D, w, k):
    G = [[] for _ in range(k)]
    for i in range(k):
        for j in range(i + 1, k):
            if (w[i], w[j]) in D:
                G[i].append(j)

    return G

def exists_another_path_dfs(G, u, v):
    n = len(G)
    Visited = [False for _ in range(n)]
    Visited[u] = True
    for x in G[u]:
        if x != v:
            dfs_visit(G, Visited, x)

    return Visited[v]

def dfs_visit(G, Visited, u):
    Visited[u] = True
    for v in G[u]:
        if not Visited[v]:
            dfs_visit(G, Visited, v)

def reduce_dependency_graph(G, k):
    for u in range(k):
        for v in G[u][::]:
            if exists_another_path_dfs(G, u, v):
                G[u].remove(v)

def foata_classes_bfs(G):
    n = len(G)
    D = [0 for _ in range(n)]
    Q = deque()
    for v in range(n):
        for u in range(v):
            if v in G[u]:
                break
        else:
            Q.appendleft(v)

    while Q:
        u = Q.pop()
        for v in G[u]:
            D[v] = D[u] + 1
            Q.appendleft(v)

    return D

def foata_normal_form(G, w, k):
    C = foata_classes_bfs(G)
    Classes = [[] for _ in range(max(C) + 1)]

    for i in range(k):
        Classes[C[i]].append(w[i])
    fnf = ""

    for c in Classes:
        fnf += '( '

        for f in c:
            fnf += f + " "
        fnf += ')'

    return fnf, Classes

def draw_graph(G, k, w, name):
    dot = Digraph(name)
    for i in range(k):
        dot.node(str(i), w[i])
    for i in range(k):
        for j in G[i]:
            dot.edge(str(i), str(j))
    dot.save()

def execute_operation(operation, M, m, n):
    indexes = operation[2:].split(',')
    match operation[0]:
        case 'A':
            i = int(indexes[0]) - 1
            k = int(indexes[1]) - 1
            m[k][i] = M[k][i] / M[i][i]
        case 'B':
            i = int(indexes[0]) - 1
            j = int(indexes[1]) - 1
            k = int(indexes[2]) - 1
            n[k][i][j] = M[i][j] * m[k][i]
        case 'C':
            i = int(indexes[0]) - 1
            j = int(indexes[1]) - 1
            k = int(indexes[2]) - 1
            M[k][j] = M[k][j] - n[k][i][j]

def gaussian_elimination_concurrently(M, C):
    size = len(M)
    m = [[0 for i in range(size - 1)] for k in range(size)]
    n = [[[0 for j in range(size + 1)] for i in range(size - 1)] for k in range(size)]

    for c in C:
        Threads = []
        for operation in c:
            Threads.append(Thread(target=execute_operation, args=(operation, M, m, n)))
        for thread in Threads:
            thread.start()
        for thread in Threads:
            thread.join()

def gaussian_elimination_simple(M):
    rows, cols = M.shape

    for i in range(min(rows, cols - 1)):
        for j in range(i + 1, rows):
            factor = M[j, i] / M[i, i]
            M[j, i:] -= factor * M[i, i:]

def test(size, M, name):
    A, T, w = init(size)

    print("INPUT")
    print("Macierz wejściowa:")
    pd.options.display.float_format = '{:.2f}'.format
    print(pd.DataFrame(M))
    print()

    n = len(A)
    k = len(w)

    D, I = dependency_relationship(A, T, n)

    G = dependency_graph(D, w, k)
    reduce_dependency_graph(G, k)

    fnf, C = foata_normal_form(G, w, k)

    M_simple = array(M)
    gaussian_elimination_concurrently(M, C)
    gaussian_elimination_simple(M_simple)

    print("OUTPUT")
    print("A = " + str(A))
    print("T = " + str(T))
    print("w = " + str(w) + "\n")
    print("D = " + str(D))
    # print("I = " + str(I))
    print("FNF([w]) = " + fnf)
    print()
    draw_graph(G, k, w, name)
    print("Macierz po współbieżnej eliminacji Gaussa:")
    print(pd.DataFrame(M))
    print()
    print("Macierz po zwykłej eliminacji Gaussa:")
    print(pd.DataFrame(M_simple))
    print()

    if M == M_simple.tolist():
        print("Uzyskane rozwiązanie jest poprawne")
    else:
        print("Uzyskane rozwiązanie jest niepoprawne")
    print()



# Główny program
print("Test dla macierzy z pliku na uplu\n")
M = [[2.0, 1.0, 3.0, 6.0], [4.0, 3.0, 8.0, 15.0], [6.0, 5.0, 16.0, 27.0]]
test(3, M, "test1")

print("Test dla losowej macierzy\n")
size = int(input("Podaj rozmiar macierzy: "))
M = [[100 * random() for j in range(size + 1)] for i in range(size)]
test(size, M, "test2")