package DataAccess.repository;

import okhttp3.*;
import com.google.gson.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import Business.modelo.Ubicacion;
import Business.interfaces.UbicacionInterface;

public class APIRESTUbicacionRepository implements UbicacionInterface<String> {

    private final String API_URL = "https://parseapi.back4app.com/classes/Ubicacion";
    private final String APPLICATION_ID = "Ww26DWeZkJxPzEn6GHA2kQMY2Vyi4PXK6rZgpJj2";
    private final String REST_API_KEY = "RQcngHQ6GtBRHXF2RoHrQYwZDjqhhBTDwiOU5MPb";
    private final Gson gson = new Gson();
    OkHttpClient client = new OkHttpClient();


    @Override
    public List<Ubicacion> listarUbicaciones(String usuarioId) {
        final String API_URL = "https://parseapi.back4app.com/classes/Ubicacion";
        final String APPLICATION_ID = "Ww26DWeZkJxPzEn6GHA2kQMY2Vyi4PXK6rZgpJj2";
        final String REST_API_KEY = "RQcngHQ6GtBRHXF2RoHrQYwZDjqhhBTDwiOU5MPb";
        final Gson gson = new Gson();
        OkHttpClient client = new OkHttpClient();
        List<Ubicacion> ubicaciones = new ArrayList<>();
        try {
            JsonObject whereObject = new JsonObject();
            JsonArray orArray = new JsonArray();

            // Primera condición: usuarioId coincide
            JsonObject condition1 = new JsonObject();
            condition1.addProperty("usuarioId", usuarioId);
            orArray.add(condition1);

            // Segunda condición: no existe usuarioId
            JsonObject condition2 = new JsonObject();
            JsonObject existsCondition = new JsonObject();
            existsCondition.addProperty("$exists", false);
            condition2.add("usuarioId", existsCondition);
            orArray.add(condition2);

            // Poner el $or
            whereObject.add("$or", orArray);

            // Codificar la consulta en la URL
            String whereParam = URLEncoder.encode(whereObject.toString(), StandardCharsets.UTF_8.name());
            String url = API_URL + "?where=" + whereParam;

            Request request = new Request.Builder()
                    .url(url)
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

                Type listType = new TypeToken<ArrayList<Ubicacion>>(){}.getType();
                ubicaciones = gson.fromJson(results, listType);
            }
        } catch (IOException e) {
            System.err.println("Error obteniendo ubicaciones: " + e.getMessage());
        }
        return ubicaciones;
    }

    @Override
    public String crearUbicacion(Ubicacion ubicacion) {
        String objectId = "";
        try {
            String jsonUbicacion = gson.toJson(ubicacion);
            
            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("X-Parse-Application-Id", APPLICATION_ID)
                    .addHeader("X-Parse-REST-API-Key", REST_API_KEY)
                    .post(RequestBody.create(MediaType.parse("application/json"), jsonUbicacion))
                    .build();

            Response response = client.newCall(request).execute();
            
            if (response.isSuccessful()) {
                JsonObject jsonResponse = gson.fromJson(response.body().string(), JsonObject.class);
                objectId = jsonResponse.get("objectId").getAsString();
            }
        } catch (IOException e) {
            System.err.println("Error insertando ubicación: " + e.getMessage());
        }
        return objectId;
    }

    @Override
    public Ubicacion obtenerUbicacionPorID(String objectId) {
        Ubicacion ubicacion = null;
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
                ubicacion = gson.fromJson(responseJson, Ubicacion.class);
            }
        } catch (IOException e) {
            System.err.println("Error obteniendo ubicación: " + e.getMessage());
        }
        return ubicacion;
    }

    @Override
    public Ubicacion findByCoordinates(double latitude, double longitude) {
        return null;
    }

    @Override
    public void setImagen(int id, String url) {

    }

    @Override
    public Boolean editarUbicacion(Ubicacion ubicacion) {
        try {
            String jsonUbicacion = gson.toJson(ubicacion);

            Request request = new Request.Builder()
                    .url(API_URL + "/" + ubicacion.getObjectId())
                    .addHeader("X-Parse-Application-Id", APPLICATION_ID)
                    .addHeader("X-Parse-REST-API-Key", REST_API_KEY)
                    .put(RequestBody.create(MediaType.parse("application/json"), jsonUbicacion))
                    .build();

            Response response = client.newCall(request).execute();
            
            return response.isSuccessful();
            
        } catch (IOException e) {
            System.err.println("Error actualizando ubicación: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean borrarUbicacion(String objectId) {
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
            System.err.println("Error eliminando ubicación: " + e.getMessage());
            return false;
        }
    }
    

    public void cerrarConexion(){
        // Cierra conexiones de OkHttp para liberar recursos
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
    }
}