package com.tatanstudios.astropollocliente.network;


import com.tatanstudios.astropollocliente.modelos.carrito.ModeloCarrito;
import com.tatanstudios.astropollocliente.modelos.carrito.ModeloCarritoList;
import com.tatanstudios.astropollocliente.modelos.carrito.ModeloCarritoProductoEditar;
import com.tatanstudios.astropollocliente.modelos.carrito.ModeloCarritoTemporal;
import com.tatanstudios.astropollocliente.modelos.categorias.ModeloCategoriasArray;
import com.tatanstudios.astropollocliente.modelos.direccion.ModeloDireccion;
import com.tatanstudios.astropollocliente.modelos.mapa.ModeloPoligonoList;
import com.tatanstudios.astropollocliente.modelos.modotesteo.ModeloProductosTesteo;
import com.tatanstudios.astropollocliente.modelos.ordenes.ModeloOrdenesActivas;
import com.tatanstudios.astropollocliente.modelos.ordenes.ModeloProductosOrdenes;
import com.tatanstudios.astropollocliente.modelos.perfil.ModeloHorarios;
import com.tatanstudios.astropollocliente.modelos.premios.ModeloPremios;
import com.tatanstudios.astropollocliente.modelos.principal.ModeloCategorias;
import com.tatanstudios.astropollocliente.modelos.procesar.ModeloProcesar;
import com.tatanstudios.astropollocliente.modelos.productoindividual.ModeloInformacionProducto;
import com.tatanstudios.astropollocliente.modelos.productos.ModeloProductosArray;
import com.tatanstudios.astropollocliente.modelos.productos.ModeloSubCategorias;
import com.tatanstudios.astropollocliente.modelos.servicios.ModeloTipoServiciosZona;
import com.tatanstudios.astropollocliente.modelos.usuario.AccessTokenUser;
import com.tatanstudios.astropollocliente.modelos.usuario.ApiRespuesta;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;


public interface ApiService {

   /* @GET("v2/list")
    Observable<ModeloPrueba> string_call
            (@Query("page") int page,
            @Query("limit") int limit);*/

   /* @GET("cliente/listado")
    Observable<ModeloPrueba> llamada(
            @Query("page") int page);*/

    // registro de usuario nuevo con area,
   /* @POST("cliente/registro") //*
    @FormUrlEncoded
    Observable<AccessTokenUser> registroUsuario(@Field("usuario") String usuario,
                                                @Field("password") String password,
                                                @Field("correo") String correo,
                                                @Field("token_fcm") String token_fcm);


*/

    @POST("cliente/login")
    @FormUrlEncoded
    Observable<AccessTokenUser> inicioSesion(@Field("usuario") String usuario,
                                             @Field("password") String password,
                                             @Field("idfirebase") String idfirebase);



    // registro de nuevo cliente
    @POST("cliente/registro")
    @FormUrlEncoded
    Observable<AccessTokenUser> registroUsuario(@Field("usuario") String usuario,
                                                @Field("password") String password,
                                                @Field("correo") String correo,
                                                @Field("token_fcm") String token_fcm,
                                                @Field("version") String version);


    // solicitud de menu principal para el cliente
    @POST("cliente/lista/servicios-bloque")
    @FormUrlEncoded
    Observable<ModeloTipoServiciosZona> listaDeTipoServicio(@Field("id") String id);


    // listado de direcciones del cliente
    @FormUrlEncoded
    @POST("cliente/listado/direcciones")
    Observable<ModeloDireccion> listadoDeDirecciones(@Field("id") String id);


    // retorna lista de poligonos para mostrar en el mapa
    @GET("cliente/listado/zonas/poligonos")
    Observable<ModeloPoligonoList> listadoMapaZonaPoligonos();


