package com.trend.ozitre.service.impl;

import com.trend.ozitre.advice.UserNotFound;
import com.trend.ozitre.dto.TeachersDto;
import com.trend.ozitre.entity.*;
import com.trend.ozitre.enums.Role;
import com.trend.ozitre.repository.CompanyRepository;
import com.trend.ozitre.repository.RoleRepository;
import com.trend.ozitre.repository.TeachersRepository;
import com.trend.ozitre.repository.UserRepository;
import com.trend.ozitre.service.TeachersService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeachersServiceImpl implements TeachersService {

    private final TeachersRepository teachersRepository;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final CompanyRepository companyRepository;

    private final ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<TeachersDto> getTeachers(Long companyId, Long seasonId) {
        List<TeacherEntity> teacherEntities = teachersRepository.findByCompanyIdAndSeasonIdOrderByTeacherIdAsc(companyId, seasonId);
        return teacherEntities.stream().map(teacher -> modelMapper.map(teacher, TeachersDto.class)).collect(Collectors.toList());
    }

    @Override
    public TeacherEntity getTeacher(Long id) {
        Optional<TeacherEntity> teacherEntity = teachersRepository.findById(id);

        if(teacherEntity.isPresent()) {
            return teacherEntity.get();
        }
        throw new UserNotFound("Öğretmen Bulunamadı!");
    }

    @Override
    public List<TeachersDto> getTeacherByState(Integer state, Long companyId) {
        List<TeacherEntity> teacherEntities = teachersRepository.findByCompanyIdAndTeacherStateEquals(companyId, state);
        return teacherEntities.stream().map(teacher -> modelMapper.map(teacher, TeachersDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<TeachersDto> getTeacherByLessonId(Long lessonId, Long companyId) {
        List<TeacherEntity> teacherEntities = teachersRepository.findByLessons_LessonIdAndTeacherStateAndCompanyId(lessonId, 0, companyId);
        return teacherEntities.stream().map(teacher -> modelMapper.map(teacher, TeachersDto.class)).collect(Collectors.toList());
    }

    @Override
    public TeachersDto saveTeacher(TeachersDto teachersDto, String username, Long companyId) {
        Optional<TeacherEntity> teacherEntity = Optional.empty();
        if (teachersDto.getTeacherId() != null) teacherEntity = teachersRepository.findById(teachersDto.getTeacherId());
        teachersDto.setUsername(formatForUsername(teachersDto.getTeacherName()) + "." + formatForUsername(teachersDto.getTeacherSurname()));
        teachersDto.setTeacherName(StringUtils.capitalize(teachersDto.getTeacherName()));
        teachersDto.setTeacherSurname(StringUtils.capitalize(teachersDto.getTeacherSurname()));
        TeacherEntity teacher = modelMapper.map(teachersDto, TeacherEntity.class);
        teacher.setCompanyId(companyId);

        if (teacherEntity.isPresent()) {
            teacher.setCreatedBy(teacherEntity.get().getCreatedBy());
            teacher.setCreatedDate(teacherEntity.get().getCreatedDate());
            teacher.setUpdatedDate(new Date());
            teacher.setUpdatedBy(username);
        } else {
            teacher.setCreatedDate(new Date());
            teacher.setCreatedBy(username);
            saveUser(teacher);
        }
        return modelMapper.map(teachersRepository.save(teacher), TeachersDto.class);
    }

    private void saveUser(TeacherEntity teacher) {
        UserEntity user = new UserEntity();
        user.setUsername(teacher.getUsername());
        user.setPassword(passwordEncoder.encode("1"));
        user.setUserAddress(teacher.getTeacherAddress());
        user.setUserMail(teacher.getTeacherMail());
        user.setUserPhoneNumber(teacher.getTeacherPhoneNumber());
        user.setFirstName(teacher.getTeacherName());
        user.setLastName(teacher.getTeacherSurname());
        Set<RoleEntity> roleSet = new HashSet<>();
        RoleEntity userRole = roleRepository.findByName(Role.TEACHER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roleSet.add(userRole);
        user.setRoles(roleSet);

        Set<CompanyEntity> companySet = new HashSet<>();
        Optional<CompanyEntity> company = companyRepository.findById(teacher.getCompanyId());
        companySet.add(company.get());
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
