from collections import deque
from graphviz import Digraph

def read_input():
    A = []
    T = []
    n = int(input("Podaj liczbę symboli w alfabecie: "))
    for i in range(n):
        x = input("Podaj symbol: ")
        A.append(x)
    print("Podaj transakcje na zmiennych:")
    for i in range(n):
        x = input("(" + A[i] + ") ")
        T.append(x)
    w = input("Podaj słowo: ")

    return A, T, w

def dependency_relationship(A, T, n):
    D = set()
    for i in range(n):
        x = T[i][0]
        for j in range(n):
            if i == j:
                D.add((A[i], A[i]))
            elif x in T[j]:
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
        fnf += '('
        c.sort()
        for f in c:
            fnf += f
        fnf += ')'

    return fnf

def draw_graph(G, k, name):
    dot = Digraph(name)
    for i in range(k):
        dot.node(str(i), w[i])
    for i in range(k):
        for j in G[i]:
            dot.edge(str(i), str(j))
    dot.save()

def test(A, T, w, name):
    n = len(A)
    k = len(w)

    D, I = dependency_relationship(A, T, n)

    G = dependency_graph(D, w, k)
    reduce_dependency_graph(G, k)

    fnf = foata_normal_form(G, w, k)

    print(name + "\n")
    print("Input")
    print("A = " + str(A))
    for i in range(n):
        print("(" + A[i] + ") " + T[i])
    print("w = " + w + "\n")

    print("Output")
    print("D = " + str(D))
    print("I = " + str(I))
    print("FNF([w]) = " + fnf)
    draw_graph(G, k, name)
    print("\n")


# Główny program

A = ['a', 'b', 'c', 'd']
T = ["x=x+y", "y=y+2z", "x=3x+z", "z=y-z"]
w = "baadcb"

test(A, T, w, "test1")

A = ['a', 'b', 'c', 'd', 'e', 'f']
T = ["x=x+1", "y=y+2z", "x=3x+z", "w=w+v", "z=y-z", "v=x+v"]
w = "acdcfbbe"

test(A, T, w, "test2")


A = ['a', 'b', 'c', 'd', 'e']
T = ["x=x+1", "y=y+x", "v=v+y", "z=z+v", "v=v+x"]
w = "acebdac"

test(A, T, w, "test3")

if input("Chcesz wykonać własny test? (tak/nie) ") == "tak":
    A, T, w = read_input()
    test(A, T, w, "test4")