    // registrar direccion de cliente
    @FormUrlEncoded
    @POST("cliente/nueva/direccion")
    Observable<ApiRespuesta> registrarDireccion(
            @Field("id") String id,
            @Field("nombre") String nombre,
            @Field("direccion") String direccion,
            @Field("punto_referencia") String punto_referencia,
            @Field("id_zona") String zonaid,
            @Field("latitud") String latitud,
            @Field("longitud") String longitud,
            @Field("latitudreal") String latitudreal,
            @Field("longitudreal") String longitudreal,
            @Field("telefono") String telefono);


    // retorna listado de categorias
    @FormUrlEncoded
    @POST("cliente/listado/todas/categorias")
    Observable<ModeloCategoriasArray> listaTodasLasCategorias(@Field("id") String id);


    // retorna listado de productos segun categoria y muestra con sus sub categorias
    @FormUrlEncoded
    @POST("cliente/listado/productos/servicios")
    Observable<ModeloSubCategorias> listadoDeProductosServicio(@Field("id") Integer id); // id categoria


    // retorna informacion de un producto individual
    @FormUrlEncoded
    @POST("cliente/informacion/producto/individual")
    Observable<ModeloInformacionProducto> infoProductoIndividual(
            @Field("id") Integer id);



    @FormUrlEncoded
    @POST("cliente/carrito/producto/agregar")
    Observable<ModeloCarritoTemporal> agregarCarritoTemporal(
            @Field("clienteid") String userid,
            @Field("productoid") int productoid,
            @Field("cantidad") int cantidad,
            @Field("notaproducto") String notaproducto);



    // ver carrito de compras del cliente
    @FormUrlEncoded
    @POST("cliente/carrito/ver/orden")
    Observable<ModeloCarrito> verCarritoCompras(
            @Field("clienteid") String clienteid);


    // eliminar todos el carrito de compras
    @FormUrlEncoded
    @POST("cliente/carrito/borrar/orden")
    Observable<ApiRespuesta> borrarCarritoCompras(
            @Field("clienteid") String clienteid);


    // eliminar una fila de producto del carrito de compras
    @FormUrlEncoded
    @POST("cliente/carrito/eliminar/producto")
    Observable<ApiRespuesta> borrarProductoCarrito(@Field("clienteid") String clienteid,
                                                   @Field("carritoid") int carritoid);


    // cambiar cantidad de producto EDITANDO
    @FormUrlEncoded
    @POST("cliente/carrito/ver/producto")
    Observable<ModeloCarritoProductoEditar> infoProductoIndividualCarrito(
            @Field("clienteid") String userid, @Field("carritoid") int carritoid);


    // cambiar la cantidad de un producto del carrito de compras
    @FormUrlEncoded
    @POST("cliente/carrito/cambiar/cantidad")
    Observable<ApiRespuesta> cambiarCantidadProducto(
            @Field("clienteid") String clienteid, @Field("cantidad") int cantidad,
            @Field("carritoid") int carritoid, @Field("nota") String nota);


    // informacion final para enviar la orden a cocina
    @FormUrlEncoded
    @POST("cliente/carrito/ver/proceso-orden")
    Observable<ModeloProcesar> verProcesarOrden(
            @Field("clienteid") String clienteid);

    // verificaciones de cupones
    @FormUrlEncoded
    @POST("cliente/verificar/cupon")
    Observable<AccessTokenUser> verificarCupon(
            @Field("clienteid") String userid, @Field("cupon") String cupon);


    // ENVIO DE LA ORDEN
    @FormUrlEncoded
    @POST("cliente/proceso/enviar/orden")
    Observable<AccessTokenUser> enviarOrden(
            @Field("clienteid") String clienteid,
            @Field("nota") String nota,
            @Field("cupon") String cupon,
            @Field("aplicacupon") int aplicacupon,
            @Field("version") String dispositivo,
            @Field("idfirebase") String idfirebase);


