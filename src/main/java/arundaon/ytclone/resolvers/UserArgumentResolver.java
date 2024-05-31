package arundaon.ytclone.resolvers;

import arundaon.ytclone.entities.User;
import arundaon.ytclone.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    private UserRepository userRepository;

    public UserArgumentResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return User.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest servletRequest =(HttpServletRequest)webRequest.getNativeRequest();
        String token = servletRequest.getHeader("X-API-TOKEN");
        if(token == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unauthorized");
        }

        User user = userRepository.findFirstByToken(token)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unauthorized"));

        if(user.getExpiredAt() < System.currentTimeMillis()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unauthorized");
        }
        return user;
    }
}
