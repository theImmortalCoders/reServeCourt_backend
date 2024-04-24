package pl.chopy.reserve_court_backend.infrastructure.reservation.dto;

import org.mapstruct.Mapper;
import pl.chopy.reserve_court_backend.infrastructure.reservation.dto.response.ReservationShortResponse;
import pl.chopy.reserve_court_backend.infrastructure.reservation.dto.response.ReservationSingleResponse;
import pl.chopy.reserve_court_backend.model.entity.Reservation;

@Mapper
public interface ReservationMapper {
	Reservation map(ReservationSingleRequest request);

	ReservationShortResponse shortMap(Reservation request);

	ReservationSingleResponse map(Reservation request);
}