    // ENVIAR NOTIFICACION A RESTAURANTE DESPUES DE ORDENAR
    @FormUrlEncoded
    @POST("cliente/proceso/orden/notificacion")
    Observable<AccessTokenUser> enviarNotiRestauranteOrdenar(
            @Field("id") int id);





    // solo obtener informacion del cliente
    @FormUrlEncoded
    @POST("cliente/informacion/personal")
    Observable<AccessTokenUser> informacionCliente(
            @Field("id") String id);



    // ver horarios del restaurante
    @FormUrlEncoded
    @POST("cliente/informacion/restaurante/horario")
    Observable<ModeloHorarios> informacionHorario(
            @Field("id") String id);



    // actualizar contrasena dentro del perfil del cliente
    @POST("cliente/perfil/actualizar/contrasena")
    @FormUrlEncoded
    Observable<AccessTokenUser> nuevaPasswordPerfil(@Field("id") String id,
                                                 @Field("password") String password);


    // informacion del cliente si quiere borrar ordenes de carrito al hacer una orden
    @FormUrlEncoded
    @POST("cliente/opcion/perfil/carrito")
    Observable<AccessTokenUser> verOpcionCarrito(
            @Field("id") String id);


    @FormUrlEncoded
    @POST("cliente/opcion/perfil/carrito/guardar")
    Observable<AccessTokenUser> guardarOpcionCarrito(
            @Field("id") String id,
            @Field("disponible") int toggle);


    // elegir direccion seleccionada
    @FormUrlEncoded
    @POST("cliente/direcciones/elegir/direccion")
    Observable<AccessTokenUser> elegirDireccion(
            @Field("id") String id,
            @Field("dirid") int dirid);


    // eliminar direccion seleccionada
    @FormUrlEncoded
    @POST("cliente/eliminar/direccion/seleccionada")
    Observable<AccessTokenUser> eliminarDireccion(
            @Field("id") String id,
            @Field("dirid") int dirid);


    // ver ordenes activas del cliente
    @FormUrlEncoded
    @POST("cliente/ordenes/listado/activas")
    Observable<ModeloOrdenesActivas> verOrdenesActivas(
            @Field("clienteid") String clienteid);


    // ESTADOS DE LA ORDEN
    @FormUrlEncoded
    @POST("cliente/orden/informacion/estado")
    Observable<ModeloOrdenesActivas> verEstadosDeOrden(
            @Field("ordenid") int ordenid);



    // ver motorista de la orden

    @FormUrlEncoded
    @POST("cliente/orden/ver/motorista")
    Observable<AccessTokenUser> verMotorista(
            @Field("ordenid") int ordenid);



    // calificar orden

    @FormUrlEncoded
    @POST("cliente/orden/completar/calificacion")
    Observable<AccessTokenUser> calificarOrden(
            @Field("ordenid") int ordenid,
            @Field("valor") int estrellas,
            @Field("mensaje") String mensaje);


    // OCULTAR MI ORDEN PORQUE FUE CANCELADA
    @FormUrlEncoded
    @POST("cliente/ocultar/mi/orden")
    Observable<AccessTokenUser> ocultarMiOrden(
            @Field("ordenid") int ordenid);




    // ENVIAR CORREO PARA RECUPERAR CONTRASENA
    @FormUrlEncoded
    @POST("cliente/enviar/codigo-correo")
    Observable<AccessTokenUser> enviarCorreoRecuperarContrasena(
            @Field("correo") String correo);

    @FormUrlEncoded
    @POST("cliente/verificar/codigo-correo-password")
    Observable<AccessTokenUser> verificarCodigoResetPassword(
            @Field("codigo") String codigo,
            @Field("correo") String correo);


    // CAMBIO DE CONTRASENA CUANDO LA HA PERDIDO
    @FormUrlEncoded
    @POST("cliente/actualizar/password")
    Observable<AccessTokenUser> cambiarPasswordPerdida(
            @Field("id") String id,
            @Field("password") String password);


