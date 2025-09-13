package co.crediyacorp.api.dtos;

import co.crediyacorp.model.solicitud.Solicitud;

import java.math.BigDecimal;
import java.util.List;

public record DatosTransformacion(BigDecimal salarioBase, List<Solicitud> solicitudesAprobadas) {}
