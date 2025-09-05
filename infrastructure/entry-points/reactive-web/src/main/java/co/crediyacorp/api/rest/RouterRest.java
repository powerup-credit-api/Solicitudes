package co.crediyacorp.api.rest;

import co.crediyacorp.api.config.SolicitudPath;
import co.crediyacorp.api.dtos.SolicitudEntradaDto;
import co.crediyacorp.model.solicitud.SolicitudPendienteDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {
    private final SolicitudPath solicitudPath;


    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    produces = {
                            MediaType.APPLICATION_JSON_VALUE
                    },
                    method = RequestMethod.POST,
                    operation = @Operation(
                            operationId = "crearSolicitud",
                            summary = "Crear una nueva solicitud de prestamo",
                            description = """
            Se debe poder registrar una nueva solicitud de prestamo proporcionando los datos requeridos.
            Validaciones:
            - email, documentoIdentidad, monto, plazo y tipoPrestamo no pueden ser nulos o vacios.
            - email debe tener un formato valido.
            - monto debe ser un numero positivo mayor que 0.
            - plazo debe ser un entero positivo mayor que 0.
            - tipoPrestamo debe existir en el catalogo de tipos de prestamo (ejemplo: PRESTAMO HIPOTECARIO).
            """,
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                    required = true,
                                    description = "Datos de la nueva solicitud",
                                    content = @Content(
                                            schema = @Schema(implementation = SolicitudEntradaDto.class),
                                            examples = @ExampleObject(
                                                    name = "Ejemplo de solicitud",
                                                    value = """
                                            {
                                              "email": "cristian@example.com",
                                              "documentoIdentidad": "123456789",
                                              "monto": 1500.0,
                                              "plazo": 36,
                                              "tipoPrestamo": "PRESTAMO HIPOTECARIO"
                                            }
                                            """
                                            )
                                    )
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Solicitud creada exitosamente"),
                                    @ApiResponse(responseCode = "400", description = "Datos invalidos en la solicitud")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    produces = {
                            MediaType.APPLICATION_JSON_VALUE
                    },
                    method = RequestMethod.GET,
                    operation = @Operation(
                            operationId = "listarSolicitudes",
                            summary = "Obtener lista de solicitudes de prestamo",
                            description = """
        Devuelve una lista paginada de solicitudes de prestamo. 
        Se puede especificar la pagina, el tamaño y la direccion de ordenamiento.
        """,
                            parameters = {
                                    @Parameter(
                                            name = "page",
                                            in = ParameterIn.QUERY,
                                            description = "Numero de pagina (0 por defecto)",
                                            schema = @Schema(type = "integer", defaultValue = "0")
                                    ),
                                    @Parameter(
                                            name = "size",
                                            in = ParameterIn.QUERY,
                                            description = "Cantidad de registros por pagina (10 por defecto)",
                                            schema = @Schema(type = "integer", defaultValue = "10")
                                    ),
                                    @Parameter(
                                            name = "sortDirection",
                                            in = ParameterIn.QUERY,
                                            description = "Direccion de ordenamiento (ASC o DESC)",
                                            schema = @Schema(type = "string", allowableValues = {"ASC", "DESC"}, defaultValue = "ASC")
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Lista de solicitudes obtenida correctamente",
                                            content = @Content(
                                                    array = @ArraySchema(
                                                            schema = @Schema(implementation = SolicitudPendienteDto.class)
                                                    )
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Ocurrio un error inesperado"
                                    )
                            }
                    )
            )

    })


    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST(solicitudPath.getSolicitud()), handler::listenCrearSolicitud)
                .andRoute(GET(solicitudPath.getSolicitud()), handler::listenObtenerSolicitudesPendientes);
    }
}
