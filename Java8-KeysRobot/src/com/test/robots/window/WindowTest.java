package com.test.robots.window;

import java.awt.Window;

public class WindowTest {
	public static void main(String[] args) {
		Window window = getSelectedWindow(Window.getWindows());
		System.out.println("selectedWindow: " + window);
		Window activeWindow = javax.swing.FocusManager.getCurrentManager().getActiveWindow();
		System.out.println("activeWindow: " + activeWindow);
	}

	public static Window getSelectedWindow(Window[] windows) {
		Window result = null;
		for (int i = 0; i < windows.length; i++) {
			Window window = windows[i];
			if (window.isActive()) {
				result = window;
			} else {
				Window[] ownedWindows = window.getOwnedWindows();
				if (ownedWindows != null) {
					result = getSelectedWindow(ownedWindows);
				}
			}
		}
		return result;
	}
}
