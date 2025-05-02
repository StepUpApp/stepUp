package DataAccess.repository;

import okhttp3.*;
import com.google.gson.*;
import java.io.IOException;
import java.util.ArrayList;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import Business.modelo.Ejercicio;
import Business.interfaces.EjercicioInterface;

public class APIRESTEjercicioRepository implements EjercicioInterface<String> {

    private final String API_URL = "https://parseapi.back4app.com/classes/Ejercicio";
    private final String APPLICATION_ID = "Ww26DWeZkJxPzEn6GHA2kQMY2Vyi4PXK6rZgpJj2";
    private final String REST_API_KEY = "RQcngHQ6GtBRHXF2RoHrQYwZDjqhhBTDwiOU5MPb";
    private final Gson gson = new Gson();
    OkHttpClient client = new OkHttpClient();

    @Override
    public ArrayList<Ejercicio> listarEjercicios() {
        List<Ejercicio> ejercicios = new ArrayList<>();
        try {
            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("X-Parse-Application-Id", APPLICATION_ID)
                    .addHeader("X-Parse-REST-API-Key", REST_API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            
            if (response.code() == 200) {
                String responseJson = response.body().string();
                JsonObject jsonObject = gson.fromJson(responseJson, JsonObject.class);
                JsonArray results = jsonObject.getAsJsonArray("results");
                
                Type listType = new TypeToken<ArrayList<Ejercicio>>(){}.getType();
                ejercicios = gson.fromJson(results, listType);
            }
        } catch (IOException e) {
            System.err.println("Error obteniendo ejercicios: " + e.getMessage());
        }
        return (ArrayList<Ejercicio>) ejercicios;
    }

    @Override
    public String crearEjercicio(Ejercicio ejercicio) {
        String objectId = "";
        try {
            String jsonEjercicio = gson.toJson(ejercicio);
            
            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("X-Parse-Application-Id", APPLICATION_ID)
                    .addHeader("X-Parse-REST-API-Key", REST_API_KEY)
                    .post(RequestBody.create(MediaType.get("application/json"), jsonEjercicio))
                    .build();

            Response response = client.newCall(request).execute();
            
            if (response.isSuccessful()) {
                JsonObject jsonResponse = gson.fromJson(response.body().string(), JsonObject.class);
                objectId = jsonResponse.get("objectId").getAsString();
            }
        } catch (IOException e) {
            System.err.println("Error insertando ejercicio: " + e.getMessage());
        }
        return objectId;
    }

    @Override
    public Ejercicio obtenerEjercicioPorId(String objectId) {
        Ejercicio ejercicio = null;
        try {
            Request request = new Request.Builder()
                    .url(API_URL + "/" + objectId)
                    .addHeader("X-Parse-Application-Id", APPLICATION_ID)
                    .addHeader("X-Parse-REST-API-Key", REST_API_KEY)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            
            if (response.isSuccessful()) {
                String responseJson = response.body().string();
                ejercicio = gson.fromJson(responseJson, Ejercicio.class);
            }
        } catch (IOException e) {
            System.err.println("Error obteniendo ejercicio: " + e.getMessage());
        }
        return ejercicio;
    }

    @Override
    public boolean editarEjercicio(Ejercicio ejercicio) {
        try {
            String jsonEjercicio = gson.toJson(ejercicio);
            
            Request request = new Request.Builder()
                    .url(API_URL + "/" + ejercicio.getObjectId())
                    .addHeader("X-Parse-Application-Id", APPLICATION_ID)
                    .addHeader("X-Parse-REST-API-Key", REST_API_KEY)
                    .put(RequestBody.create(MediaType.get("application/json"), jsonEjercicio))
                    .build();

            Response response = client.newCall(request).execute();
            
            return response.isSuccessful();
            
        } catch (IOException e) {
            System.err.println("Error actualizando ejercicio: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean borrarEjercicio(String objectId) {
        
        try {
            Request request = new Request.Builder()
                    .url(API_URL + "/" + objectId)
                    .addHeader("X-Parse-Application-Id", APPLICATION_ID)
                    .addHeader("X-Parse-REST-API-Key", REST_API_KEY)
                    .delete()
                    .build();

            Response response = client.newCall(request).execute();

            return response.isSuccessful();
            
        } catch (IOException e) {
            System.err.println("Error eliminando ejercicio: " + e.getMessage());
            return false;
        } 

    }
    public void cerrarConexion(){
        // Cierra conexiones de OkHttp para liberar recursos
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
    }
    public List<Ejercicio> calcularEjercicios(long tiempoSentado, double peso, double altura, int edad){return null;}

}