    // ver lista de los productos que se orden
    @FormUrlEncoded
    @POST("cliente/listado/productos/ordenes")
    Observable<ModeloProductosOrdenes> verProductosOrdenes(
            @Field("ordenid") int ordenid);



    @FormUrlEncoded
    @POST("cliente/listado/productos/ordenes-individual")
    Observable<ModeloProductosOrdenes> verProductosOrdenesIndividual(
            @Field("idordendescrip") int idordendescrip);



    // CANCELAR ORDEN ENVIADA
    @FormUrlEncoded
    @POST("cliente/proceso/cancelar/orden")
    Observable<AccessTokenUser> cancelarOrden(
            @Field("idorden") int idorden);


    @FormUrlEncoded
    @POST("cliente/historial/listado/ordenes")
    Observable<ModeloOrdenesActivas> verHistorial(
            @Field("id") String id,
            @Field("fecha1") String fecha1,
            @Field("fecha2") String fecha2);



    // ** PREMIOS
    @FormUrlEncoded
    @POST("cliente/premios/listado")
    Observable<ModeloPremios> listaDePremiosDisponibles(
            @Field("clienteid") String clienteid);


    @FormUrlEncoded
    @POST("cliente/premios/seleccionar")
    Observable<ModeloPremios> seleccionarPremio(
            @Field("clienteid") String clienteid,
            @Field("idpremio") int idpremio);


    @FormUrlEncoded
    @POST("cliente/premios/deseleccionar")
    Observable<ModeloPremios> deseleccionarPremio(
            @Field("clienteid") String clienteid);


    // enviar un problema del cliente

    @FormUrlEncoded
    @POST("cliente/problema/aplicacion/nota")
    Observable<ModeloPremios> enviarProblema(
            @Field("clienteid") String clienteid,
            @Field("manufactura") String manufactura,
            @Field("nombre") String nombre,
            @Field("modelo") String modelo,
            @Field("codenombre") String codenombre,
            @Field("devicenombre") String devicenombre,
            @Field("problema") String problema);







    @FormUrlEncoded
    @POST("cliente/listado/productos/testeo")
    Observable<ModeloProductosTesteo> listadoProductosModoTesteo(
            @Field("idcliente") String userid);



    // informacion de un producto de modo testeo
    @FormUrlEncoded
    @POST("cliente/info/producto/individual/modotesteo")
    Observable<ModeloInformacionProducto> infoProductoIndividualModoTesteo(
            @Field("idpro") Integer idpro);


    // agregar carrito para modo testeo

    @FormUrlEncoded
    @POST("cliente/carrito/producto/agregar/modotesteo")
    Observable<ModeloCarritoTemporal> agregarCarritoTemporalModoTesteo(
            @Field("clienteid") String userid,
            @Field("productoid") int productoid,
            @Field("cantidad") int cantidad,
            @Field("notaproducto") String notaproducto);


    // lista de productos para carrito de compras
    @FormUrlEncoded
    @POST("cliente/carrito/ver/orden/modotesteo")
    Observable<ModeloCarrito> verCarritoComprasModoTesteo(
            @Field("clienteid") String clienteid);



    // eliminar un producto del modo testeo
    @FormUrlEncoded
    @POST("cliente/carrito/eliminar/producto/modotesteo")
    Observable<ApiRespuesta> borrarProductoCarritoModoTesteo(@Field("clienteid") String clienteid,
                                                   @Field("carritoid") int carritoid);


    // informacion final al cliente

    @FormUrlEncoded
    @POST("cliente/carrito/ver/procesoorden/modotesteo")
    Observable<ModeloProcesar> verProcesarOrdenModoTesteo(
            @Field("clienteid") String clienteid);


    // finalizar el modo testeo
    @FormUrlEncoded
    @POST("cliente/finalizar/modotesteo")
    Observable<ApiRespuesta> finalizarModoTesteo(
            @Field("clienteid") String clienteid);




}
