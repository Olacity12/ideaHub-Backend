package com.ideahub.backend.utils;

import org.springframework.stereotype.Component;
import com.ideahub.backend.repository.UserRepository;
import java.util.List;
import java.util.Random;

@Component
public class UsernameGenerator {

    private final UserRepository userRepository;

    public UsernameGenerator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private static final List<String> adjectives = List.of(
            "Quick", "Happy", "Bright", "Calm", "Eager", "Brave", "Clever", "Daring", "Gentle", "Loyal",
            "Mighty", "Proud", "Sly", "Swift", "Wise", "Bold", "Charming", "Fearless", "Noble", "Playful"
    );

    private static final List<String> nouns = List.of(
            "Tiger", "Eagle", "Lion", "Bear", "Shark", "Wolf", "Falcon", "Panther", "Hawk", "Fox",
            "Cheetah", "Puma", "Rhino", "Stallion", "Jaguar", "Otter", "Phoenix", "Griffin", "Leopard", "Cougar"
    );


    public String generateUniqueUsername() {
        Random random = new Random();
        String username;
        do {
            String adjective = adjectives.get(random.nextInt(adjectives.size()));
            String noun = nouns.get(random.nextInt(nouns.size()));
            int number = random.nextInt(1000); // Adjust as needed for more variation

            username = (adjective + noun + number).toLowerCase();
        } while (userRepository.existsByUsername(username));

        return username;
    }
}
