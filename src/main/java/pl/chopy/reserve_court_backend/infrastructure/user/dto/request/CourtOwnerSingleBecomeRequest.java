package pl.chopy.reserve_court_backend.infrastructure.user.dto.request;

import lombok.Data;

@Data
public class CourtOwnerSingleBecomeRequest {
    private String companyName;
    private String address;
    private String city;
}
