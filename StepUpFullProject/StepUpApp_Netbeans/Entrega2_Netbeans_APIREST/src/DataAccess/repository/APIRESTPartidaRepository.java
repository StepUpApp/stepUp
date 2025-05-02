package DataAccess.repository;

import Business.modelo.Ubicacion;
import okhttp3.*;
import com.google.gson.*;
import java.io.IOException;
import java.util.ArrayList;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import Business.modelo.Partida;
import Business.interfaces.PartidaInterface;

public class APIRESTPartidaRepository implements PartidaInterface<String> {

    private final String API_URL = "https://parseapi.back4app.com/classes/Partida";
    private final String APPLICATION_ID = "Ww26DWeZkJxPzEn6GHA2kQMY2Vyi4PXK6rZgpJj2";
    private final String REST_API_KEY = "RQcngHQ6GtBRHXF2RoHrQYwZDjqhhBTDwiOU5MPb";
    private final Gson gson = new Gson();
    OkHttpClient client = new OkHttpClient();

    @Override
    public List<Partida> listarPartidas() {
        List<Partida> partidas = new ArrayList<>();
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
                
                Type listType = new TypeToken<ArrayList<Partida>>(){}.getType();
                partidas = gson.fromJson(results, listType);
            }
        } catch (IOException e) {
            System.err.println("Error obteniendo partidas: " + e.getMessage());
        }
        return partidas;
    }

    @Override
    public String guardarPartida(Partida partida) {
        String objectId = "";
        try {
            String jsonPartida = gson.toJson(partida);
            
            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("X-Parse-Application-Id", APPLICATION_ID)
                    .addHeader("X-Parse-REST-API-Key", REST_API_KEY)
                    .post(RequestBody.create(MediaType.get("application/json"), jsonPartida))
                    .build();

            Response response = client.newCall(request).execute();
            
            if (response.isSuccessful()) {
                Partida newPartida = gson.fromJson(response.body().string(), Partida.class);
                objectId = newPartida.objectId;
            }
        } catch (IOException e) {
            System.err.println("Error insertando partida: " + e.getMessage());
        }
        return objectId;
    }

    @Override
    public Partida obtenerPartidaPorId(String objectId) {
        Partida partida = null;
        try {
            Request request = new Request.Builder()
                    .url(API_URL + "/" + objectId)
                    .addHeader("X-Parse-Application-Id", APPLICATION_ID)
                    .addHeader("X-Parse-REST-API-Key", REST_API_KEY)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            
            if (response.isSuccessful()) {
                partida = gson.fromJson(response.body().string(), Partida.class);
            }
        } catch (IOException e) {
            System.err.println("Error obteniendo partida: " + e.getMessage());
        }
        return partida;
    }

    @Override
    public boolean editarPartida(Partida partida) {
        try {
            String jsonPartida = gson.toJson(partida);
            
            Request request = new Request.Builder()
                    .url(API_URL + "/" + partida.getObjectId())
                    .addHeader("X-Parse-Application-Id", APPLICATION_ID)
                    .addHeader("X-Parse-REST-API-Key", REST_API_KEY)
                    .put(RequestBody.create(MediaType.get("application/json"), jsonPartida))
                    .build();

            Response response = client.newCall(request).execute();
            
            return response.isSuccessful();
            
        } catch (IOException e) {
            System.err.println("Error actualizando partida: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean borrarPartida(String objectId) {
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
            System.err.println("Error eliminando partida: " + e.getMessage());
            return false;
        }
    }
    
    public void cerrarConexion(){
        // Cierra conexiones de OkHttp para liberar recursos
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
    }

    public Partida startPartida(String username, Ubicacion ubicacion){return null;}
    public void stopPartida(Partida partida){return;}
    public long getTiempoSentado(int id){return 0;}
    public List<Partida> getPartidasActivas(){return null;}
}