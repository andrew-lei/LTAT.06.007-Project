package com.github.ltat_06_007_project;

import com.github.ltat_06_007_project.Views.ChatView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MainApplication extends Application {

	private ConfigurableApplicationContext springContext;
	private Scene scene;

	public static void main(final String[] args){
		launch(MainApplication.class, args);
	}

	@Override
	public void init() {
		springContext = SpringApplication.run(MainApplication.class);
		scene = new Scene(springContext.getBean(ChatView.class));
	}

	@Override
	public void stop() {
		springContext.stop();
	}

	@Override
	public void start(Stage stage) {
		stage.setTitle("EID IM");
		stage.setScene(scene);
		stage.show();
	}
}
