package com.cebix.swiftcodesapi.parser;

import com.cebix.swiftcodesapi.entity.Country;
import com.cebix.swiftcodesapi.entity.SwiftCode;
import com.cebix.swiftcodesapi.repository.CountryRepository;
import com.cebix.swiftcodesapi.repository.SwiftCodeRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ParserServiceTest {

    private AutoCloseable closeable;

    @InjectMocks
    private ParserService parserService;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private SwiftCodeRepository swiftCodeRepository;

    @Captor
    private ArgumentCaptor<SwiftCode> swiftCodeCaptor;

    @Captor
    private ArgumentCaptor<Country> countryCaptor;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Nested
    class ImportTests {

        @Test
        void shouldImportHeadquarterAndBranchAndCreateCountry() throws Exception {
            Country savedCountry = Country.builder().id(1L).isoCode("AL").name("ALBANIA").build();

            when(countryRepository.findByIsoCode("AL"))
                    .thenReturn(Optional.empty())
                    .thenReturn(Optional.of(savedCountry));

            when(swiftCodeRepository.findBySwiftCode("AAISALTRXXX")).thenReturn(Optional.empty());
            when(swiftCodeRepository.findBySwiftCode("AAISALTR1XX")).thenReturn(Optional.empty());

            when(countryRepository.save(any())).thenReturn(savedCountry);

            var resource = new ClassPathResource("data/test_swift_codes.csv");
            parserService.importDataFromStream(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));

            verify(countryRepository, times(3)).findByIsoCode("AL");
            verify(countryRepository).save(countryCaptor.capture());

            verify(swiftCodeRepository, times(3)).save(swiftCodeCaptor.capture());

            var swiftCodes = swiftCodeCaptor.getAllValues();

            SwiftCode hq = swiftCodes.get(0);
            SwiftCode branch = swiftCodes.get(1);

            assertThat(hq.getSwiftCode()).isEqualTo("AAISALTRXXX");
            assertThat(hq.isHeadquarter()).isTrue();

            assertThat(branch.getSwiftCode()).isEqualTo("AAISALTR1XX");
            assertThat(branch.isHeadquarter()).isFalse();
        }

        @Test
        void shouldSkipInvalidCountryCode() throws Exception {
            var csvWithInvalidISO = """
                COUNTRY ISO2 CODE,SWIFT CODE,CODE TYPE,NAME,ADDRESS,TOWN NAME,COUNTRY NAME,TIME ZONE
                XXX,INVALIDXXX,BIC11,Invalid Bank,Address,Town,Invalidland,Europe/Nowhere
                """;

            var stream = new InputStreamReader(new java.io.ByteArrayInputStream(csvWithInvalidISO.getBytes(StandardCharsets.UTF_8)));

            parserService.importDataFromStream(stream);

            verifyNoInteractions(countryRepository);
            verifyNoInteractions(swiftCodeRepository);
        }
    }

    @Nested
    class ExceptionHandlingTests {

        @Test
        void shouldHandleCsvValidationExceptionGracefully() throws Exception {
            InputStreamReader reader = mock(InputStreamReader.class);
            CSVReader csvReader = mock(CSVReader.class);

            when(csvReader.readNext()).thenThrow(new CsvValidationException("Test exception"));

            ParserService parserServiceSpy = spy(parserService);
            doReturn(csvReader).when(parserServiceSpy).createCsvReader(any());

            assertDoesNotThrow(() -> parserServiceSpy.importDataFromStream(reader));
        }

        @Test
        void shouldHandleIOExceptionDuringImportDataFromStream() throws Exception {
            InputStreamReader reader = mock(InputStreamReader.class);
            CSVReader csvReader = mock(CSVReader.class);

            when(csvReader.readNext()).thenThrow(new IOException("Test IOException during read"));

            ParserService parserServiceSpy = spy(parserService);
            doReturn(csvReader).when(parserServiceSpy).createCsvReader(any());

            assertDoesNotThrow(() -> parserServiceSpy.importDataFromStream(reader));
        }

        @Test
        void shouldHandleIOExceptionGracefully_whenImportingData() throws Exception {
            ClassPathResource resourceMock = mock(ClassPathResource.class);
            when(resourceMock.getInputStream()).thenThrow(new IOException("File not found"));

            ParserService parserServiceSpy = spy(parserService);
            doReturn(resourceMock).when(parserServiceSpy).getClassPathResource(anyString());

            assertDoesNotThrow(parserServiceSpy::importData);
        }
    }

    @Nested
    class SwiftCodeProcessingTests {

        @Test
        void shouldUpdateExistingSwiftCode() throws Exception {
            Country existingCountry = Country.builder().id(1L).isoCode("AL").name("ALBANIA").build();

            SwiftCode existingSwiftCode = SwiftCode.builder()
                    .id(1L)
                    .swiftCode("AAISALTRXXX")
                    .isHeadquarter(false)
                    .country(existingCountry)
                    .build();

            when(countryRepository.findByIsoCode("AL")).thenReturn(Optional.of(existingCountry));
            when(swiftCodeRepository.findBySwiftCode("AAISALTRXXX")).thenReturn(Optional.of(existingSwiftCode));

            var csv = """
                COUNTRY ISO2 CODE,SWIFT CODE,CODE TYPE,NAME,ADDRESS,TOWN NAME,COUNTRY NAME,TIME ZONE
                AL,AAISALTRXXX,BIC11,Updated Bank,New Address,Tirana,ALBANIA,Europe/Tirane
                """;

            var stream = new InputStreamReader(new java.io.ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));

            parserService.importDataFromStream(stream);

            verify(swiftCodeRepository).save(swiftCodeCaptor.capture());
            SwiftCode updatedSwiftCode = swiftCodeCaptor.getValue();

            assertThat(updatedSwiftCode.getBankName()).isEqualTo("Updated Bank");
            assertThat(updatedSwiftCode.getAddress()).isEqualTo("New Address");
        }

        @Test
        void shouldAssignHeadquarterEntityToBranch_usingMapAsDatabase() throws Exception {
            Map<String, SwiftCode> swiftCodeStore = new HashMap<>();

            Country country = Country.builder().isoCode("AL").name("ALBANIA").build();

            when(countryRepository.findByIsoCode("AL")).thenReturn(Optional.of(country));

            when(swiftCodeRepository.save(any())).thenAnswer(invocation -> {
                SwiftCode swiftCode = invocation.getArgument(0);
                if (swiftCode.getId() == null) {
                    swiftCode.setId((long) (swiftCodeStore.size() + 1));
                }
                swiftCodeStore.put(swiftCode.getSwiftCode(), swiftCode);
                return swiftCode;
            });

            when(swiftCodeRepository.findBySwiftCode(anyString())).thenAnswer(invocation -> {
                String code = invocation.getArgument(0);
                return Optional.ofNullable(swiftCodeStore.get(code));
            });

            var resource = new ClassPathResource("data/test_swift_codes.csv");
            parserService.importDataFromStream(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));

            SwiftCode hq = swiftCodeStore.get("AAISALTRXXX");
            SwiftCode branch = swiftCodeStore.get("AAISALTR1XX");

            assertThat(hq).isNotNull();
            assertThat(branch).isNotNull();

            assertThat(hq.isHeadquarter()).isTrue();
            assertThat(branch.isHeadquarter()).isFalse();

            assertThat(branch.getHeadquarterEntity()).isNotNull();
            assertThat(branch.getHeadquarterEntity().getSwiftCode()).isEqualTo("AAISALTRXXX");

            verify(swiftCodeRepository, times(3)).save(any());
        }

        @Test
        void shouldNotAssignHeadquarterEntityToHeadquarter() throws Exception {
            Country country = Country.builder().isoCode("AL").name("ALBANIA").build();

            when(countryRepository.findByIsoCode("AL")).thenReturn(Optional.of(country));
            when(swiftCodeRepository.findBySwiftCode("AAISALTRXXX")).thenReturn(Optional.empty());
            when(swiftCodeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            var resource = new ClassPathResource("data/test_only_headquarter.csv");
            parserService.importDataFromStream(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));

            verify(swiftCodeRepository).save(swiftCodeCaptor.capture());

            SwiftCode savedHq = swiftCodeCaptor.getValue();
            assertThat(savedHq.isHeadquarter()).isTrue();
            assertThat(savedHq.getHeadquarterEntity()).isNull();
        }
    }
}
