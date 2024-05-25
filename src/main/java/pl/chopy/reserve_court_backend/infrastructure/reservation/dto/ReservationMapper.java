package pl.chopy.reserve_court_backend.infrastructure.reservation.dto;

import org.mapstruct.Mapper;
import pl.chopy.reserve_court_backend.infrastructure.court.dto.CourtMapperImpl;
import pl.chopy.reserve_court_backend.infrastructure.image.dto.ImageMapperImpl;
import pl.chopy.reserve_court_backend.infrastructure.reservation.dto.response.ReservationShortResponse;
import pl.chopy.reserve_court_backend.infrastructure.reservation.dto.response.ReservationSingleResponse;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.UserMapperImpl;
import pl.chopy.reserve_court_backend.infrastructure.user.dto.response.UserShortResponse;
import pl.chopy.reserve_court_backend.model.entity.Reservation;

@Mapper
public interface ReservationMapper {
	Reservation map(ReservationSingleRequest request);

	default ReservationShortResponse shortMap(Reservation request){
		var response = new ReservationShortResponse();
		response.setId(request.getId());
		response.setConfirmed(request.isConfirmed());
		response.setCanceled(request.isCanceled());
		response.setReservedByOwner(request.isReservedByOwner());
		response.setTimeFrom(request.getTimeFrom());
		response.setTimeTo(request.getTimeTo());

		updateShortCourt(request, response);

		return response;
	}


	default ReservationSingleResponse map(Reservation request){
		var response = new ReservationSingleResponse();
		response.setId(request.getId());
		response.setConfirmed(request.isConfirmed());
		response.setCanceled(request.isCanceled());
		response.setReservedByOwner(request.isReservedByOwner());
		response.setTimeFrom(request.getTimeFrom());
		response.setTimeTo(request.getTimeTo());
		response.setMessage(request.getMessage());

		var userMapper = new UserMapperImpl();
		response.setBooker(userMapper.shortMap(request.getBooker()));
		updateFullCourt(request, response);

		return response;
	}

	private static void updateShortCourt(Reservation request, ReservationShortResponse response) {
		var courtMapper = new CourtMapperImpl();
		var imageMapper = new ImageMapperImpl();
		var courtResponse = courtMapper.shortMap(request.getCourt());
		if (!request.getCourt().getImages().isEmpty()) {
			courtResponse.setImage(imageMapper.map(
					request.getCourt().getImages().get(0)
			));
		}
		courtResponse.setClubId(request.getCourt().getClub().getId());
		response.setCourt(courtResponse);
	}

	private static void updateFullCourt(Reservation request, ReservationSingleResponse response) {
		var courtMapper = new CourtMapperImpl();
		var imageMapper = new ImageMapperImpl();
		var courtResponse = courtMapper.shortMap(request.getCourt());
		if (!request.getCourt().getImages().isEmpty()) {
			courtResponse.setImage(imageMapper.map(
					request.getCourt().getImages().get(0)
			));
		}
		courtResponse.setClubId(request.getCourt().getClub().getId());
		response.setCourt(courtResponse);
	}
}
