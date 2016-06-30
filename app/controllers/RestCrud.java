package controllers;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
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
import play.utils.Java;

public class RestCrud extends Controller {
	
	private static Moshi moshi;
	
	public void index() {
		renderText("RestCrud Play Module");
	}

	public void list(String model) {
		List<play.db.Model> entityList = getEntityList(model);
		renderJSON(entityList);
	}
	
	public void create(String model, String body) throws IOException{
		Model fromJson = getModelFromJson(model, body);
		validation.valid(fromJson);
		if (validation.hasErrors()) {
			response.status = Http.StatusCode.BAD_REQUEST;
            renderJSON(validation.errorsMap());
        }
		fromJson._save();
		renderJSON(fromJson);
	}

	public void get(String model, String id) throws Exception {
        play.db.Model byId = findModelById(model, id);
        notFoundIfNull(byId);
        renderJSON(byId);
	}

	public void update(String model, String id, String body) throws Exception{
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

	public void delete(String model, String id) throws Exception{
		play.db.Model byId = findModelById(model, id);
		notFoundIfNull(byId);
		byId._delete();
		ok();
	}
	
	
	
	
	
	
	protected play.db.Model findModelById(String model, String id) throws Exception{
		Class<? extends Model> modelClass = getModelClass(model);
		Factory factory =  Model.Manager.factoryFor(modelClass);
        Object boundId = Binder.directBind(id, factory.keyType());
        return factory.findById(boundId);
	}
	
	protected Model getModelFromJson(String model, String body) throws IOException {
		JsonAdapter<? extends Model> adapter = getMoshiAdapter(model);
		return adapter.fromJson(body);
	}
	
	protected JsonAdapter<? extends Model> getMoshiAdapter(String model){
		if(moshi == null){
			moshi = new Moshi.Builder().build();
		}
		Class<? extends Model> modelClass = getModelClass(model);
		return moshi.adapter(modelClass);
	}
	
	protected List<play.db.Model> getEntityList(String model){
		Class<? extends Model> modelClass = getModelClass(model);
		Factory factory = Model.Manager.factoryFor(modelClass);
		Long count = factory.count(null, null, null);
		return factory.fetch(0, count.intValue(), null, null, null, null, null);
	}
	
	protected Class<? extends Model> getModelClass(String model) {
		try {
			String fullModelName = "models." + model;
			Class<? extends Model> modelClass = (Class<? extends Model>) Play.classloader
					.loadClass(fullModelName);
			
			notFoundIfNull(modelClass);
			return modelClass;

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

}
