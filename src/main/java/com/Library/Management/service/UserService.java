package com.Library.Management.service;


import com.Library.Management.dto.RegisterRequest;
import com.Library.Management.entity.Organisation;
import com.Library.Management.entity.UserRole;
import com.Library.Management.entity.User;
import com.Library.Management.exception.BusinessException;
import com.Library.Management.repository.UserRepository;
import com.Library.Management.tenant.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrganisationService organisationService;
    private final TransactionTemplate transactionTemplate;
    private final TenantContext tenantContext;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            OrganisationService organisationService,
            TransactionTemplate transactionTemplate,
            TenantContext tenantContext) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.organisationService = organisationService;
        this.transactionTemplate = transactionTemplate;
        this.tenantContext = tenantContext;
    }

    public User register(RegisterRequest request) {
        Long organisationId = null;
        User ret=null;
        try {
            organisationId = organisationService.findOrganisationByName(request.getOrganisationName());
            tenantContext.setTenantId(organisationId);
            log.info("Registering user {} in organisation {}", request.getEmail(), organisationId);

            if (userRepository.existsByEmail(request.getEmail())) {
                log.warn("User already registered by email{}", request.getEmail());
                throw new RuntimeException("Email already registered");
            }
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            User user = User.builder().email(request.getEmail()).
                    password(encodedPassword).
                    userRole(UserRole.ADMIN).
                    username(request.getUsername()).
                    build();
            user.setOrganisationId(organisationId);
            log.info("User with details {}  created", user);
             ret= userRepository.save(user);
        } catch (BusinessException e) {
            log.warn("Organisation not found with name {}", request.getOrganisationName());
            throw new BusinessException("Organisation not found with name: " + request.getOrganisationName());
        }
        catch (Exception e){
           e.printStackTrace();
        }
        return ret;
    }
}
