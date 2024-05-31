package arundaon.ytclone.services;

import arundaon.ytclone.models.RegisterUserRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ValidationService {

    private Validator validator;

    public ValidationService(Validator validator) {
        this.validator = validator;
    }

    public void validate(Object request){
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(request);
        if(!constraintViolations.isEmpty()){
            throw new ConstraintViolationException(constraintViolations);
        }
    }
}
