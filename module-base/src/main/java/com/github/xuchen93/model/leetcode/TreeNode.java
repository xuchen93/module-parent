package com.github.xuchen93.model.leetcode;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class TreeNode {
	public Integer val;
	public TreeNode left;
	public TreeNode right;

	private static final String EMPTY_STR = "              ";

	public TreeNode(int val) {
		this.val = val;
	}

	public TreeNode(Integer[] arr) {
		this(arr,true);
	}

	public TreeNode(Integer[] arr,boolean ignoreNullNode) {
		this.val = arr[0];
		LinkedList<TreeNode> parentList = new LinkedList<>();
		LinkedList<TreeNode> tempList = new LinkedList<>();
		parentList.add(this);
		Integer temp;
		for (int i = 1; i < arr.length; ) {
			for (TreeNode node : parentList) {
				if (node == null) {
					if (!ignoreNullNode){
						i += 2;
					}
					continue;
				}
				if (i >= arr.length) {
					return;
				}
				temp = arr[i++];
				if (temp != null) {
					node.left = new TreeNode(temp);
				}
				if (i == arr.length) {
					return;
				}
				temp = arr[i++];
				if (temp != null) {
					node.right = new TreeNode(temp);
				}
				tempList.add(node.left);
				tempList.add(node.right);
			}
			parentList = tempList;
			tempList = new LinkedList<>();
		}
	}

	public void soutFormatStr() {
		//获取每行需要输出的数
		HashMap<Integer, List<Integer>> map = new LinkedHashMap<>();
		LinkedList<TreeNode> parentList = new LinkedList<>();
		parentList.add(this);
		LinkedList<TreeNode> tempList = new LinkedList<>();
		int row = 0;
		int maxNumLen = 0;
		while (parentList.stream().anyMatch(Objects::nonNull)) {
			for (TreeNode treeNode : parentList) {
				List<Integer> list = map.computeIfAbsent(row, i -> new ArrayList<>());
				if (treeNode == null) {
					list.add(null);
					tempList.add(null);
					tempList.add(null);
				} else {
					list.add(treeNode.val);
					maxNumLen = Math.max(String.valueOf(treeNode.val).length(), maxNumLen);
					tempList.add(treeNode.left);
					tempList.add(treeNode.right);
				}
			}
			parentList = tempList;
			tempList = new LinkedList<>();
			row++;
		}
		for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
			soutRow(map.size(), entry.getKey(), entry.getValue(), maxNumLen);
		}
	}

	private void soutRow(int size, int index, List<Integer> list, int maxNumLen) {
		String t = EMPTY_STR.substring(0, maxNumLen);
		int realIndex = size - index - 1;
		int firstLen = (int) Math.pow(2, realIndex) - 1;//(每个数的前后距离-1)/2
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < firstLen; i++) {
			s.append(t);
		}
		String result = list.stream().map(i -> {
			String numStr = i == null ? "N" : String.valueOf(i);
			return s + (numStr + EMPTY_STR).substring(0, maxNumLen) + s;
		}).collect(Collectors.joining(t));
		System.out.println(result);

	}
}
