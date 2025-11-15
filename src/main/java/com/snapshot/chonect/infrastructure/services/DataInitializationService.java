package com.snapshot.chonect.infrastructure.services;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.snapshot.chonect.domain.models.CountryEntity;
import com.snapshot.chonect.domain.models.LanguageEntity;
import com.snapshot.chonect.domain.models.UserEntity;
import com.snapshot.chonect.domain.repositories.CountryRepository;
import com.snapshot.chonect.domain.repositories.LanguageRepository;
import com.snapshot.chonect.domain.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DataInitializationService implements CommandLineRunner {

    private final CountryRepository countryRepository;

    private final LanguageRepository languageRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeCountries();
        initializeLanguages();
        encryptExistingPlainTextPasswords();
    }
    @SuppressWarnings("null")
    private void initializeCountries() {
        if (countryRepository.count() == 0) {
            // América del Norte
            countryRepository.save(CountryEntity.builder().name("Canadá").code("CAN").build());
            countryRepository.save(CountryEntity.builder().name("Estados Unidos").code("USA").build());
            countryRepository.save(CountryEntity.builder().name("México").code("MEX").build());

            // América Central y Caribe
            countryRepository.save(CountryEntity.builder().name("Costa Rica").code("CRI").build());
            countryRepository.save(CountryEntity.builder().name("Cuba").code("CUB").build());
            countryRepository.save(CountryEntity.builder().name("Guatemala").code("GTM").build());
            countryRepository.save(CountryEntity.builder().name("Honduras").code("HND").build());
            countryRepository.save(CountryEntity.builder().name("Nicaragua").code("NIC").build());
            countryRepository.save(CountryEntity.builder().name("Panamá").code("PAN").build());
            countryRepository.save(CountryEntity.builder().name("El Salvador").code("SLV").build());
            countryRepository.save(CountryEntity.builder().name("República Dominicana").code("DOM").build());
            countryRepository.save(CountryEntity.builder().name("Puerto Rico").code("PRI").build());

            // América del Sur
            countryRepository.save(CountryEntity.builder().name("Argentina").code("ARG").build());
            countryRepository.save(CountryEntity.builder().name("Bolivia").code("BOL").build());
            countryRepository.save(CountryEntity.builder().name("Brasil").code("BRA").build());
            countryRepository.save(CountryEntity.builder().name("Chile").code("CHL").build());
            countryRepository.save(CountryEntity.builder().name("Colombia").code("COL").build());
            countryRepository.save(CountryEntity.builder().name("Ecuador").code("ECU").build());
            countryRepository.save(CountryEntity.builder().name("Paraguay").code("PRY").build());
            countryRepository.save(CountryEntity.builder().name("Perú").code("PER").build());
            countryRepository.save(CountryEntity.builder().name("Uruguay").code("URY").build());
            countryRepository.save(CountryEntity.builder().name("Venezuela").code("VEN").build());

            // Europa
            countryRepository.save(CountryEntity.builder().name("Alemania").code("DEU").build());
            countryRepository.save(CountryEntity.builder().name("Austria").code("AUT").build());
            countryRepository.save(CountryEntity.builder().name("Bélgica").code("BEL").build());
            countryRepository.save(CountryEntity.builder().name("Dinamarca").code("DNK").build());
            countryRepository.save(CountryEntity.builder().name("España").code("ESP").build());
            countryRepository.save(CountryEntity.builder().name("Finlandia").code("FIN").build());
            countryRepository.save(CountryEntity.builder().name("Francia").code("FRA").build());
            countryRepository.save(CountryEntity.builder().name("Grecia").code("GRC").build());
            countryRepository.save(CountryEntity.builder().name("Irlanda").code("IRL").build());
            countryRepository.save(CountryEntity.builder().name("Italia").code("ITA").build());
            countryRepository.save(CountryEntity.builder().name("Noruega").code("NOR").build());
            countryRepository.save(CountryEntity.builder().name("Países Bajos").code("NLD").build());
            countryRepository.save(CountryEntity.builder().name("Polonia").code("POL").build());
            countryRepository.save(CountryEntity.builder().name("Portugal").code("PRT").build());
            countryRepository.save(CountryEntity.builder().name("Reino Unido").code("GBR").build());
            countryRepository.save(CountryEntity.builder().name("Suecia").code("SWE").build());
            countryRepository.save(CountryEntity.builder().name("Suiza").code("CHE").build());

            // Asia
            countryRepository.save(CountryEntity.builder().name("China").code("CHN").build());
            countryRepository.save(CountryEntity.builder().name("Corea del Sur").code("KOR").build());
            countryRepository.save(CountryEntity.builder().name("Filipinas").code("PHL").build());
            countryRepository.save(CountryEntity.builder().name("India").code("IND").build());
            countryRepository.save(CountryEntity.builder().name("Indonesia").code("IDN").build());
            countryRepository.save(CountryEntity.builder().name("Japón").code("JPN").build());
            countryRepository.save(CountryEntity.builder().name("Malasia").code("MYS").build());
            countryRepository.save(CountryEntity.builder().name("Singapur").code("SGP").build());
            countryRepository.save(CountryEntity.builder().name("Tailandia").code("THA").build());
            countryRepository.save(CountryEntity.builder().name("Turquía").code("TUR").build());
            countryRepository.save(CountryEntity.builder().name("Vietnam").code("VNM").build());

            // Oceanía
            countryRepository.save(CountryEntity.builder().name("Australia").code("AUS").build());
            countryRepository.save(CountryEntity.builder().name("Nueva Zelanda").code("NZL").build());

            // África
            countryRepository.save(CountryEntity.builder().name("Egipto").code("EGY").build());
            countryRepository.save(CountryEntity.builder().name("Marruecos").code("MAR").build());
            countryRepository.save(CountryEntity.builder().name("Sudáfrica").code("ZAF").build());
            countryRepository.save(CountryEntity.builder().name("Túnez").code("TUN").build());

            // Oriente Medio
            countryRepository.save(CountryEntity.builder().name("Arabia Saudita").code("SAU").build());
            countryRepository.save(CountryEntity.builder().name("Emiratos Árabes Unidos").code("ARE").build());
            countryRepository.save(CountryEntity.builder().name("Israel").code("ISR").build());

            // Europa del Este
            countryRepository.save(CountryEntity.builder().name("Rusia").code("RUS").build());
            countryRepository.save(CountryEntity.builder().name("Ucrania").code("UKR").build());

            // América adicional
            countryRepository.save(CountryEntity.builder().name("Jamaica").code("JAM").build());
            countryRepository.save(CountryEntity.builder().name("Trinidad y Tobago").code("TTO").build());
        }
    }

    @SuppressWarnings("null")
    private void initializeLanguages() {
        if (languageRepository.count() == 0) {
            // Idiomas más hablados globalmente
            languageRepository.save(LanguageEntity.builder().name("Español").code("es").build());
            languageRepository.save(LanguageEntity.builder().name("Inglés").code("en").build());
            languageRepository.save(LanguageEntity.builder().name("Francés").code("fr").build());
            languageRepository.save(LanguageEntity.builder().name("Portugués").code("pt").build());
            languageRepository.save(LanguageEntity.builder().name("Italiano").code("it").build());
            languageRepository.save(LanguageEntity.builder().name("Alemán").code("de").build());

            // Idiomas adicionales importantes
            languageRepository.save(LanguageEntity.builder().name("Chino Mandarín").code("zh").build());
            languageRepository.save(LanguageEntity.builder().name("Japonés").code("ja").build());
            languageRepository.save(LanguageEntity.builder().name("Coreano").code("ko").build());
            languageRepository.save(LanguageEntity.builder().name("Árabe").code("ar").build());
            languageRepository.save(LanguageEntity.builder().name("Hindi").code("hi").build());
            languageRepository.save(LanguageEntity.builder().name("Ruso").code("ru").build());
            languageRepository.save(LanguageEntity.builder().name("Holandés").code("nl").build());
            languageRepository.save(LanguageEntity.builder().name("Sueco").code("sv").build());
            languageRepository.save(LanguageEntity.builder().name("Noruego").code("no").build());
            languageRepository.save(LanguageEntity.builder().name("Danés").code("da").build());
            languageRepository.save(LanguageEntity.builder().name("Polaco").code("pl").build());
            languageRepository.save(LanguageEntity.builder().name("Turco").code("tr").build());
            languageRepository.save(LanguageEntity.builder().name("Griego").code("el").build());
            languageRepository.save(LanguageEntity.builder().name("Hebreo").code("he").build());
        }
    }

    @SuppressWarnings("null")
    private void encryptExistingPlainTextPasswords() {
        // Buscar usuarios cuyas contraseñas no estén encriptadas (no empiecen con $2)
        var usersWithPlainTextPasswords = userRepository.findAll().stream()
                .filter(user -> user.getPassword() != null && !user.getPassword().startsWith("$2"))
                .toList();

        if (!usersWithPlainTextPasswords.isEmpty()) {
            System.out.println("Encriptando " + usersWithPlainTextPasswords.size() + " contraseñas de usuarios existentes...");

            for (UserEntity user : usersWithPlainTextPasswords) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userRepository.save(user);
            }

            System.out.println("Encriptación de contraseñas existente completada.");
        } else {
            System.out.println("No se encontraron contraseñas sin encriptar.");
        }
    }
}
