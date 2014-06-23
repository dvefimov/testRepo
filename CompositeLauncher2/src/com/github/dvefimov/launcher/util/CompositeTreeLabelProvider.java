package com.github.dvefimov.launcher.util;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import java.util.Map;

public class CompositeTreeLabelProvider implements ILabelProvider {
	Map<String, Image> images;

	public CompositeTreeLabelProvider(Map<String, Image> images) {
		this.images = images;
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

	@Override
	public Image getImage(Object element) {
		String key = ((Node) element).getName();
		Image im = images.get(key);
		return im;
	}

	@Override
	public String getText(Object element) {
		return ((Node) element).getName();
	}

}
