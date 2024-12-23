package com.trend.ozitre.service.impl;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;
import com.trend.ozitre.advice.UserNotFound;
import com.trend.ozitre.dto.EventsDto;
import com.trend.ozitre.dto.StudentsDto;
import com.trend.ozitre.entity.*;
import com.trend.ozitre.enums.Role;
import com.trend.ozitre.model.HeaderDetails;
import com.trend.ozitre.model.PackageTableHeader;
import com.trend.ozitre.model.ProductTableHeader;
import com.trend.ozitre.repository.*;
import com.trend.ozitre.service.EventsService;
import com.trend.ozitre.service.RegistryPdfCreatorService;
import com.trend.ozitre.service.StudentsService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentsServiceImpl implements StudentsService {

    private final StudentsRepository studentsRepository;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final CompanyRepository companyRepository;

    private final EventsRepository eventsRepository;

    private final EventsService eventsService;

    private final ModelMapper modelMapper;

    @Autowired
    private RegistryPdfCreatorService registryPdfCreatorService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<StudentsDto> getStudents(Long companyId) {
        List<StudentsEntity> students = studentsRepository.findByCompanyIdOrderByStudentIdAsc(companyId);
        return students.stream().map(student -> modelMapper.map(student, StudentsDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<StudentsDto> getStudentsByRegState(Integer regState, Long companyId) {
        List<StudentsEntity> students = studentsRepository.findByCompanyIdAndRegStateEquals(companyId, regState);
        return students.stream().map(student -> modelMapper.map(student, StudentsDto.class)).collect(Collectors.toList());
    }

    @Override
    public StudentsDto getStudent(Long id) {
        Optional<StudentsEntity> studentsEntity = studentsRepository.findById(id);

        if(studentsEntity.isPresent()) {
            return modelMapper.map(studentsEntity.get(), StudentsDto.class);
        }
        throw new UserNotFound("Kullanıcı Bulunamadı!");
    }

    @Override
    public StudentsDto saveStudent(StudentsDto studentsDto, String username, Long companyId) {
        Optional<StudentsEntity> students = Optional.empty();
        if (studentsDto.getStudentId() != null) students = studentsRepository.findById(studentsDto.getStudentId());
        studentsDto.setUsername(formatForUsername(studentsDto.getName()) + "." + formatForUsername(studentsDto.getSurname()));
        studentsDto.setName(StringUtils.capitalize(studentsDto.getName()));
        studentsDto.setSurname(StringUtils.capitalize(studentsDto.getSurname()));
        studentsDto.setParent(StringUtils.capitalize(studentsDto.getParent()));
        studentsDto.setAddress(StringUtils.capitalize(studentsDto.getAddress()));
        studentsDto.setSchool(StringUtils.capitalize(studentsDto.getSchool()));
        StudentsEntity student = modelMapper.map(studentsDto, StudentsEntity.class);
        student.setCompanyId(companyId);

        if (students.isPresent()) {
            student.setCreatedBy(students.get().getCreatedBy());
            student.setCreatedDate(students.get().getCreatedDate());
            student.setUpdatedDate(new Date());
            student.setUpdatedBy(username);
            student = studentsRepository.save(student);
        } else {
            student.setCreatedDate(new Date());
            student.setCreatedBy(username);
            saveUser(student);
            student = studentsRepository.save(student);
            if (studentsDto.getPackageId() != null) {
                int month = student.getStartMonth();
                LocalDate localDate = student.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                int year = localDate.getYear();
                LocalDate givenDate = LocalDate.of(year, month, 1);
                LocalDate currentDate = LocalDate.now();

                if (currentDate.isAfter(givenDate)) {
                    EventsDto event = new EventsDto();
                    event.setStudentId(student.getStudentId());
                    event.setEventStatus(true);
                    event.setDate(Date.from(givenDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    event.setTitle("Paket Dersi Düzenli");
                    BigDecimal price = BigDecimal.valueOf(student.getTotalPrice() / student.getInstallment());
                    Optional<EventsEntity> isEvent = eventsRepository.findByStudentIdAndDateAndEventStatusAndTitle(event.getStudentId(), event.getDate(), event.getEventStatus(), event.getTitle());
                    if (isEvent.isEmpty()) {
                        eventsService.addEventNew(event, price, student.getCreatedBy(), student.getCompanyId());
                    }
                }
            }
        }
        return modelMapper.map(student, StudentsDto.class);
    }

    @Override
    public Boolean removeStudent(Long id) {
        Optional<StudentsEntity> student = studentsRepository.findById(id);

        if(student.isPresent()) {
            studentsRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Long getSizeOfStudents(Long companyId) {
        return studentsRepository.countByCompanyIdAndRegState(companyId, 0);
    }

    @Override
    public byte[] getRegistryPdf(Long studentId) throws IOException, URISyntaxException {
        URL res = getClass().getResource("/trendders-logo-xl.png");
        assert res != null;

        String imagePath;
        try (InputStream inputStream = res.openStream()) {
            File file = File.createTempFile("trendders-logo", ".png");
            file.deleteOnExit();
            Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            imagePath = file.getAbsolutePath();
        }

        registryPdfCreatorService.createDocument();

        // Create Header start
        HeaderDetails header = new HeaderDetails();

        // Image nesnesini oluştur
        Image companyLogo = new Image(ImageDataFactory.create(new File(imagePath).toURL()));

        companyLogo.scale(0.4F, 0.4F);
        header.setImageBanner(companyLogo);
        header.setInvoiceDate(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        registryPdfCreatorService.createHeader(header);
        //Header End
        StudentsEntity studentsEntity = studentsRepository.getReferenceById(studentId);

        //Create Address start
        registryPdfCreatorService.createAddress(studentsEntity);
        //Address end


        if (studentsEntity.getPackageId() != null) {
            PackageTableHeader packageTableHeader = new PackageTableHeader();
            registryPdfCreatorService.createTableHeader(packageTableHeader);

            registryPdfCreatorService.createEvent(studentsEntity);
        } else {
            ProductTableHeader productTableHeader = new ProductTableHeader();
            registryPdfCreatorService.createTableHeader(productTableHeader);

            registryPdfCreatorService.createEvent(studentsEntity);
        }

        //Term and Condition Start
        List<String> TncList = new ArrayList<>();
        TncList.add("****** Mali Değeri Yoktur ******");
        //TncList.add("2. The Seller warrants the product for one (1) year from the date of shipment");
        registryPdfCreatorService.createTnc(TncList, false, imagePath);

        // Term and condition end
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Path source = Paths.get("yeniPdfName.pdf");
        Files.copy(source, outputStream);
        Files.delete(source);

        return outputStream.toByteArray();
    }

    private void saveUser(StudentsEntity student) {
        UserEntity user = new UserEntity();
        user.setUsername(student.getUsername());
        user.setPassword(passwordEncoder.encode("1"));
        user.setUserAddress(student.getAddress());
        user.setUserMail(student.getMail());
        user.setUserPhoneNumber(student.getPhoneNumber());
        user.setFirstName(student.getName());
        user.setLastName(student.getSurname());
        Set<RoleEntity> roleSet = new HashSet<>();
        RoleEntity userRole = roleRepository.findByName(Role.STUDENT)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roleSet.add(userRole);
        user.setRoles(roleSet);

        Set<CompanyEntity> companySet = new HashSet<>();
        Optional<CompanyEntity> userCompany = companyRepository.findById(student.getCompanyId());
        companySet.add(userCompany.get());
        user.setCompanies(companySet);

        userRepository.save(user);
    }

    private String formatForUsername(String str) {
        str = str.replaceAll("\\s+","");
        str = str.toLowerCase();
        String ret = str;
        char[] turkishChars = new char[] {0x131, 0x130, 0xFC, 0xDC, 0xF6, 0xD6, 0x15F, 0x15E, 0xE7, 0xC7, 0x11F, 0x11E};
        char[] englishChars = new char[] {'i', 'I', 'u', 'U', 'o', 'O', 's', 'S', 'c', 'C', 'g', 'G'};
        for (int i = 0; i < turkishChars.length; i++) {
            ret = ret.replaceAll(new String(new char[]{turkishChars[i]}), new String(new char[]{englishChars[i]}));
        }
        return ret;
    }
}
