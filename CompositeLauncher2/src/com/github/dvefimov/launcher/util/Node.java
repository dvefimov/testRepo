package com.github.dvefimov.launcher.util;

import java.util.LinkedList;
import java.util.List;

public class Node {
	private String name;
	private List<Node> subCategories;
	private Node parent;

	public Node(String name, Node parent) {
		this.name = name;
		this.parent = parent;
		if (parent != null)
			parent.addSubCategory(this);
	}

	public List<Node> getSubCategories() {
		return subCategories;
	}

	private void addSubCategory(Node subcategory) {
		if (subCategories == null)
			subCategories = new LinkedList<Node>();
		if (!subCategories.contains(subcategory))
			subCategories.add(subcategory);
	}

	public String getName() {
		return name;
	}

	public Node getParent() {
		return parent;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Node) {
			String oName = ((Node) o).getName();
			return name.equals(oName);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
