package semaforos;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *
 * @author jsortega
 */
public class Semaforos {

    public static void main(String[] args) {

        String URL = "http://10.32.1.103:8080";

        /**
         * *********************************************************
         **              Solicita autorizacion                    **
         ***********************************************************
         */
        
        String cookie = "";   //Aqui guardamos la cookie de autorizacion para usar en todas las peticiones
        /*Documentacion:
        https://documenter.getpostman.com/view/2315327/6fU2mEz?version=latest#fc71ee19-f77f-39da-bf48-13883dce60fa
         */
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n\t\"username\":\"alien\",\n\t\"password\":\"2ad09f231a5695d0a75fa3a0dde5ccba78cb4c422f6d0e386a7ae852792eb5fc\"\n}");
        Request request = new Request.Builder()
                .url(URL + "/ALE/api/auth")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
            System.out.println("Cookie: " + response.header("Set-Cookie"));
            cookie = response.header("Set-Cookie");  //guardamos la cookie que viene en el header de la respuesta 
            
            /**
             **********************************************************
             ** Interroga por las acciones que                       **
             ** se pueden hacer con los semaforos                    **
             **********************************************************
             */
            
            Request requestAcciones = new Request.Builder()
                    .url(URL + "/ALE/api/device/Anden1Baliza/action") //Aqui esta el recusrso para invocar los semaforos 

                    /* Documentacion
                       https://documenter.getpostman.com/view/2315327/6fU2mEz?version=latest#20dbc7eb-dd81-5c3e-b637-0ce46dc40d63
                     */
                    .method("GET", null)
                    .addHeader("Cookie", cookie)
                    .build();
            try {
                Response responseAcciones = client.newCall(requestAcciones).execute();

                System.out.println("Respuesta Lista de acciones: " + responseAcciones.body().string());
            } catch (IOException ex) {
                Logger.getLogger(Semaforos.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            /**
             * *********************************************************
             ** Cambia estatus Semaforos **
             **********************************************************
             */
            //Ya con la cookie generamos la peticion, en este caso vamos a invocar el cambio del semaforo del anden 1 a rojo
            RequestBody bodyInvoke = RequestBody.create(mediaType, "{\n\t\"deviceName\":\"Anden1Baliza\",\n\t\"deviceAction\":\"Apagado\"\n}"); //Generamos el JSON con el cuerpo de lo que queremos hacer
            Request requestInvoke = new Request.Builder()
                    .url(URL + "/ALE/api/device/invoke") //Aqui esta el recusrso para invocar los semaforos 

                    /* Documentacion
                       https://documenter.getpostman.com/view/2315327/6fU2mEz?version=latest#1962bba9-8e9b-aad7-c883-62c99ca49cc7
                     */
                    .method("POST", bodyInvoke)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Cookie", cookie)
                    .build();
            try {
                Response responseInvoke = client.newCall(requestInvoke).execute();

                System.out.println("Respuesta Invoke Semaforo: " + responseInvoke.code());
            } catch (IOException ex) {
                Logger.getLogger(Semaforos.class.getName()).log(Level.SEVERE, null, ex);
            }



        } catch (IOException ex) {
            Logger.getLogger(Semaforos.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
