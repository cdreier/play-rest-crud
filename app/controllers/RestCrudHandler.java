package controllers;

import java.io.IOException;

import play.Play;
import play.db.jpa.Model;
import play.mvc.Before;
import play.mvc.Controller;

public class RestCrudHandler extends Controller {

	private static final String CONTROLLER_PACKAGE = "controllers.api";

	private Class<? extends RestCrud> findUserImpl(String modelName) {

		String controllerName = String.format("%s.%sApi", CONTROLLER_PACKAGE, modelName);
		try {
			Class<? extends RestCrud> controllerClass = (Class<? extends RestCrud>) Play.classloader
					.loadClass(controllerName);
			return controllerClass;
		} catch (ClassNotFoundException e) {
			notFound("Expected a controller with name: " + controllerName);
		}

		return null;
	}

	public void index() {
		new RestCrud().index();
	}

	public void list(String model) throws Exception {
		Class<? extends RestCrud> userImpl = findUserImpl(model);
		userImpl.newInstance().list(model);
	}

	public void create(String model, String body) throws Exception {
		Class<? extends RestCrud> userImpl = findUserImpl(model);
		userImpl.newInstance().create(model, body);
	}

	public void get(String model, String id) throws Exception {
		Class<? extends RestCrud> userImpl = findUserImpl(model);
		userImpl.newInstance().get(model, id);
	}

	public void update(String model, String id, String body) throws Exception {
		Class<? extends RestCrud> userImpl = findUserImpl(model);
		userImpl.newInstance().update(model, id, body);
	}

	public void delete(String model, String id) throws Exception {
		Class<? extends RestCrud> userImpl = findUserImpl(model);
		userImpl.newInstance().delete(model, id);
	}

}
