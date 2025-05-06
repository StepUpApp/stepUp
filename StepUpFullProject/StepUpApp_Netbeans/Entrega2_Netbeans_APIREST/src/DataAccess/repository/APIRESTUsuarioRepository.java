package DataAccess.repository;

import okhttp3.*;
import com.google.gson.*;
import java.io.IOException;
import java.util.ArrayList;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import java.net.URLEncoder;
import java.util.List;

import Business.modelo.Usuario;
import Business.interfaces.UsuarioInterface;

public class APIRESTUsuarioRepository implements UsuarioInterface<String> {

    private final String API_URL = "https://parseapi.back4app.com/classes/Usuario";
    private final String APPLICATION_ID = "Ww26DWeZkJxPzEn6GHA2kQMY2Vyi4PXK6rZgpJj2";
    private final String REST_API_KEY = "RQcngHQ6GtBRHXF2RoHrQYwZDjqhhBTDwiOU5MPb";
    private final Gson gson = new Gson();
    OkHttpClient client = new OkHttpClient();


    @Override
    public Usuario obtenerUsuarioPorUsername(String username){ return null;}

    @Override
    public List<Usuario> listarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
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
                
                Type listType = new TypeToken<ArrayList<Usuario>>(){}.getType();
                usuarios = gson.fromJson(results, listType);
            }
        } catch (IOException e) {
            System.err.println("Error obteniendo usuarios: " + e.getMessage());
        }
        return usuarios;
    }

    @Override
    public String crearUsuario(Usuario usuario) {
        String objectId = "";
        try {
            String jsonUsuario = gson.toJson(usuario);
            
            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("X-Parse-Application-Id", APPLICATION_ID)
                    .addHeader("X-Parse-REST-API-Key", REST_API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(MediaType.parse("application/json"),jsonUsuario))
                    .build();

            Response response = client.newCall(request).execute();
            
            if (response.isSuccessful()) {
                Usuario newUsuario = gson.fromJson(response.body().string(), Usuario.class);
                objectId = newUsuario.objectId;
            }
        } catch (IOException e) {
            System.err.println("Error insertando usuario: " + e.getMessage());
        }
        return objectId;
    }

    @Override
    public Usuario recuperarUsuarioPorId(String objectId) {
        Usuario usuario = null;
        try {
            Request request = new Request.Builder()
                    .url(API_URL + "/" + objectId)
                    .addHeader("X-Parse-Application-Id", APPLICATION_ID)
                    .addHeader("X-Parse-REST-API-Key", REST_API_KEY)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            
            if (response.isSuccessful()) {
                usuario = gson.fromJson(response.body().string(), Usuario.class);
            }
        } catch (IOException e) {
            System.err.println("Error obteniendo usuario: " + e.getMessage());
        }
        return usuario;
    }

    @Override
    public boolean editarUsuario(Usuario usuario) {
        try {
            String jsonUsuario = gson.toJson(usuario);
            
            Request request = new Request.Builder()
                    .url(API_URL + "/" + usuario.getObjectId())
                    .addHeader("X-Parse-Application-Id", APPLICATION_ID)
                    .addHeader("X-Parse-REST-API-Key", REST_API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .build();

            Response response = client.newCall(request).execute();
            
            return response.isSuccessful();
            
        } catch (IOException e) {
            System.err.println("Error actualizando usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean borrarUsuario(String objectId) {
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
            System.err.println("Error eliminando usuario: " + e.getMessage());
            return false;
        }
    }

    /*
    @Override

    public Usuario obtenerUsuarioPorId(int id) {
        try {
            // Codificar la query en formato URL
            String whereClause = URLEncoder.encode("{\"id\":" + id + "}", "UTF-8");
            String url = API_URL + "?where=" + whereClause;

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("X-Parse-Application-Id", APPLICATION_ID)
                    .addHeader("X-Parse-REST-API-Key", REST_API_KEY)
                    .get()
                    .build();
            
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String jsonResponse = response.body().string();
                JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
                JsonArray results = jsonObject.getAsJsonArray("results");

                if (results.size() > 0) {
                    return gson.fromJson(results.get(0), Usuario.class);
                }
            }
        } catch (IOException e) {
            System.err.println("Error obteniendo usuario por ID: " + e.getMessage());
        }
        return null;
    }

     */
    
    public Usuario getByUsername(String username) {
        try {
            String whereClause = URLEncoder.encode("{\"username\":\"" + username + "\"}", "UTF-8");
            String url = API_URL + "?where=" + whereClause;

            Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Parse-Application-Id", APPLICATION_ID)
                .addHeader("X-Parse-REST-API-Key", REST_API_KEY)
                .get()
                .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                JsonObject jsonResponse = gson.fromJson(response.body().string(), JsonObject.class);
                JsonArray results = jsonResponse.getAsJsonArray("results");
                if (results.size() > 0) {
                    return gson.fromJson(results.get(0), Usuario.class);
                }
            }
        } catch (IOException e) {
            System.err.println("Error obteniendo usuario: " + e.getMessage());
        }
        return null;
    }


    public void cerrarConexion(){
        // Cierra conexiones de OkHttp para liberar recursos
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
    }


}