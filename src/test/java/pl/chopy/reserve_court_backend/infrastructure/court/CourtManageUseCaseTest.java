package pl.chopy.reserve_court_backend.infrastructure.court;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import pl.chopy.reserve_court_backend.infrastructure.club.ClubUtil;
import pl.chopy.reserve_court_backend.infrastructure.court.dto.CourtMapper;
import pl.chopy.reserve_court_backend.infrastructure.court.dto.CourtMapperImpl;
import pl.chopy.reserve_court_backend.infrastructure.court.dto.CourtSingleRequest;
import pl.chopy.reserve_court_backend.infrastructure.image.ImageUtil;
import pl.chopy.reserve_court_backend.model.entity.Club;
import pl.chopy.reserve_court_backend.model.entity.Court;
import pl.chopy.reserve_court_backend.model.entity.Image;
import pl.chopy.reserve_court_backend.model.entity.repository.CourtRepository;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CourtManageUseCaseTest {
    private Court court = new Court();
    private Club club = new Club();
    private Image image = new Image();
    private final CourtRepository courtRepository = mock(CourtRepository.class);
    @Spy
    private CourtUtil courtUtil = new CourtUtil(courtRepository);
    @Mock
    private ClubUtil clubUtil;
    @Spy
    private final CourtMapper courtMapper = new CourtMapperImpl();
    @Mock
    private ImageUtil imageUtil;
    @InjectMocks
    private CourtManageUseCase courtManageUseCase;

    @BeforeEach
    public void setUp() {
        court.setId(1L);

        image.setId(1L);

        when(imageUtil.getImagesByIds(new ArrayList<>(List.of(1L)))).thenReturn(new ArrayList<>(List.of(image)));
        when(clubUtil.getById(1L)).thenReturn(club);
        when(courtRepository.save(any(Court.class))).thenReturn(court);
    }

    @Test
    public void shouldAddCourt() {
        var request = new CourtSingleRequest();
        request.setImagesIds(new ArrayList<>(List.of(1L)));

        courtManageUseCase.add(1L, request);

        verify(courtRepository, times(1)).save(any(Court.class));
        verify(clubUtil, times(1)).save(any(Club.class));
    }
}
