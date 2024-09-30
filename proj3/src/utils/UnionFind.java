package utils;

public class UnionFind {
    private int[] parent;
    private int count;

    public UnionFind(int n) {
        parent = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }
        count = n;
    }

    public int find(int p) {
        while (p != parent[p]) {
            parent[p] = parent[parent[p]];  // 路径压缩
            p = parent[p];
        }
        return p;
    }

    public boolean union(int p, int q) {
        int rootP = find(p);
        int rootQ = find(q);
        if (rootP == rootQ) return false;
        parent[rootP] = rootQ;
        count--;
        return true;
    }

    public int getCount() {
        return count;
    }
}
