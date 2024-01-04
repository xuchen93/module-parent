package com.github.xuchen93.model.leetcode;

import lombok.Data;

@Data
public class ListNode {
	public int val;
    public ListNode next;
    public ListNode() {}
    public ListNode(int val) { this.val = val; }
    public ListNode(int val, ListNode next) { this.val = val; this.next = next; }

	public void soutFormatStr() {
		ListNode node = this;
		while (node != null){
			System.out.print(node.val);
			if (node.next != null){
				System.out.print(" ——> ");
			}
			node = node.next;
		}
		System.out.println();
	}
}
