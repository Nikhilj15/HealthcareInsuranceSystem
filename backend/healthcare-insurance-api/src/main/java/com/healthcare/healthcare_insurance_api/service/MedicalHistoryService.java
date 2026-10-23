package com.healthcare.healthcare_insurance_api.service;

import com.healthcare.healthcare_insurance_api.dto.MedicalHistoryRequest;
import com.healthcare.healthcare_insurance_api.dto.MedicalHistoryResponse;
import com.healthcare.healthcare_insurance_api.entity.MedicalHistory;
import com.healthcare.healthcare_insurance_api.entity.User;
import com.healthcare.healthcare_insurance_api.enums.Role;
import com.healthcare.healthcare_insurance_api.repository.MedicalHistoryRepository;
import com.healthcare.healthcare_insurance_api.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MedicalHistoryService {

    private final MedicalHistoryRepository medicalHistoryRepository;

    private final UserRepository userRepository;


    public MedicalHistoryService(
            MedicalHistoryRepository medicalHistoryRepository,
            UserRepository userRepository) {

        this.medicalHistoryRepository =
                medicalHistoryRepository;

        this.userRepository =
                userRepository;
    }


    // =========================================================
    // GET CUSTOMER MEDICAL HISTORY
    // =========================================================

    public List<MedicalHistoryResponse> getCustomerMedicalHistory(
            Long customerId) {

        getCustomer(customerId);

        return medicalHistoryRepository
                .findByUserIdOrderByDiagnosisDateDesc(customerId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // =========================================================
    // ADD MEDICAL HISTORY
    // =========================================================

    public MedicalHistoryResponse addMedicalHistory(
            Long customerId,
            MedicalHistoryRequest request) {

        User customer =
                getCustomer(customerId);


        validateRequest(request);


        MedicalHistory history =
                new MedicalHistory();

        history.setUser(customer);

        history.setCondition(
                request.getCondition().trim()
        );

        history.setDiagnosisDate(
                request.getDiagnosisDate()
        );

        history.setTreatment(
                request.getTreatment()
        );

        history.setDoctorName(
                request.getDoctorName()
        );

        history.setHospitalName(
                request.getHospitalName()
        );

        history.setStatus(
                request.getStatus()
        );

        history.setNotes(
                request.getNotes()
        );


        MedicalHistory saved =
                medicalHistoryRepository.save(history);


        return convertToResponse(saved);
    }


    // =========================================================
    // UPDATE MEDICAL HISTORY
    // =========================================================

    public MedicalHistoryResponse updateMedicalHistory(
            Long id,
            MedicalHistoryRequest request) {

        MedicalHistory history =
                medicalHistoryRepository.findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Medical history not found"
                                )
                        );


        validateRequest(request);


        history.setCondition(
                request.getCondition().trim()
        );

        history.setDiagnosisDate(
                request.getDiagnosisDate()
        );

        history.setTreatment(
                request.getTreatment()
        );

        history.setDoctorName(
                request.getDoctorName()
        );

        history.setHospitalName(
                request.getHospitalName()
        );

        history.setStatus(
                request.getStatus()
        );

        history.setNotes(
                request.getNotes()
        );


        MedicalHistory updated =
                medicalHistoryRepository.save(history);


        return convertToResponse(updated);
    }


    // =========================================================
    // DELETE MEDICAL HISTORY
    // =========================================================

    public void deleteMedicalHistory(Long id) {

        MedicalHistory history =
                medicalHistoryRepository.findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Medical history not found"
                                )
                        );


        medicalHistoryRepository.delete(history);
    }


    // =========================================================
    // GET CUSTOMER
    // =========================================================

    private User getCustomer(Long customerId) {

        User user =
                userRepository.findById(customerId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Customer not found"
                                )
                        );


        if (user.getRole() != Role.USER) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Customer not found"
            );
        }


        return user;
    }


    // =========================================================
    // VALIDATE REQUEST
    // =========================================================

    private void validateRequest(
            MedicalHistoryRequest request) {

        if (request == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Medical history request is required"
            );
        }


        if (
                request.getCondition() == null
                        ||
                request.getCondition()
                        .trim()
                        .isEmpty()
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Medical condition is required"
            );
        }
    }


    // =========================================================
    // ENTITY -> RESPONSE
    // =========================================================

    private MedicalHistoryResponse convertToResponse(
            MedicalHistory history) {

        return new MedicalHistoryResponse(

                history.getId(),

                history.getUser().getId(),

                history.getCondition(),

                history.getDiagnosisDate(),

                history.getTreatment(),

                history.getDoctorName(),

                history.getHospitalName(),

                history.getStatus(),

                history.getNotes()
        );
    }
}