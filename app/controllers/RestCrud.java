package controllers;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import play.Play;
import play.data.binding.Binder;
import play.db.Model.Factory;
import play.db.jpa.Model;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;

public class RestCrud extends Controller {
	
	private static Moshi moshi;
	
	public static void index() {
		renderText("yooo");
	}

	public static void list(String model) {
		List<play.db.Model> entityList = getEntityList(model);
		renderJSON(entityList);
	}
	
	public static void create(String model, String body) throws IOException{
		Model fromJson = getModelFromJson(model, body);
		validation.valid(fromJson);
		if (validation.hasErrors()) {
			response.status = Http.StatusCode.BAD_REQUEST;
            renderJSON(validation.errorsMap());
        }
		fromJson._save();
		renderJSON(fromJson);
	}

	public static void get(String model, String id) throws Exception {
        play.db.Model byId = findModelById(model, id);
        notFoundIfNull(byId);
        renderJSON(byId);
	}

	public static void update(String model, String id, String body) throws Exception{
		play.db.Model byId = findModelById(model, id);
		notFoundIfNull(byId);
		
		Model fromJson = getModelFromJson(model, body);
		
		for(Field f : byId.getClass().getDeclaredFields()){
			Object value = f.get(fromJson);
			if(value == null){
				continue;
			}
			f.set(byId, value);
		}
		
		validation.valid(byId);
		if (validation.hasErrors()) {
			response.status = Http.StatusCode.BAD_REQUEST;
            renderJSON(validation.errorsMap());
        }
		byId._save();
		renderJSON(byId);
	}

	public static void delete(String model, String id) throws Exception{
		play.db.Model byId = findModelById(model, id);
		notFoundIfNull(byId);
		byId._delete();
		ok();
	}
	
	
	
	
	
	
	private static play.db.Model findModelById(String model, String id) throws Exception{
		Class<? extends Model> modelClass = getModelClass(model);
		Factory factory =  Model.Manager.factoryFor(modelClass);
        Object boundId = Binder.directBind(id, factory.keyType());
        return factory.findById(boundId);
	}
	
	private static Model getModelFromJson(String model, String body) throws IOException {
		JsonAdapter<? extends Model> adapter = getMoshiAdapter(model);
		return adapter.fromJson(body);
	}
	
	private static JsonAdapter<? extends Model> getMoshiAdapter(String model){
		if(moshi == null){
			moshi = new Moshi.Builder().build();
		}
		Class<? extends Model> modelClass = getModelClass(model);
		return moshi.adapter(modelClass);
	}
	
	private static List<play.db.Model> getEntityList(String model){
		Class<? extends Model> modelClass = getModelClass(model);
		Factory factory = Model.Manager.factoryFor(modelClass);
		Long count = factory.count(null, null, null);
		return factory.fetch(0, count.intValue(), null, null, null, null, null);
	}
	
	private static Class<? extends Model> getModelClass(String model) {
		try {
			String fullModelName = "models." + model;
			Class<? extends Model> modelClass = (Class<? extends Model>) Play.classloader
					.loadClass(fullModelName);

			return modelClass;

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

}
