package com.github.xuchen93.model.structure;

/**
 * 并查集
 */
public class UnionSet {

	/**
	 * 根节点
	 */
	private int[] f;
	/**
	 * 深度
	 */
	private int[] rank;

	public UnionSet(int count) {
		f = new int[count];
		rank = new int[count];
		for (int i = 1; i <= count; i++) {
			f[i] = i;
			rank[i] = 1;
		}
	}

	public int find(int x) {
		if (f[x] == x) {
			return x;
		} else {
			f[x] = find(f[x]);//父节点设为根节点
			return f[x];
		}
	}

	public void merge(int i, int j) {
		int p1 = find(i);
		int p2 = find(j);
		if (p1 == p2) {
			return;
		}
		int newP;//新的根节点
		if (rank[p1] <= rank[p2]) {//p1的深度更低
			f[p1] = p2;
			newP = p2;
		} else {
			f[p2] = p1;
			newP = p1;
		}
		if (rank[p1] != rank[p2]) {
			rank[newP]++;
		}
	}

	public void unionSet(int x, int y) {
		int fx = find(x), fy = find(y);
		if (fx == fy) {
			return;
		}
		if (rank[fx] < rank[fy]) {
			int temp = fx;
			fx = fy;
			fy = temp;
		}
		rank[fx] += rank[fy];
		f[fy] = fx;
	}
}
