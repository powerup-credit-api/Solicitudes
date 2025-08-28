package co.crediyacorp.api.rest;

import co.crediyacorp.api.config.SolicitudPath;
import co.crediyacorp.api.dtos.SolicitudEntradaDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {
    private final SolicitudPath solicitudPath;


    @RouterOperation(
            path = "/api/v1/solicitud",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            },
            method = RequestMethod.POST,
            operation = @Operation(
                    operationId = "crearSolicitud",
                    summary = "Crear una nueva solicitud de préstamo",
                    description = """
        Se debe poder registrar una nueva solicitud de préstamo proporcionando los datos requeridos.
        Validaciones:
        - email, documentoIdentidad, monto, plazo y tipoPrestamo no pueden ser nulos o vacíos.
        - email debe tener un formato válido.
        - monto debe ser un número positivo mayor que 0.
        - plazo debe ser un entero positivo mayor que 0.
        - tipoPrestamo debe existir en el catálogo de tipos de préstamo (ejemplo: PRESTAMO HIPOTECARIO).
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
                            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud")
                    }
            )
    )

    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST(solicitudPath.getSolicitar()), handler::listenCrearSolicitud);
    }
}
