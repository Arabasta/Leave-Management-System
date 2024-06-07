package com.team4.leaveprocessingsystem.seeder;

import com.team4.leaveprocessingsystem.model.Role;
import com.team4.leaveprocessingsystem.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleSeeder {

    private final RoleRepository roleRepository;

    public RoleSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public void seed() {
        if (roleRepository.count() == 0) {
            Role administrative = new Role("administrative", 14);
            roleRepository.save(administrative);

            Role management = new Role("management", 30);
            roleRepository.save(management);

            Role cleaning = new Role("cleaning", 3);
            roleRepository.save(cleaning);

            Role partTime = new Role("partTime", 0);
            roleRepository.save(partTime);

            Role intern = new Role("intern", 10);
            roleRepository.save(intern);
        }
    }
}
