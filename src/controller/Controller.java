package controller;

import application.Main;

public abstract class Controller {

	private Main main;

	public Main getMain() {
		return main;
	}

	public void setMain(Main main) {
		this.main = main;
	}
	
}
