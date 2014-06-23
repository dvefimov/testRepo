package com.github.dvefimov.launcher.util;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class CompositeTreeContentProvider implements ITreeContentProvider {

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement != null && inputElement instanceof List<?>) {
			return ((List<?>) inputElement).toArray();
		}
		return new Object[0];
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		List<Node> subcats = ((Node) parentElement).getSubCategories();
		return subcats == null ? new Object[0] : subcats.toArray();
	}

	@Override
	public Object getParent(Object element) {
		return ((Node) element).getParent();
	}

	@Override
	public boolean hasChildren(Object element) {
		return ((Node) element).getSubCategories() != null;
	}

}
