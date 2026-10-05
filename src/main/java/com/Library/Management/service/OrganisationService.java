package com.Library.Management.service;

import com.Library.Management.entity.Organisation;
import com.Library.Management.exception.ResourceNotFoundException;
import com.Library.Management.repository.OrganisationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class OrganisationService {
    private static final Logger log = LoggerFactory.getLogger(OrganisationService.class);
    private final OrganisationRepository organisationRepository;

    public OrganisationService(OrganisationRepository organisationRepository) {
        this.organisationRepository = organisationRepository;
    }
    

    @Transactional
    public Organisation create(String name) {
        log.info("Creating organisation {}", name);
        Organisation organisation = Organisation.builder()
                .name(name)
                .build();
        Organisation saved = organisationRepository.save(organisation);
        log.info("Organisation with details {} created", saved);
        return saved;
    }
    public Long findOrganisationByName(String name) {
        log.info("Looking up organisation {}", name);
        Optional<Organisation> organisation = organisationRepository.findByName(name);
        if (organisation.isPresent()) {
            log.info("found org");
            return organisation.get().getId();
        } else {
               log.warn("Organisation not found with name {}", name);
            throw new ResourceNotFoundException("Organisation not found with name: " + name);
        }
    }

}
