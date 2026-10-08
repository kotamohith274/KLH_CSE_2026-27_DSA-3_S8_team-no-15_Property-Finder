package com.example.propertyfinder.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.propertyfinder.algorithms.AhoCorasick;
import com.example.propertyfinder.algorithms.FuzzyMatcher;
import com.example.propertyfinder.algorithms.KmpSearch;
import com.example.propertyfinder.model.Property;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/properties")
public class PropertyController {

    private final List<Property> inventory = new ArrayList<>();
    private final AhoCorasick amenityScanner;

    public PropertyController() {
        // Sample Property Database (Expand this dataset as needed)
        inventory.add(new Property(1, "Skyline Luxury Apartment", "Gachibowli", "3BHK", 12500000, 
            "Spacious flat with swimming pool access, personal gym, power backup, and dedicated parking."));
        inventory.add(new Property(2, "Green Meadows Residency", "Hitech City", "2BHK", 7800000, 
            "Modern aesthetic with private balcony, central garden, modular kitchen, and 24/7 security."));
        inventory.add(new Property(3, "Serene Lakeview Villa", "Kondapur", "Villa", 24000000, 
            "Independent villa with private swimming pool, lawn garden, clubhouse, and multi-car parking."));
        inventory.add(new Property(4, "Urban Heights Flat", "Madhapur", "1BHK", 4200000, 
            "Affordable bachelor studio near metro, includes lift, gym facility, and fast fiber wifi."));
        inventory.add(new Property(5, "Oakwood Elite Penthouse", "Banjara Hills", "3BHK", 18500000, 
            "Penthouse featuring panoramic city views, wide balcony, jacuzzi, club, and smart security."));
        inventory.add(new Property(6, "Silicon Breeze Apartments", "Gachibowli", "2BHK", 8200000, 
            "Ideal for IT professionals. Amenities include gym, terrace garden, visitor parking, and lift."));

        // Keywords for Aho-Corasick multi-pattern scanner
        List<String> amenityKeywords = List.of(
            "swimming pool", "pool", "gym", "balcony", "parking", 
            "garden", "security", "clubhouse", "lift", "jacuzzi"
        );
        this.amenityScanner = new AhoCorasick(amenityKeywords);
    }

    @GetMapping("/search")
    public List<Property> search(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Double maxPrice) {

        return inventory.stream()
            // 1. Fuzzy Matching / Edit Distance on Location (tolerant up to 2 typos)
            .filter(p -> location == null || location.isBlank() || 
                         FuzzyMatcher.isFuzzyMatch(p.getLocation(), location, 2))
            
            // 2. KMP exact matching on title or description
            .filter(p -> keyword == null || keyword.isBlank() || 
                         KmpSearch.search(p.getTitle(), keyword) || 
                         KmpSearch.search(p.getDescription(), keyword))
            
            // 3. Exact filters
            .filter(p -> type == null || type.isBlank() || p.getType().equalsIgnoreCase(type))
            .filter(p -> maxPrice == null || p.getPrice() <= maxPrice)
            
            // 4. Aho-Corasick multi-pattern search to tag amenities automatically
            .map(p -> {
                p.setDetectedAmenities(amenityScanner.search(p.getDescription()));
                return p;
            })
            .collect(Collectors.toList());
    }
}