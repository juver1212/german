package com.itp.trackinn.ServiceGeolocation;

import com.google.gson.JsonElement;
import com.itp.trackinn.data.entity.CoordenadaLocation;

import java.math.BigDecimal;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Service {


    @POST("principal/Pruebaolva")
    Call<Object> postLocation(@Body CoordenadaLocation coordenadaLocation);
/*
    @POST(Constants.PATH_GESTION_ENTREGA_MOTIVAR_CON_FOTO_BY_REMITO)
    Call<ResponseGeneric> insertarFotoOlvaMotivacionByRemito(@Body GestiónBodyRequest gestiónBodyRequest);

    @POST(Constants.PATH_GESTION_ENTREGA_CONFIRMAR_CON_FOTO_BY_REMITO)
    Call<ResponseGeneric> insertarFotoOlvaConfirmacionByRemito(@Body GestiónBodyRequest gestiónBodyRequest);

    @PUT(Constants.PATH_GESTION_ENTREGA_MOTIVAR_MOBILE_BY_REMITO)
    Call<GestionCambioEstadoResponse> gestionMotivarMobileByRemito(@Body GestiónBodyRequest gestiónBodyRequest);

    @PUT(Constants.PATH_GESTION_ENTREGA_CONFIRMAR_BY_REMITO)
    Call<GestionCambioEstadoResponse> gestionConfirmarByRemito(@Body GestiónBodyRequest gestiónBodyRequest);

    @PUT(Constants.PATH_GESTION_ENTREGA_MOTIVAR_MESA_DE_PARTES_BY_REMITO)
    Call<GestionCambioEstadoResponse> gestionMotivarMesaDePartesByRemito(
            @Path("usuario") String usuario, @Path("idUsuario") BigDecimal idUsuario,
            @Body List<GestiónBodyRequest> gestiónBodyRequest);

    //JUNTAR WS
    @PUT(Constants.PATH_CONFIRMAR_SINGLE)
    Call<GestionCambioEstadoResponse> confirmarSingle(@Body GestiónBodyRequest asignacionBodyRequest);

    @PUT(Constants.PATH_MOTIVAR_SINGLE)
    Call<GestionCambioEstadoResponse> motivarSingle(@Body GestiónBodyRequest asignacionBodyRequest);
 */
}
